package com.hjq.http.config;

import androidx.annotation.NonNull;

import com.hjq.http.model.CacheMode;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2021/05/22
 *    desc   : 请求缓存配置
 */
public interface IRequestCache {

    /**
     * 获取缓存的模式
     */
    @NonNull
    CacheMode getCacheMode();

    /**
     * 获取缓存的有效时长（以毫秒为单位）
     */
    long getCacheTime();
}