package com.hjq.http.callback;

import androidx.annotation.NonNull;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.exception.FileMd5Exception;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.request.HttpRequest;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.Call;
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
    private long mTotalByte;

    /** 已下载字节 */
    private long mDownloadByte;

    /** 下载进度 */
    private int mDownloadProgress;

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

    @Override
    protected void onStart(Call call) {
        mHttpRequest.getRequestHandler().downloadStart(mHttpRequest, mFile);
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchDownloadStartCallback);
    }

    @Override
    protected void onHttpResponse(Response response) throws Throwable {
        // 打印请求耗时时间
        EasyLog.printLog(mHttpRequest, "RequestConsuming：" +
                (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + " ms");

        IRequestInterceptor interceptor = mHttpRequest.getRequestInterceptor();
        if (interceptor != null) {
            response = interceptor.interceptResponse(mHttpRequest, response);
        }

        if (!response.isSuccessful())  {
            throw new ResponseException("The request failed, responseCode: " +
                    response.code() + ", message: " + response.message(), response);
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

        File parentFile = mFile.getParentFile();
        if (parentFile != null) {
            EasyUtils.createFolder(parentFile);
        }
        ResponseBody body = response.body();
        if (body == null) {
            throw new NullBodyException("The response body is empty");
        }

        mTotalByte = body.contentLength();
        if (mTotalByte < 0) {
            mTotalByte = 0;
        }

        // 如果这个文件已经下载过，并且经过校验 MD5 是同一个文件的话，就直接回调下载成功监听
        if (mFile.isFile() && mMd5 != null && !"".equals(mMd5) &&
                mMd5.equalsIgnoreCase(EasyUtils.getFileMd5(EasyUtils.openFileInputStream(mFile)))) {
            // 文件已存在，跳过下载
            EasyLog.printLog(mHttpRequest, mFile.getPath() + " file already exists, skip download");
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchDownloadSuccessCallback(true));
            return;
        }

        int readLength;
        mDownloadByte = 0;
        byte[] bytes = new byte[8192];
        InputStream inputStream = body.byteStream();
        OutputStream outputStream = EasyUtils.openFileOutputStream(mFile);
        while ((readLength = inputStream.read(bytes)) != -1) {
            mDownloadByte += readLength;
            outputStream.write(bytes, 0, readLength);
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchDownloadByteChangeCallback);
        }
        EasyUtils.closeStream(inputStream);
        EasyUtils.closeStream(outputStream);

        String md5 = EasyUtils.getFileMd5(EasyUtils.openFileInputStream(mFile));
        if (mMd5 != null && !"".equals(mMd5) && !mMd5.equalsIgnoreCase(md5)) {
            // 文件 MD5 值校验失败
            throw new FileMd5Exception("File md5 hash verify failure", md5);
        }

        // 下载成功
        mHttpRequest.getRequestHandler().downloadSuccess(mHttpRequest, response, mFile);

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

    private void dispatchDownloadStartCallback() {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadStart(mFile);
        }
        EasyLog.printLog(mHttpRequest,  "Download file start, file path = " + mFile.getPath());
    }

    private void dispatchDownloadByteChangeCallback() {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadByteChange(mFile, mTotalByte, mDownloadByte);
        }
        int currentProgress = EasyUtils.getProgressProgress(mTotalByte, mDownloadByte);
        // 只有下载进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
        if (currentProgress == mDownloadProgress) {
            return;
        }
        mDownloadProgress = currentProgress;
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadProgressChange(mFile, mDownloadProgress);
        }
        EasyLog.printLog(mHttpRequest,  "Download file progress change, downloaded: " + mDownloadByte + " / " + mTotalByte +
            ", progress: " + currentProgress + " %" + "file path = " + mFile.getPath());
    }

    private void dispatchDownloadSuccessCallback(boolean cache) {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadSuccess(mFile, cache);
            mListener.onDownloadEnd(mFile);
        }
        EasyLog.printLog(mHttpRequest,  "Download file success, file path = " + mFile.getPath());
    }

    private void dispatchDownloadFailCallback(Throwable throwable) {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onDownloadFail(mFile, throwable);
            mListener.onDownloadEnd(mFile);
        }
        EasyLog.printLog(mHttpRequest,  "Download file fail, file path = " + mFile.getPath());
    }
}