package com.hjq.http.callback;

import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.exception.MD5Exception;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.DownloadInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 下载接口回调
 */
public final class DownloadCallback extends BaseCallback {

    /** 下载任务 */
    private DownloadInfo mDownloadInfo;
    /** 保存的文件 */
    private File mFile;
    /** 校验的 MD5 */
    private String mMD5;
    /** 下载监听回调 */
    private OnDownloadListener mListener;

    public DownloadCallback(CallProxy call, File file, String md5, OnDownloadListener listener) {
        super(call);
        mDownloadInfo = new DownloadInfo(file);
        mFile = file;
        mMD5 = md5;
        mListener = listener;
    }

    @Override
    protected void onResponse(Response response) throws Exception {
        if (mMD5 == null || "".equals(mMD5)) {
            // 获取响应头中的文件 MD5 值
            mMD5 = response.header("Content-MD5");
        }

        EasyUtils.createFolder(mFile.getParentFile());

        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                mListener.onDownloadStart(mDownloadInfo);
            }
        });

        ResponseBody body = response.body();
        if (body == null) {
            EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    mListener.onDownloadError(mDownloadInfo, new NullBodyException("The response body is empty"));
                }
            });
            return;
        }

        mDownloadInfo.setTotalLength(body.contentLength());
        // 如果这个文件已经下载过，并且经过校验 MD5 是同一个文件的话，就直接回调下载成功监听
        if (mMD5 != null && !"".equals(mMD5) && mFile.exists() && mFile.isFile() && mMD5.equalsIgnoreCase(EasyUtils.getFileMD5(mFile))) {
            EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    mDownloadInfo.setDownloadLength(mDownloadInfo.getTotalLength());
                    mListener.onDownloadComplete(mDownloadInfo);
                }
            });
            return;
        }

        int readLength;
        long downloadSize = 0;
        byte[] bytes = new byte[8192];
        InputStream inputStream = body.byteStream();
        FileOutputStream outputStream = new FileOutputStream(mFile);
        while ((readLength = inputStream.read(bytes)) != -1) {
            downloadSize += readLength;
            outputStream.write(bytes, 0, readLength);
            mDownloadInfo.setDownloadLength(downloadSize);
            EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    mListener.onDownloadProgress(mDownloadInfo);
                }
            });
            EasyLog.print(mFile.getPath() + " 正在下载" +
                    "，文件总字节：" + mDownloadInfo.getTotalLength() +
                    "，已下载字节：" + mDownloadInfo.getDownloadLength() +
                    "，下载进度：" + mDownloadInfo.getDownloadProgress() + " %");
        }
        outputStream.flush();

        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                String fileMD5 = EasyUtils.getFileMD5(mDownloadInfo.getFile());
                if (mMD5 != null && !"".equals(mMD5) && !mMD5.equalsIgnoreCase(fileMD5)) {
                    onFailure(new MD5Exception("MD5 verify failure", fileMD5));
                } else {
                    mListener.onDownloadComplete(mDownloadInfo);
                }
            }
        });

        EasyUtils.closeStream(inputStream);
        EasyUtils.closeStream(outputStream);
    }

    @Override
    protected void onFailure(final Exception e) {
        EasyLog.print(e);
        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                mListener.onDownloadError(mDownloadInfo, e);
            }
        });
    }
}