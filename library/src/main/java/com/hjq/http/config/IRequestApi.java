package com.hjq.http.config;

import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求接口配置
 */
public interface IRequestApi {

    /**
     * 请求接口
     */
    @NonNull
    String getApi();
}