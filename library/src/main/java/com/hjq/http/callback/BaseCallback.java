package com.hjq.http.callback;

import androidx.annotation.NonNull;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.ThreadSchedulers;
import com.hjq.http.request.HttpRequest;
import java.io.IOException;
import java.net.SocketTimeoutException;
import okhttp3.Call;
import okhttp3.Callback;
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

    /** 请求任务对象 */
    private CallProxy mCall;

    /** 当前重试次数 */
    private int mRetryCount;

    public BaseCallback(@NonNull HttpRequest<?> request) {
        mHttpRequest = request;
        // Lifecycle addObserver 需要在主线程中执行，所以这里要做一下线程转换
        EasyUtils.runOnAssignThread(ThreadSchedulers.MAIN,
                () -> HttpLifecycleManager.register(mHttpRequest.getLifecycleOwner()));
    }

    public BaseCallback setCall(CallProxy call) {
        mCall = call;
        return this;
    }

    public void start() {
        mCall.enqueue(this);
        onStart(mCall);
    }

    protected CallProxy getCall() {
        return mCall;
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
            // 关闭响应
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
                mCall.setCall(newCall);
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
    protected abstract void onStart(Call call);

    /**
     * 请求成功
     */
    protected abstract void onHttpResponse(Response response) throws Throwable;

    /**
     * 请求失败
     */
    protected abstract void onHttpFailure(Throwable e);

    /**
     * 关闭响应
     */
    protected void closeResponse(Response response) {
        EasyUtils.closeStream(response);
    }
}