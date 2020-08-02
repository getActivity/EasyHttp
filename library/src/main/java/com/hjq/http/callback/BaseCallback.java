package com.hjq.http.callback;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.HttpLifecycle;

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

    /**
     * 请求任务对象
     */
    private CallProxy mCall;
    /**
     * 当前重试次数
     */
    private int mRetryCount;
    /**
     * 生命周期管理
     */
    private LifecycleOwner mLifecycleOwner;

    BaseCallback(LifecycleOwner lifecycleOwner, CallProxy call) {
        mCall = call;
        mLifecycleOwner = lifecycleOwner;
        HttpLifecycle.with(lifecycleOwner);
    }

    CallProxy getCall() {
        return mCall;
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            onResponse(response);
        } catch (final Exception e) {
            // 回调失败
            onFailure(e);
        } finally {
            // 关闭响应
            response.close();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        // 服务器请求超时重试
        if (e instanceof SocketTimeoutException && mRetryCount < EasyConfig.getInstance().getRetryCount()) {
            // 设置延迟 N 秒后重试该请求
            EasyUtils.postDelayed(() -> {
                // 前提是宿主还没有被销毁
                if (isLifecycleActive()) {
                    mRetryCount++;
                    Call newCall = call.clone();
                    mCall.setCall(newCall);
                    newCall.enqueue(BaseCallback.this);
                    EasyLog.print("请求超时，正在延迟重试，重试次数：" + mRetryCount + "/" + EasyConfig.getInstance().getRetryCount());
                } else {
                    EasyLog.print("宿主已被销毁，无法对请求进行重试");
                }
            }, EasyConfig.getInstance().getRetryTime());
            return;
        }
        onFailure(e);
    }

    /**
     * 判断宿主是否处于活动状态
     */
    protected boolean isLifecycleActive() {
        return mLifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED;
    }

    protected abstract void onResponse(Response response) throws Exception;

    protected abstract void onFailure(Exception e);
}
