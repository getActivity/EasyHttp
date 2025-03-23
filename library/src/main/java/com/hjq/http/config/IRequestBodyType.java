package com.hjq.http.config;

import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/01/01
 *    desc   : 请求体类型配置
 */
public interface IRequestBodyType {

    /**
     * 获取参数的提交类型
     */
    @NonNull
    IHttpPostBodyStrategy getBodyType();
}