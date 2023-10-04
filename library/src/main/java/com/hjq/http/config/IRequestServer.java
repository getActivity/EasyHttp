package com.hjq.http.config;

import androidx.annotation.NonNull;

import com.hjq.http.model.RequestBodyType;
import com.hjq.http.model.CacheMode;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求服务配置
 */
public interface IRequestServer extends
        IRequestHost, IRequestClient,
        IRequestType, IRequestCache {

    @NonNull
    @Override
    default IRequestBodyStrategy getBodyType() {
        // 默认以表单的方式提交
        return RequestBodyType.FORM;
    }

    @NonNull
    @Override
    default CacheMode getCacheMode() {
        // 默认的缓存方式
        return CacheMode.DEFAULT;
    }

    @Override
    default long getCacheTime() {
        return Long.MAX_VALUE;
    }
}