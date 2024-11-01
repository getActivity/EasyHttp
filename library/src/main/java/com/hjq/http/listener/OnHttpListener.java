package com.hjq.http.listener;

import androidx.annotation.NonNull;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求回调监听器
 */
public interface OnHttpListener<T> {

    /**
     * 请求开始
     */
    default void onHttpStart(@NonNull IRequestApi api) {}

    /**
     * 请求成功
     *
     * @param cache         是否是通过缓存请求成功的
     */
    default void onHttpSuccess(@NonNull T result, boolean cache) {
        onHttpSuccess(result);
    }

    /**
     * 请求成功
     */
    void onHttpSuccess(@NonNull T result);

    /**
     * 请求出错
     */
    void onHttpFail(@NonNull Throwable throwable);

    /**
     * 请求结束
     */
    default void onHttpEnd(@NonNull IRequestApi api) {}
}