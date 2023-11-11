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
    default void onDownloadStart(File file) {}

    /**
     * 下载字节改变
     *
     * @param totalByte             总字节数
     * @param downloadByte          已下载字节数
     */
    default void onDownloadByteChange(File file, long totalByte, long downloadByte) {}

    /**
     * 下载进度改变
     *
     * @param progress              下载进度值（0-100）
     */
    void onDownloadProgressChange(File file, int progress);

    /**
     * 下载成功
     *
     * @param cache         是否是通过缓存下载成功的
     */
    default void onDownloadSuccess(File file, boolean cache) {
        onDownloadSuccess(file);
    }

    /**
     * 下载成功
     */
    void onDownloadSuccess(File file);

    /**
     * 下载失败
     */
    void onDownloadFail(File file, Throwable throwable);

    /**
     * 下载结束
     */
    default void onDownloadEnd(File file) {}
}