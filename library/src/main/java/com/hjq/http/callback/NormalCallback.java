package com.hjq.http.callback;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.CallProxy;

import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 正常接口回调
 */
@SuppressWarnings("rawtypes")
public final class NormalCallback extends BaseCallback {

    private final LifecycleOwner mLifecycle;
    private final OnHttpListener mListener;
    private final IRequestApi mRequestApi;
    private final IRequestHandler mRequestHandler;

    public NormalCallback(LifecycleOwner lifecycleOwner, CallProxy call, IRequestApi api, IRequestHandler handler, OnHttpListener listener) {
        super(lifecycleOwner, call);
        mLifecycle = lifecycleOwner;
        mRequestApi = api;
        mListener = listener;
        mRequestHandler = handler;

        EasyUtils.post(() -> {
            if (mListener == null || !HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                return;
            }
            mListener.onStart(call);
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onResponse(Response response) throws Exception {
        // 打印请求耗时时间
        EasyLog.print("RequestConsuming：" + (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + " ms");
        final Object result = mRequestHandler.requestSucceed(mLifecycle, mRequestApi, response, EasyUtils.getReflectType(mListener));
        EasyUtils.post( () -> {
            if (mListener == null || !HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                return;
            }
            mListener.onSucceed(result);
            mListener.onEnd(getCall());
        });
    }

    @Override
    protected void onFailure(Exception e) {
        final Exception exception = mRequestHandler.requestFail(mLifecycle, mRequestApi, e);
        EasyLog.print(exception);
        EasyUtils.post(() -> {
            if (mListener == null || !HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                return;
            }
            mListener.onFail(exception);
            mListener.onEnd(getCall());
        });
    }
}