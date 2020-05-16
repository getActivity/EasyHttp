package com.hjq.http.demo;

import android.app.Application;

import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.demo.http.model.RequestHandler;
import com.hjq.http.demo.http.server.ReleaseServer;
import com.hjq.http.demo.http.server.TestServer;
import com.hjq.toast.ToastUtils;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);

        // 网络请求框架初始化
        IRequestServer server;
        if (BuildConfig.DEBUG) {
            server = new TestServer();
        } else {
            server = new ReleaseServer();
        }

        EasyConfig.with(new OkHttpClient())
                // 是否打印日志
                //.setLogEnabled(BuildConfig.DEBUG)
                // 设置服务器配置
                .setServer(server)
                // 设置请求处理策略
                .setHandler(new RequestHandler(this))
                // 设置请求重试次数
                .setRetryCount(1)
                // 添加全局请求参数
                //.addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
                .into();
    }
}