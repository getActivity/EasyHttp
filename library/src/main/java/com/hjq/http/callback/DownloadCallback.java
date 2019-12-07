package com.hjq.http.callback;

import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.exception.MD5Exception;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.DownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 下载接口回调
 */
public class DownloadCallback implements Callback {

    /** 下载任务 */
    private DownloadTask mDownloadTask;
    /** 保存的文件 */
    private File mFile;
    /** 校验的 MD5 */
    private String mMD5;
    /** 下载监听回调 */
    private OnDownloadListener mListener;

    public DownloadCallback(File file, String md5, OnDownloadListener listener) {
        mDownloadTask = new DownloadTask(file);
        mFile = file;
        mMD5 = md5;
        mListener = listener;
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (mMD5 == null || "".equals(mMD5)) {
            // 获取响应头中的文件 MD5 值
            mMD5 = response.header("Content-MD5");
        }

        EasyUtils.createFolder(mFile.getParentFile());

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        byte[] bytes = new byte[8192];
        int length;
        try {
            EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    mListener.onDownloadStart(mDownloadTask);
                }
            });
            ResponseBody body = response.body();
            if (body != null) {
                long totalSize = body.contentLength();
                mDownloadTask.setTotalBytes(totalSize);
                if (mMD5 != null && !"".equals(mMD5) && mFile.exists() && mFile.isFile()) {
                    if (mMD5.equalsIgnoreCase(EasyUtils.getFileMD5(mFile))) {
                        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                            @Override
                            public void run() {
                                mDownloadTask.setCurrentBytes(mDownloadTask.getTotalBytes());
                                mListener.onDownloadProgress(mDownloadTask);
                                mListener.onDownloadComplete(mDownloadTask);
                            }
                        });
                    }
                } else {
                    long downloadSize = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(mFile);
                    while ((length = inputStream.read(bytes)) != -1) {
                        downloadSize += length;
                        outputStream.write(bytes, 0, length);
                        mDownloadTask.setCurrentBytes(downloadSize);
                        if (EasyLog.isEnable()) {
                            EasyLog.print(mFile.getPath() + " 正在下载，" + "文件总字节：" + totalSize + "，已下载字节：" + downloadSize + ", 下载进度：" + mDownloadTask.getProgress() + " %");
                        }
                        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                            @Override
                            public void run() {
                                mListener.onDownloadProgress(mDownloadTask);
                            }
                        });
                    }
                    outputStream.flush();

                    EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                        @Override
                        public void run() {
                            String fileMD5 = EasyUtils.getFileMD5(mDownloadTask.getFile());
                            if (mMD5 != null && !"".equals(mMD5) && !mMD5.equalsIgnoreCase(fileMD5)) {
                                mListener.onDownloadError(mDownloadTask, new MD5Exception("MD5 verify failure", fileMD5));
                            } else {
                                mListener.onDownloadComplete(mDownloadTask);
                            }
                        }
                    });
                }
            }
        } catch (final IOException e) {
            EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    mListener.onDownloadError(mDownloadTask, e);
                }
            });
            e.printStackTrace();
        } finally {
            EasyUtils.closeStream(inputStream);
            EasyUtils.closeStream(outputStream);
        }
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        if (EasyLog.isEnable()) {
            // 打印错误信息
            e.printStackTrace();
        }
        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                mListener.onDownloadError(mDownloadTask, e);
            }
        });
    }
}