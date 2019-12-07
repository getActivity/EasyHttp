package com.hjq.http.listener;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 回调监听
 */
public interface OnHttpListener<T> {

    /**
     * 请求结果
     */
    void onSucceed(T result);

    /**
     * 错误
     */
    void onFail(Exception e);
}