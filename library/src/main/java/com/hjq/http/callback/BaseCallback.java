package com.hjq.http.callback;

import androidx.annotation.NonNull;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.ThreadSchedulers;
import com.hjq.http.request.HttpRequest;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketTimeoutException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 接口回调基类
 */
public abstract class BaseCallback implements Callback {

    /** 请求配置 */
    private final HttpRequest<?> mHttpRequest;

    /** 请求任务对象创建工厂 */
    private CallProxy.Factory mCallProxyFactory;

    /** 请求任务对象 */
    private CallProxy mCallProxy;

    /** 当前重试次数 */
    private int mRetryCount;

    public BaseCallback(@NonNull HttpRequest<?> request) {
        mHttpRequest = request;
        // Lifecycle addObserver 需要在主线程中执行，所以这里要做一下线程转换
        EasyUtils.runOnAssignThread(ThreadSchedulers.MAIN,
                () -> HttpLifecycleManager.register(mHttpRequest.getLifecycleOwner()));
    }

    public BaseCallback setCallProxyFactory(CallProxy.Factory factory) {
        mCallProxyFactory = factory;
        return this;
    }

    public void start() {
        onStart();
        mCallProxy = mCallProxyFactory.create();
        try {
            mCallProxy.enqueue(this);
        } catch (Throwable throwable) {
            onHttpFailure(throwable);
        }
    }

    protected CallProxy getCallProxy() {
        return mCallProxy;
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
        try {
            // 收到响应
            onHttpResponse(response);
        } catch (Throwable throwable) {
            // 回调失败
            onHttpFailure(throwable);
        } finally {
            // 关闭请求体
            closeRequest(response.request());
            // 关闭响应体
            closeResponse(response);
        }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        // 服务器请求超时重试
        if (e instanceof SocketTimeoutException && mRetryCount < EasyConfig.getInstance().getRetryCount()) {
            // 设置延迟 N 秒后重试该请求
            EasyUtils.postDelayedRunnable(() -> {

                // 前提是宿主还没有被销毁
                if (!HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
                    // 宿主已被销毁，请求无法进行
                    EasyLog.printLog(mHttpRequest, "LifecycleOwner has been destroyed and the request cannot be made");
                    return;
                }

                mRetryCount++;
                Call newCall = call.clone();
                mCallProxy.setRealCall(newCall);
                newCall.enqueue(BaseCallback.this);
                // 请求超时，正在执行延迟重试
                EasyLog.printLog(mHttpRequest, "The request timed out, a delayed retry is being performed, the number of retries: " +
                        mRetryCount + " / " + EasyConfig.getInstance().getRetryCount());

            }, EasyConfig.getInstance().getRetryTime());

            return;
        }
        onHttpFailure(e);
    }

    /**
     * 请求开始
     */
    protected abstract void onStart();

    /**
     * 请求成功
     */
    protected abstract void onHttpResponse(Response response) throws Throwable;

    /**
     * 请求失败
     */
    protected abstract void onHttpFailure(Throwable e);

    /**
     * 关闭请求体
     */
    protected void closeRequest(Request request) {
        RequestBody body = request.body();
        if (body == null) {
            return;
        }
        if (!(body instanceof Closeable)) {
            return;
        }
        // 手动关闭文件流，避免内存泄漏
        EasyUtils.closeStream(((Closeable) body));
    }

    /**
     * 关闭响应体
     */
    protected void closeResponse(Response response) {
        EasyUtils.closeStream(response);
    }
}