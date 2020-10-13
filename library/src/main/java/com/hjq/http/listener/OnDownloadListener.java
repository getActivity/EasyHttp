package com.hjq.http.listener;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 下载监听器
 */
public interface OnDownloadListener {

    /**
     * 下载开始
     */
    void onStart(File file);

    /**
     * 下载进度改变
     */
    void onProgress(File file, long totalByte, long downloadByte, int progress);

    /**
     * 下载完成
     */
    void onComplete(File file);

    /**
     * 下载出错
     */
    void onError(File file, Exception e);

    /**
     * 下载结束
     */
    void onEnd(File file);
}