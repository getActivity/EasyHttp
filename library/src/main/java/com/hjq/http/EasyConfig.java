package com.hjq.http;

import com.hjq.http.config.IRequestHandler;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.config.RequestServer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求配置类
 */
public final class EasyConfig {

    private static volatile EasyConfig sConfig;

    public static EasyConfig getInstance() {
        if (sConfig == null) {
            // 当前没有初始化配置
            throw new IllegalStateException("You haven't initialized the configuration yet");
        }
        return sConfig;
    }

    private static void setInstance(EasyConfig config) {
        sConfig = config;
    }

    public static EasyConfig.Builder with(OkHttpClient client) {
        return new EasyConfig.Builder(client);
    }

    private IRequestServer mServer;
    private IRequestHandler mHandler;
    private boolean mLogEnabled;
    private int mRetryCount;

    private HashMap<String, Object> mParams;
    private HashMap<String, String> mHeaders;

    private OkHttpClient mHttpClient;

    private EasyConfig(Builder builder) {
        mServer = builder.mServer;
        mHandler = builder.mHandler;

        mParams = builder.mParams;
        mHeaders = builder.mHeaders;

        mHttpClient = builder.mClient;

        mLogEnabled = builder.mLogEnabled;
        mRetryCount = builder.mRetryCount;
    }

    public IRequestServer getServer() {
        return mServer;
    }

    public IRequestHandler getHandler() {
        return mHandler;
    }

    public OkHttpClient getClient() {
        return mHttpClient;
    }

    public HashMap<String, Object> getParams() {
        return mParams;
    }

    public HashMap<String, String> getHeaders() {
        return mHeaders;
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    public void addParam(String key, String value) {
        mParams.put(key, value);
    }

    public boolean isLog() {
        return mLogEnabled;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    public static final class Builder {

        /** 服务器配置 */
        private IRequestServer mServer;
        /** 请求拦截器 */
        private IRequestHandler mHandler;

        /** OkHttp 客户端 */
        private OkHttpClient mClient;

        /** 通用参数 */
        private HashMap<String, Object> mParams;
        /** 通用请求头 */
        private HashMap<String, String> mHeaders;

        /** 日志开关 */
        private boolean mLogEnabled = true;
        /** 重试次数 */
        private int mRetryCount;

        public Builder(OkHttpClient client) {
            mClient = client;
            mParams = new HashMap<>();
            mHeaders = new HashMap<>();
        }

        public Builder setServer(String host) {
            return setServer(new RequestServer(host));
        }

        public Builder setServer(IRequestServer server) {
            mServer = server;
            return this;
        }

        public Builder setHandler(IRequestHandler handler) {
            mHandler = handler;
            return this;
        }

        public Builder setLog(boolean enabled) {
            mLogEnabled = enabled;
            return this;
        }

        public Builder addHeader(String key, String value) {
            mHeaders.put(key, value);
            return this;
        }

        public Builder addParam(String key, String value) {
            mParams.put(key, value);
            return this;
        }

        public Builder setRetryCount(int retryCount) {
            if (retryCount < 0) {
                throw new IllegalArgumentException("The number of retries must be greater than 0");
            }
            mRetryCount = retryCount;
            return this;
        }

        public void into() {
            if (mClient == null) {
                throw new IllegalArgumentException("The OkHttp client object cannot be empty");
            }
            if (mServer == null) {
                throw new IllegalArgumentException("The host configuration cannot be empty");
            } else {
                try {
                    // 校验主机和路径的 url 是否合法
                    new URL(mServer.getHost() + mServer.getPath());
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("The configured host path url address is not correct");
                }
            }
            if (mHandler == null) {
                throw new IllegalArgumentException("The object being processed by the request cannot be empty");
            }
            EasyConfig.setInstance(new EasyConfig(this));
        }
    }
}