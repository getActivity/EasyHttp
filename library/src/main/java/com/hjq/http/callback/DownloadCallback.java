package com.hjq.http.callback;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.exception.FileMd5Exception;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.CallProxy;
import com.hjq.http.request.HttpRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 下载接口回调
 */
public final class DownloadCallback extends BaseCallback {

    /** 请求配置 */
    @NonNull
    private final HttpRequest<?> mHttpRequest;

    /** 文件 MD5 正则表达式 */
    private static final String FILE_MD5_REGEX = "^[\\w]{32}$";

    /** 保存的文件 */
    private File mFile;

    /** 校验的 MD5 */
    private String mMd5;

    /** 下载监听回调 */
    private OnDownloadListener mListener;

    /** 下载总字节 */
    private final AtomicLong mTotalByte = new AtomicLong();

    /** 已下载字节 */
    private final AtomicLong mDownloadByte = new AtomicLong();

    /** 下载进度 */
    private int mDownloadProgress;

    /** 断点续传开关 */
    private boolean mResumableTransfer;

    public DownloadCallback(@NonNull HttpRequest<?> request) {
        super(request);
        mHttpRequest = request;
    }

    public DownloadCallback setFile(File file) {
        mFile = file;
        return this;
    }

    public DownloadCallback setMd5(String md5) {
        mMd5 = md5;
        return this;
    }

    public DownloadCallback setListener(OnDownloadListener listener) {
        mListener = listener;
        return this;
    }

    public DownloadCallback setResumableTransfer(boolean resumableTransfer) {
        mResumableTransfer = resumableTransfer;
        return this;
    }

    @Override
    protected void onStart() {
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchDownloadStartCallback);
    }

    @Override
    protected void onHttpResponse(Response response) throws Throwable {
        // 打印请求耗时时间
        EasyLog.printLog(mHttpRequest, "RequestConsuming：" +
                (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + " ms");

        // 响应码 416 表示请求的范围不符合要求，这通常发生在使用断点续传（Range请求头）时，服务器无法满足请求的范围条件，造成这个问题的原因可能有以下几种：
        // 1. 范围请求错误：请求头中的Range字段可能设置了无效的范围。请确保你设置的范围是有效的，且在文件范围内
        // 2. 服务器不支持范围请求：有些服务器不支持范围请求，尤其是对于静态文件服务。在这种情况下，服务器可能会返回 416 错误
        // 3. 服务器不允许断点续传：即使服务器支持范围请求，也可能配置为不允许断点续传。这可能是出于性能或其他原因的考虑
        if (response.code() == 416 && !TextUtils.isEmpty(response.request().header("Range"))) {
            Request request = response.request().newBuilder().removeHeader("Range").build();
            CallProxy callProxy = getCallProxy();
            Call newCall = mHttpRequest.getRequestClient().getOkHttpClient().newCall(request);
            callProxy.setRealCall(newCall);
            Response newResponse = callProxy.execute();
            // 打印请求耗时时间
            EasyLog.printLog(mHttpRequest, "The response status code is 416" +
                ", response message: " + response.message() + ", require special treatment" +
                ", re-initiate a new request，new request consuming：" +
                (newResponse.receivedResponseAtMillis() - newResponse.sentRequestAtMillis()) + " ms");
            // 替换之前的 Response 对象
            response = newResponse;
        }

        IRequestInterceptor interceptor = mHttpRequest.getRequestInterceptor();
        if (interceptor != null) {
            response = interceptor.interceptResponse(mHttpRequest, response);
        }

        if (!response.isSuccessful())  {
            throw new ResponseException("The request failed, response code: " +
                response.code() + ", response message: " + response.message(), response);
        }

        // 如果没有指定文件的 md5 值
        if (mMd5 == null) {
            // 获取响应头中的文件 MD5 值
            String md5 = response.header("Content-MD5");
            // 这个 md5 值必须是文件的 md5 值
            if (md5 != null && md5.matches(FILE_MD5_REGEX)) {
                mMd5 = md5;
            }
        }

        // 如果这个文件已经下载过，并且经过校验 MD5 是同一个文件的话，就直接回调下载成功监听
        if (verifyFileMd5()) {
            // 文件已存在，跳过下载
            EasyLog.printLog(mHttpRequest, mFile.getPath() + " download file already exists, skip request");
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchDownloadSuccessCallback(true));
            return;
        }

        // 当前是否支持断点续传
        boolean supportResumableTransfer = false;
        long fileLength = mFile.length();
        // 当前必须开启了断点续传的开关
        if (mResumableTransfer && response.code() == 206 && fileLength > 0) {
            // 获取 Accept-Ranges 字段的值
            String acceptRanges = response.header("Accept-Ranges");
            String contentRange = response.header("Content-Range");
            // 若能够找到 Content-Range，则表明服务器支持断点续传
            // 有些服务器还会返回 Accept-Ranges，输出结果 Accept-Ranges: bytes，说明服务器支持按字节下载
            if (acceptRanges != null && !"".equals(acceptRanges)) {
                // Accept-Ranges：bytes
                supportResumableTransfer = "bytes".equalsIgnoreCase(acceptRanges);
            } else if (contentRange != null && !"".equals(contentRange)) {
                // Content-Range: bytes 7897088-221048888/221048889
                supportResumableTransfer = contentRange.matches("bytes\\s+\\d+-\\d+/\\d+");
            }
        }

        File parentFile = mFile.getParentFile();
        if (parentFile != null) {
            EasyUtils.createFolder(parentFile);
        }
        ResponseBody body = response.body();
        if (body == null) {
            throw new NullBodyException("The response body is empty");
        }

        mTotalByte.set(body.contentLength());
        if (mTotalByte.get() < 0) {
            mTotalByte.set(0);
        }

        int readLength;
        mDownloadByte.set(0);
        byte[] bytes = new byte[8192];
        InputStream responeInputStream = body.byteStream();

        OutputStream fileOutputStream = EasyUtils.openFileOutputStream(mFile, supportResumableTransfer);
        if (supportResumableTransfer) {
            mDownloadByte.addAndGet(fileLength);
            // 有一些响应头没有返回 Content-Length 请求头，会导致总字节数为 0
            if (mTotalByte.get() > 0) {
                mTotalByte.addAndGet(fileLength);
            }
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchDownloadByteChangeCallback);
        }

        while ((readLength = responeInputStream.read(bytes)) != -1) {
            mDownloadByte.addAndGet(readLength);
            fileOutputStream.write(bytes, 0, readLength);
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchDownloadByteChangeCallback);
        }

        // 刷新 IO 缓冲区
        fileOutputStream.flush();

        EasyUtils.closeStream(responeInputStream);
        EasyUtils.closeStream(fileOutputStream);
        EasyUtils.closeStream(response);

        String md5 = EasyUtils.getFileMd5(EasyUtils.openFileInputStream(mFile));
        if (mMd5 != null && !"".equals(mMd5) && !mMd5.equalsIgnoreCase(md5)) {
            // 文件 MD5 值校验失败
            throw new FileMd5Exception("File md5 hash verify failure", md5);
        }

        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchDownloadSuccessCallback(false));
    }

    @Override
    protected void onHttpFailure(final Throwable throwable) {
        EasyLog.printThrowable(mHttpRequest, throwable);
        // 打印错误堆栈
        final Throwable finalThrowable = mHttpRequest.getRequestHandler().downloadFail(mHttpRequest, throwable);
        if (finalThrowable != throwable) {
            EasyLog.printThrowable(mHttpRequest, finalThrowable);
        }
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchDownloadFailCallback(finalThrowable));
    }

    public boolean verifyFileMd5() {
        try {
            return mFile.isFile() && mMd5 != null && !"".equals(mMd5) &&
                mMd5.equalsIgnoreCase(EasyUtils.getFileMd5(EasyUtils.openFileInputStream(mFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void dispatchDownloadStartCallback() {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadStart(mFile);
        }
        EasyLog.printLog(mHttpRequest,  "Download file start, file path = " + mFile.getPath());
    }

    public void dispatchDownloadByteChangeCallback() {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadByteChange(mFile, mTotalByte.get(), mDownloadByte.get());
        }
        int currentProgress = EasyUtils.getProgressProgress(mTotalByte.get(), mDownloadByte.get());
        // 只有下载进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
        if (currentProgress == mDownloadProgress) {
            return;
        }
        mDownloadProgress = currentProgress;
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadProgressChange(mFile, mDownloadProgress);
        }
        EasyLog.printLog(mHttpRequest,  "Download file progress change, downloaded: " + mDownloadByte + " / " + mTotalByte +
            ", progress: " + currentProgress + " %" + ", file path = " + mFile.getPath());
    }

    public void dispatchDownloadSuccessCallback(boolean cache) {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadSuccess(mFile, cache);
            mListener.onDownloadEnd(mFile);
        }
        EasyLog.printLog(mHttpRequest,  "Download file success, file path = " + mFile.getPath());
    }

    public void dispatchDownloadFailCallback(Throwable throwable) {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadFail(mFile, throwable);
            mListener.onDownloadEnd(mFile);
        }
        EasyLog.printLog(mHttpRequest,  "Download file fail, file path = " + mFile.getPath());
    }
}