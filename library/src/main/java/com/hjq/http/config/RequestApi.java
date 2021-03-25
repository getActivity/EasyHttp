package com.hjq.http.config;

import com.hjq.http.annotation.HttpIgnore;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求接口简单配置类
 */
public final class RequestApi implements IRequestApi {

    /** 接口地址 */
    @HttpIgnore
    private final String mApi;

    public RequestApi(String api) {
        mApi = api;
    }

    @Override
    public String getApi() {
        return mApi;
    }

    @Override
    public String toString() {
        return mApi;
    }
}