package com.hjq.http.config;

import androidx.annotation.NonNull;

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
    @NonNull
    default OkHttpClient getOkHttpClient() {
        return EasyConfig.getInstance().getClient();
    }
}