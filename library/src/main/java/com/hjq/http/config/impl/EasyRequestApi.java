package com.hjq.http.config.impl;

import androidx.annotation.NonNull;
import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求接口简单配置类
 */
public class EasyRequestApi implements IRequestApi {

    /** 接口地址 */
    @HttpIgnore
    private final String mApi;

    public EasyRequestApi(String api) {
        mApi = api;
    }

    @NonNull
    @Override
    public String getApi() {
        return mApi;
    }

    @NonNull
    @Override
    public String toString() {
        return mApi;
    }
}