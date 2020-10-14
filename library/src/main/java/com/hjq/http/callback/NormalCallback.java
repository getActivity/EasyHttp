package com.hjq.http.callback;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.lifecycle.HttpLifecycleControl;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.CallProxy;

import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 正常接口回调
 */
public final class NormalCallback extends BaseCallback {

    private LifecycleOwner mLifecycle;
    private OnHttpListener mListener;

    public NormalCallback(LifecycleOwner lifecycleOwner, CallProxy call, OnHttpListener listener) {
        super(lifecycleOwner, call);
        mLifecycle = lifecycleOwner;
        mListener = listener;

        EasyUtils.post(() -> {
            if (mListener != null && HttpLifecycleControl.isLifecycleActive(getLifecycleOwner())) {
                mListener.onStart(call);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onResponse(Response response) throws Exception {
        EasyLog.print("RequestTime：" + (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + " ms");
        final Object result = EasyConfig.getInstance().getHandler().requestSucceed(mLifecycle, response, EasyUtils.getReflectType(mListener));
        EasyUtils.post( () -> {
            if (mListener != null && HttpLifecycleControl.isLifecycleActive(getLifecycleOwner())) {
                mListener.onSucceed(result);
                mListener.onEnd(getCall());
            }
        });
    }

    @Override
    protected void onFailure(Exception e) {
        EasyLog.print(e);
        final Exception exception = EasyConfig.getInstance().getHandler().requestFail(mLifecycle, e);
        EasyUtils.post(() -> {
            if (mListener != null && HttpLifecycleControl.isLifecycleActive(getLifecycleOwner())) {
                mListener.onFail(exception);
                mListener.onEnd(getCall());
            }
        });
    }
}