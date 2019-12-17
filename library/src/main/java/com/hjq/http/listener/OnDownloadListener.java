package com.hjq.http.listener;

import com.hjq.http.model.DownloadInfo;

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
    void onDownloadStart(DownloadInfo info);

    /**
     * 下载进度改变
     */
    void onDownloadProgress(DownloadInfo info);

    /**
     * 完成下载
     */
    void onDownloadComplete(DownloadInfo info);

    /**
     * 下载出错
     */
    void onDownloadError(DownloadInfo info, Exception e);
}