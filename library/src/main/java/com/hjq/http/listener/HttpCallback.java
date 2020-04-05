package com.hjq.http.listener;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求回调包装类
 */
public class HttpCallback<T> implements OnHttpListener<T> {

    private OnHttpListener mSource;

    public HttpCallback(OnHttpListener source) {
        mSource = source;
    }

    @Override
    public void onStart(Call call) {
        if (mSource != null) {
            mSource.onStart(call);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSucceed(T result) {
        if (mSource != null) {
            mSource.onSucceed(result);
        }
    }

    @Override
    public void onFail(Exception e) {
        if (mSource != null) {
            mSource.onFail(e);
        }
    }

    @Override
    public void onEnd(Call call) {
        if (mSource != null) {
            mSource.onEnd(call);
        }
    }
}
