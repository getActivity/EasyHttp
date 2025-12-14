package com.hjq.easy.demo;

import android.app.Application;
import androidx.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.hjq.easy.demo.http.model.HttpCacheStrategy;
import com.hjq.easy.demo.http.model.RequestHandler;
import com.hjq.easy.demo.http.server.ReleaseServer;
import com.hjq.easy.demo.http.server.TestServer;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.gson.factory.ParseExceptionCallback;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import com.hjq.toast.Toaster;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/07
 *    desc   : 应用入口
 */
public final class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toaster.init(this);
        MMKV.initialize(this);

        // Bugly 异常捕捉
        CrashReport.initCrashReport(this, "8ca94a2408", BuildConfig.DEBUG);

        // 设置 Json 解析容错监听
        GsonFactory.setParseExceptionCallback(new ParseExceptionCallback() {

            @Override
            public void onParseObjectException(TypeToken<?> typeToken, String fieldName, JsonToken jsonToken) {
                handlerGsonParseException("Object parsing exception: " + typeToken + "#" + fieldName + ", backend return type: " + jsonToken);
            }

            @Override
            public void onParseListItemException(TypeToken<?> typeToken, String fieldName, JsonToken listItemJsonToken) {
                handlerGsonParseException("List parsing exception: " + typeToken + "#" + fieldName + ", backend return item type: " + listItemJsonToken);
            }

            @Override
            public void onParseMapItemException(TypeToken<?> typeToken, String fieldName, String mapItemKey, JsonToken mapItemJsonToken) {
                handlerGsonParseException("Map parsing exception: " + typeToken + "#" + fieldName + ", mapItemKey = " + mapItemKey + ", backend return item type: " + mapItemJsonToken);
            }

            private void handlerGsonParseException(String message) {
                if (BuildConfig.DEBUG) {
                    throw new IllegalArgumentException(message);
                }  else {
                    CrashReport.postCatchedException(new IllegalArgumentException(message));
                }
            }
        });

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
                // 设置服务器配置（必须设置）
                .setServer(server)
                // 设置请求处理策略（必须设置）
                .setHandler(new RequestHandler(this))
                // 设置请求缓存实现策略（非必须）
                .setCacheStrategy(new HttpCacheStrategy())
                // 设置请求参数拦截器
                .setInterceptor(new IRequestInterceptor() {
                    @Override
                    public void interceptArguments(@NonNull HttpRequest<?> httpRequest,
                                                   @NonNull HttpParams params,
                                                   @NonNull HttpHeaders headers) {
                        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    }
                })
                // 设置请求重试次数
                .setRetryCount(1)
                // 设置请求重试时间
                .setRetryTime(2000)
                // 添加全局请求参数
                .addParam("token", "6666666")
                // 添加全局请求头
                //.addHeader("date", "20191030")
                .into();
    }
}