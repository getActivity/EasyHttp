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
 *    desc   : 默认接口回调
 */
public final class DefaultCallback extends BaseCallback {

    private Context mContext;
    private OnHttpListener mListener;

    public DefaultCallback(Context context, CallProxy call, OnHttpListener listener) {
        super(call);
        mContext = context;
        mListener = listener;
    }

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

        Class clazz;
        if (type instanceof Class) {
            // 如果这个是简单的 Class（String、JSONObject、JSONArray）
            clazz = (Class) type;
        } else if (type instanceof ParameterizedType) {
            // 如果这个是复杂的 Class（HttpData<Bean>）
            clazz = (Class) ((ParameterizedType) type).getRawType();
        } else {
            // 如果这个是其他类型 Class，就直接用 Void 代替
            clazz = Void.TYPE;
        }

        final Object result = EasyConfig.getInstance().getHandler().requestSucceed(mContext, response, clazz);
        EasyUtils.runOnUiThread(mListener != null, new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                mListener.onSucceed(result);
            }
        });
        EasyConfig.getInstance().getHandler().requestEnd(mContext, getCall());
    }

    @Override
    protected void onFailure(Exception e) {
        EasyLog.print(e);
        final Exception exception = EasyConfig.getInstance().getHandler().requestFail(mContext, e);
        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                mListener.onFail(exception);
            }
        });
        EasyConfig.getInstance().getHandler().requestEnd(mContext, getCall());
    }
}