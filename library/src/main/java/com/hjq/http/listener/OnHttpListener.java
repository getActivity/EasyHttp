package com.hjq.http.listener;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求回调监听器
 */
public interface OnHttpListener<T> {

    /**
     * 请求开始
     */
    default void onHttpStart(Call call) {}

    /**
     * 请求成功
     *
     * @param cache         是否是通过缓存请求成功的
     */
    default void onHttpSuccess(T result, boolean cache) {
        onHttpSuccess(result);
    }

    /**
     * 请求成功
     */
    void onHttpSuccess(T result);

    /**
     * 请求出错
     */
    void onHttpFail(Throwable throwable);

    /**
     * 请求结束
     */
    default void onHttpEnd(Call call) {}
}