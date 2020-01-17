package com.hjq.http.listener;

import com.hjq.http.model.DownloadInfo;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 下载监听器
 */
public interface OnDownloadListener {

    /**
     * 下载进度改变
     */
    void onProgress(DownloadInfo info);

    /**
     * 下载完成
     */
    void onComplete(DownloadInfo info);

    /**
     * 下载出错
     */
    void onError(DownloadInfo info, Exception e);

    /**
     * 下载开始
     */
    void onStart(Call call);

    /**
     * 下载结束
     */
    void onEnd(Call call);
}