package com.hjq.http.listener;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/08/15
 *    desc   : 带上传进度回调的监听器
 */
public interface OnUpdateListener<T> extends OnHttpListener<T> {

    /**
     * 上传进度发生变化
     *
     * @param totalByte         总字节数
     * @param updateByte        已上传字节数
     * @param progress          上传进度值（0-100）
     */
    void onUpdate(long totalByte, long updateByte, int progress);
}