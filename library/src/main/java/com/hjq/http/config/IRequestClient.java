package com.hjq.http.config;

import com.hjq.http.EasyConfig;

import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2021/03/02
 *    desc   : OkHttpClient 配置
 */
public interface IRequestClient {

    /**
     * 获取 OkHttpClient
     */
    default OkHttpClient getClient() {
        return EasyConfig.getInstance().getClient();
    }
}