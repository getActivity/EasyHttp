package com.hjq.http.listener;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求回调包装类
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HttpCallback<T> implements OnHttpListener<T> {

    private final OnHttpListener mListener;

    public HttpCallback(OnHttpListener listener) {
        mListener = listener;
    }

    @Override
    public void onStart(Call call) {
        if (mListener == null) {
            return;
        }
        mListener.onStart(call);
    }

    @Override
    public void onSucceed(T result, boolean cache) {
        onSucceed(result);
    }

    @Override
    public void onSucceed(T result) {
        if (mListener == null) {
            return;
        }
        mListener.onSucceed(result);
    }

    @Override
    public void onFail(Exception e) {
        if (mListener == null) {
            return;
        }
        mListener.onFail(e);
    }

    @Override
    public void onEnd(Call call) {
        if (mListener == null) {
            return;
        }
        mListener.onEnd(call);
    }
}