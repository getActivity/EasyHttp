package com.hjq.http.listener;

import androidx.annotation.NonNull;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求回调代理类
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HttpCallbackProxy<T> implements OnHttpListener<T> {

    private final OnHttpListener mSourceListener;

    public HttpCallbackProxy(OnHttpListener listener) {
        mSourceListener = listener;
    }

    @Override
    public void onHttpStart(@NonNull IRequestApi api) {
        if (mSourceListener == null) {
            return;
        }
        mSourceListener.onHttpStart(api);
    }

    @Override
    public void onHttpSuccess(@NonNull T result, boolean cache) {
        // 这里解释一下，为什么不那么写
        // 这是因为回调原有的监听器的 onHttpSuccess(T result, boolean cache) 方法，
        // 最终它只会回调原有监听器的 onHttpSuccess(T result) 方法
        // 这样会导致当前类的 onHttpSuccess(T result) 方法没有被回调
        // if (mListener == null) {
        //     return;
        // }
        // mListener.onHttpSuccess(result, cache);
        onHttpSuccess(result);
    }

    @Override
    public void onHttpSuccess(@NonNull T result) {
        if (mSourceListener == null) {
            return;
        }
        mSourceListener.onHttpSuccess(result);
    }

    @Override
    public void onHttpFail(@NonNull Throwable throwable) {
        if (mSourceListener == null) {
            return;
        }
        mSourceListener.onHttpFail(throwable);
    }

    @Override
    public void onHttpEnd(@NonNull IRequestApi api) {
        if (mSourceListener == null) {
            return;
        }
        mSourceListener.onHttpEnd(api);
    }
}