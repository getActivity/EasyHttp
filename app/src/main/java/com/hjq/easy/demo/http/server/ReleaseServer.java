package com.hjq.easy.demo.http.server;

import com.hjq.http.config.IRequestServer;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 正式环境
 */
public class ReleaseServer implements IRequestServer {

    @Override
    public String getHost() {
        return "https://www.wanandroid.com/";
    }

    @Override
    public String getPath() {
        return "";
    }
}