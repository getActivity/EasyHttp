package com.hjq.http.callback;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.CallProxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
    private long mRequestTime;

    public NormalCallback(LifecycleOwner lifecycleOwner, CallProxy call, OnHttpListener listener) {
        super(lifecycleOwner, call);
        mLifecycle = lifecycleOwner;
        mListener = listener;
        mRequestTime = System.currentTimeMillis();

        EasyUtils.runOnUiThread(mListener != null && isLifecycleActive(), () -> mListener.onStart(call));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onResponse(Response response) throws Exception {
        Type type;
        Type[] types = mListener.getClass().getGenericInterfaces();
        if (types.length > 0) {
            // 如果这个监听对象是直接实现了接口
            type = ((ParameterizedType) types[0]).getActualTypeArguments()[0];
        } else {
            // 如果这个监听对象是通过类继承
            type = ((ParameterizedType) mListener.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

        EasyLog.print("RequestTime：" + (System.currentTimeMillis() - mRequestTime) + " ms");
        final Object result = EasyConfig.getInstance().getHandler().requestSucceed(mLifecycle, response, type);
        EasyUtils.runOnUiThread(mListener != null && isLifecycleActive(), () -> {
            mListener.onSucceed(result);
            mListener.onEnd(getCall());
        });
    }

    @Override
    protected void onFailure(Exception e) {
        EasyLog.print(e);
        final Exception exception = EasyConfig.getInstance().getHandler().requestFail(mLifecycle, e);
        EasyUtils.runOnUiThread(mListener != null && isLifecycleActive(), () -> {
            mListener.onFail(exception);
            mListener.onEnd(getCall());
        });
    }
}