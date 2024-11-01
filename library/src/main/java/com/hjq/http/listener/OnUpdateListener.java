package com.hjq.http.listener;

import androidx.annotation.NonNull;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/08/15
 *    desc   : 带上传进度回调的监听器
 */
public interface OnUpdateListener<T> extends OnHttpListener<T> {

    /**
     * 请求开始
     */
    @Override
    default void onHttpStart(@NonNull IRequestApi api) {
        onUpdateStart(api);
    }

    /**
     * 请求成功
     */
    @Override
    default void onHttpSuccess(@NonNull T result) {
        onUpdateSuccess(result);
    }

    /**
     * 请求出错
     */
    @Override
    default void onHttpFail(@NonNull Throwable throwable) {
        onUpdateFail(throwable);
    }

    /**
     * 请求结束
     */
    @Override
    default void onHttpEnd(@NonNull IRequestApi api) {
        onUpdateEnd(api);
    }

    /* --------------------------------------------------------------- */

    /**
     * 上传开始
     */
    default void onUpdateStart(@NonNull IRequestApi api) {}

    /**
     * 上传字节改变
     *
     * @param totalByte             总字节数
     * @param updateByte            已上传字节数
     */
    default void onUpdateByteChange(long totalByte, long updateByte) {}

    /**
     * 上传进度改变
     *
     * @param progress          上传进度值（0-100）
     */
    void onUpdateProgressChange(int progress);

    /**
     * 上传成功
     */
    void onUpdateSuccess(@NonNull T result);

    /**
     * 上传出错
     */
    void onUpdateFail(@NonNull Throwable throwable);

    /**
     * 上传结束
     */
    default void onUpdateEnd(@NonNull IRequestApi api) {}
}