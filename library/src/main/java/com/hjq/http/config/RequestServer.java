package com.hjq.http.config;

import com.hjq.http.annotation.HttpIgnore;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求服务简单配置类
 */
public final class RequestServer implements IRequestServer {

    /** 主机地址 */
    @HttpIgnore
    private String mHost;
    /** 接口路径 */
    @HttpIgnore
    private String mPath;

    public RequestServer(String host) {
        this(host, "");
    }

    public RequestServer(String host, String path) {
        mHost = host;
        mPath = path;
    }

    @Override
    public String getHost() {
        return mHost;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public String toString() {
        return mHost + mPath;
    }
}