package com.hjq.http;

import com.hjq.http.config.IRequestHandler;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.config.IRequestLogStrategy;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.config.impl.EasyHttpLogStrategy;
import com.hjq.http.config.impl.EasyRequestServer;
import com.hjq.http.model.ThreadSchedulers;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求配置类
 */
@SuppressWarnings("unused")
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

    public static EasyConfig with(OkHttpClient client) {
        return new EasyConfig(client);
    }

    /** 服务器配置 */
    private IRequestServer mServer;
    /** 请求处理器 */
    private IRequestHandler mHandler;
    /** 请求拦截器 */
    private IRequestInterceptor mInterceptor;
    /** 日志打印策略 */
    private IRequestLogStrategy mLogStrategy;

    /** OkHttp 客户端 */
    private OkHttpClient mClient;

    /** 通用参数 */
    private Map<String, Object> mParams;
    /** 通用请求头 */
    private Map<String, String> mHeaders;

    /** 线程调度器 */
    private ThreadSchedulers mThreadSchedulers = ThreadSchedulers.MAIN;

    /** 日志开关 */
    private boolean mLogEnabled = true;
    /** 日志 TAG */
    private String mLogTag = "EasyHttp";

    /** 重试次数 */
    private int mRetryCount;
    /** 重试时间 */
    private long mRetryTime = 2000;

    private EasyConfig(OkHttpClient client) {
        mClient = client;
        mParams = new HashMap<>();
        mHeaders = new HashMap<>();
    }

    public EasyConfig setServer(String host) {
        return setServer(new EasyRequestServer(host));
    }

    public EasyConfig setServer(IRequestServer server) {
        mServer = server;
        return this;
    }

    public EasyConfig setHandler(IRequestHandler handler) {
        mHandler = handler;
        return this;
    }

    public EasyConfig setInterceptor(IRequestInterceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    public EasyConfig setClient(OkHttpClient client) {
        mClient = client;
        if (mClient == null) {
            throw new IllegalArgumentException("The OkHttp client object cannot be empty");
        }
        return this;
    }

    public EasyConfig setParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>(10);
        }
        mParams = params;
        return this;
    }

    public EasyConfig setHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>(10);
        }
        mHeaders = headers;
        return this;
    }

    public EasyConfig addHeader(String key, String value) {
        if (key != null && value != null) {
            mHeaders.put(key, value);
        }
        return this;
    }

    public EasyConfig removeHeader(String key) {
        if (key != null) {
            mHeaders.remove(key);
        }
        return this;
    }

    public EasyConfig addParam(String key, String value) {
        if (key != null && value != null) {
            mParams.put(key, value);
        }
        return this;
    }

    public EasyConfig removeParam(String key) {
        if (key != null) {
            mParams.remove(key);
        }
        return this;
    }

    public EasyConfig setThreadSchedulers(ThreadSchedulers schedulers) {
        if (mThreadSchedulers == null) {
            // 线程调度器不能为空
            throw new NullPointerException("Thread schedulers cannot be empty");
        }
        mThreadSchedulers = schedulers;
        return this;
    }

    public EasyConfig setLogStrategy(IRequestLogStrategy strategy) {
        mLogStrategy = strategy;
        return this;
    }

    public EasyConfig setLogEnabled(boolean enabled) {
        mLogEnabled = enabled;
        return this;
    }

    public EasyConfig setLogTag(String tag) {
        mLogTag = tag;
        return this;
    }

    public EasyConfig setRetryCount(int count) {
        if (count < 0) {
            // 重试次数必须大于等于 0 次
            throw new IllegalArgumentException("The number of retries must be greater than 0");
        }
        mRetryCount = count;
        return this;
    }

    public EasyConfig setRetryTime(long time) {
        if (time < 0) {
            // 重试时间必须大于等于 0 毫秒
            throw new IllegalArgumentException("The retry time must be greater than 0");
        }
        mRetryTime = time;
        return this;
    }

    public IRequestServer getServer() {
        return mServer;
    }

    public IRequestHandler getHandler() {
        return mHandler;
    }

    public IRequestInterceptor getInterceptor() {
        return mInterceptor;
    }

    public OkHttpClient getClient() {
        return mClient;
    }

    public Map<String, Object> getParams() {
        return mParams;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public ThreadSchedulers getThreadSchedulers() {
        return mThreadSchedulers;
    }

    public IRequestLogStrategy getLogStrategy() {
        return mLogStrategy;
    }

    public boolean isLogEnabled() {
        return mLogEnabled && mLogStrategy != null;
    }

    public String getLogTag() {
        return mLogTag;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    public long getRetryTime() {
        return mRetryTime;
    }

    public void into() {
        if (mClient == null) {
            throw new IllegalArgumentException("Please set up the OkHttpClient object");
        }

        if (mServer == null) {
            throw new IllegalArgumentException("Please set up the RequestServer object");
        }

        if (mHandler == null) {
            throw new IllegalArgumentException("Please set the RequestHandler object");
        }

        try {
            // 校验主机和路径的 url 是否合法
            new URL(mServer.getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("The configured host path url address is not correct");
        }

        if (mLogStrategy == null) {
            mLogStrategy = new EasyHttpLogStrategy();
        }
        EasyConfig.setInstance(this);
    }
}