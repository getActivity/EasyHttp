package com.hjq.http.config.impl;

import androidx.annotation.NonNull;
import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.config.IRequestServer;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 服务器简单配置
 */
public final class EasyRequestServer implements IRequestServer {

    /** 主机地址 */
    @HttpIgnore
    private final String mHost;

    public EasyRequestServer(String host) {
        mHost = host;
    }

    @NonNull
    @Override
    public String getHost() {
        return mHost;
    }

    @NonNull
    @Override
    public String toString() {
        return mHost;
    }
}