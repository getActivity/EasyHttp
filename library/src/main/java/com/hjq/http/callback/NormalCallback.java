package com.hjq.http.callback;

import android.content.Context;

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

    private Context mContext;
    private OnHttpListener mListener;

    public NormalCallback(Context context, CallProxy call, OnHttpListener listener) {
        super(call);
        mContext = context;
        mListener = listener;

        EasyUtils.runOnUiThread(mListener != null, () -> {
            mListener.onStart(call);
            EasyConfig.getInstance().getHandler().requestStart(context, call);
        });
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
            // 如果这个监听对象有通过类继承
            type = ((ParameterizedType) mListener.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

        final Object result = EasyConfig.getInstance().getHandler().requestSucceed(mContext, response, type);
        EasyUtils.runOnUiThread(mListener != null, () -> {
            mListener.onSucceed(result);
            mListener.onEnd(getCall());
            EasyConfig.getInstance().getHandler().requestEnd(mContext, getCall());
        });
    }

    @Override
    protected void onFailure(Exception e) {
        EasyLog.print(e);
        final Exception exception = EasyConfig.getInstance().getHandler().requestFail(mContext, e);
        EasyUtils.runOnUiThread(mListener != null, () -> {
            mListener.onFail(exception);
            mListener.onEnd(getCall());
            EasyConfig.getInstance().getHandler().requestEnd(mContext, getCall());
        });
    }
}