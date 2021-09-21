package com.hjq.http.callback;

import android.text.TextUtils;

import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.exception.MD5Exception;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.FileWrapper;
import com.hjq.http.request.BaseRequest;

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
    private final BaseRequest<?> mBaseRequest;

    /** 文件 MD5 正则表达式 */
    private static final String FILE_MD5_REGEX = "^[\\w]{32}$";

    /** 保存的文件 */
    private FileWrapper mFile;

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

    public DownloadCallback(BaseRequest<?> request) {
        super(request);
        mBaseRequest = request;
    }

    public DownloadCallback setFile(FileWrapper file) {
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
        EasyUtils.post(() -> {
            if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mBaseRequest.getLifecycleOwner())) {
                return;
            }
            mListener.onStart(mFile);
        });
    }

    @Override
    protected void onResponse(Response response) throws Exception {
        // 如果没有指定文件的 md5 值
        if (mMd5 == null) {
            // 获取响应头中的文件 MD5 值
            String md5 = response.header("Content-MD5");
            // 这个 md5 值必须是文件的 md5 值
            if (!TextUtils.isEmpty(md5) && md5.matches(FILE_MD5_REGEX)) {
                mMd5 = md5;
            }
        }

        File parentFile = mFile.getParentFile();
        if (parentFile != null) {
            FileWrapper.createFolder(parentFile);
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
        if (!TextUtils.isEmpty(mMd5) && mFile.isFile() &&
                mMd5.equalsIgnoreCase(FileWrapper.getFileMd5(mFile.openInputStream()))) {
            EasyUtils.post(() -> {
                if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mBaseRequest.getLifecycleOwner())) {
                    return;
                }
                mListener.onComplete(mFile);
                mListener.onEnd(mFile);
            });
            return;
        }

        int readLength;
        mDownloadByte = 0;
        byte[] bytes = new byte[8192];
        InputStream inputStream = body.byteStream();
        OutputStream outputStream = mFile.openOutputStream();
        while ((readLength = inputStream.read(bytes)) != -1) {
            mDownloadByte += readLength;
            outputStream.write(bytes, 0, readLength);
            EasyUtils.post(() -> {
                if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mBaseRequest.getLifecycleOwner())) {
                    return;
                }
                mListener.onByte(mFile, mTotalByte, mDownloadByte);
                int progress = EasyUtils.getProgressProgress(mTotalByte, mDownloadByte);
                // 只有下载进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
                if (progress != mDownloadProgress) {
                    mDownloadProgress = progress;
                    mListener.onProgress(mFile, mDownloadProgress);
                    EasyLog.print(mFile.getPath() + " 正在下载，总字节：" + mTotalByte +
                            "，已下载：" + mDownloadByte + "，进度：" + progress + " %");
                }
            });
        }
        EasyUtils.closeStream(inputStream);
        EasyUtils.closeStream(outputStream);

        String md5 = FileWrapper.getFileMd5(mFile.openInputStream());
        if (!TextUtils.isEmpty(mMd5) && !mMd5.equalsIgnoreCase(md5)) {
            // 文件 MD5 值校验失败
            throw new MD5Exception("MD5 verify failure", md5);
        }

        EasyUtils.post(() -> {
            if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mBaseRequest.getLifecycleOwner())) {
                return;
            }
            mListener.onComplete(mFile);
            mListener.onEnd(mFile);
        });
    }

    @Override
    protected void onFailure(final Exception e) {
        // 打印错误堆栈
        final Exception exception = mBaseRequest.getRequestHandler().requestFail(
                mBaseRequest.getLifecycleOwner(), mBaseRequest.getRequestApi(), e);
        EasyLog.print(exception);

        EasyUtils.post(() -> {
            if (mListener == null || !HttpLifecycleManager.isLifecycleActive(mBaseRequest.getLifecycleOwner())) {
                return;
            }
            mListener.onError(mFile, exception);
            mListener.onEnd(mFile);
        });
    }
}