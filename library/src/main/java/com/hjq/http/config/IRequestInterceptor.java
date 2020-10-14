package com.hjq.http.config;

import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/08/05
 *    desc   : 请求参数拦截器
 */
public interface IRequestInterceptor {

    /**
     * 开始请求之前调用
     *
     * @param url           请求地址
     * @param tag           请求标记
     * @param params        请求参数
     * @param headers       请求头参数
     */
    void intercept(String url, String tag, HttpParams params, HttpHeaders headers);
}