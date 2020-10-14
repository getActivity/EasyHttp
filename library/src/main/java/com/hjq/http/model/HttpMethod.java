package com.hjq.http.model;

import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求方式
 */
public enum HttpMethod {

    /** GET 请求 */
    GET("GET"),

    /** Post 请求 */
    POST("POST"),

    /** Head 请求 */
    HEAD("HEAD"),

    /** Delete 请求 */
    DELETE("DELETE"),

    /** Put 请求 */
    PUT("PUT"),

    /** Patch 请求 */
    PATCH("PATCH");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    @NonNull
    @Override
    public String toString() {
        return method;
    }
}