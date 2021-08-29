package com.hjq.http.config;

import com.hjq.http.model.CacheMode;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2021/05/22
 *    desc   : 请求缓存配置
 */
public interface IRequestCache {

    /**
     * 接口缓存方式
     */
    CacheMode getMode();
}