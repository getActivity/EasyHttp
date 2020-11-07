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
     * 下载字节改变
     *
     * @param totalByte             总字节数
     * @param downloadByte          已下载字节数
     */
    default void onByte(File file, long totalByte, long downloadByte) {}

    /**
     * 下载进度改变
     *
     * @param progress              下载进度值（0-100）
     */
    void onProgress(File file, int progress);

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