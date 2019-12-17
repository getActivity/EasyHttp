package com.hjq.http.callback;

import com.hjq.http.EasyConfig;
import com.hjq.http.model.CallProxy;

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

    /** 请求任务对象 */
    private CallProxy mCall;
    /** 当前重试次数 */
    private int mRetryCount;

    BaseCallback(CallProxy call) {
        mCall = call;
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
            mRetryCount++;
            Call newCall = call.clone();
            mCall.setCall(newCall);
            newCall.enqueue(this);
            return;
        }
        onFailure(e);
    }

    protected abstract void onResponse(Response response) throws Exception;

    protected abstract void onFailure(Exception e);
}
