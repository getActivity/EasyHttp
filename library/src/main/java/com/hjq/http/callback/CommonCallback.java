package com.hjq.http.callback;

import android.content.Context;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyUtils;
import com.hjq.http.listener.OnHttpListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 通用接口回调
 */
public class CommonCallback implements Callback {

    private Context mContext;
    private OnHttpListener mListener;

    public CommonCallback(Context context, OnHttpListener listener) {
        mContext = context;
        mListener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResponse(final Call call, final Response response) {

        try {
            final Object data = EasyConfig.getInstance().getHandler().requestSucceed(mContext, call, response, mListener);
            EasyUtils.runOnUiThread(mListener != null, new Runnable() {
                @Override
                public void run() {
                    mListener.onSucceed(data);
                }
            });
            EasyConfig.getInstance().getHandler().requestEnd(mContext, call);
        } catch (final Exception e) {
            onFailure(call, EasyConfig.getInstance().getHandler().requestFail(mContext, call, e, mListener));
        }
    }

    @Override
    public void onFailure(final Call call, final IOException e) {
        onFailure(call, (Exception) e);
    }

    private void onFailure(final Call call, final Exception e) {
        final Exception exception = EasyConfig.getInstance().getHandler().requestFail(mContext, call, e, mListener);
        EasyUtils.runOnUiThread(mListener != null, new Runnable() {
            @Override
            public void run() {
                mListener.onFail(exception);
            }
        });
        EasyConfig.getInstance().getHandler().requestEnd(mContext, call);
    }
}