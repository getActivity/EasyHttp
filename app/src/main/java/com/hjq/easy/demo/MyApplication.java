package com.hjq.easy.demo;

import android.app.Application;

import com.hjq.easy.demo.http.model.RequestHandler;
import com.hjq.easy.demo.http.server.ReleaseServer;
import com.hjq.easy.demo.http.server.TestServer;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
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

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        EasyConfig.with(okHttpClient)
                // 是否打印日志
                //.setLogEnabled(BuildConfig.DEBUG)
                // 设置服务器配置
                .setServer(server)
                // 设置请求处理策略
                .setHandler(new RequestHandler(this))
                // 设置请求参数拦截器
                .setInterceptor(new IRequestInterceptor() {
                    @Override
                    public void intercept(String url, String tag, HttpParams params, HttpHeaders headers) {
                        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    }
                })
                // 设置请求重试次数
                .setRetryCount(1)
                // 设置请求重试时间
                .setRetryTime(1000)
                // 添加全局请求参数
                .addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("time", "20191030")
                .into();
    }
}