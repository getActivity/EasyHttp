package com.hjq.http.listener;

import com.hjq.http.model.DownloadTask;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   :
 */
public interface OnDownloadListener {

    /**
     * 开始下载
     */
    void onDownloadStart(DownloadTask task);

    /**
     * 下载进度改变
     */
    void onDownloadProgress(DownloadTask task);

    /**
     * 完成下载
     */
    void onDownloadComplete(DownloadTask task);

    /**
     * 下载出错
     */
    void onDownloadError(DownloadTask task, Exception e);
}