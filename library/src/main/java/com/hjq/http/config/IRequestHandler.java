package com.hjq.http.config;

import android.content.Context;

import com.hjq.http.listener.OnHttpListener;

import okhttp3.Call;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 请求处理器
 */
public interface IRequestHandler {

    /**
     * 请求开始
     */
    void requestStart(Context context, Call call);

    /**
     * 请求结束
     */
    void requestEnd(Context context, Call call);

    /**
     * 请求成功
     */
    Object requestSucceed(Context context, Call call, Response response, OnHttpListener listener) throws Exception;

    /**
     * 请求失败
     */
    Exception requestFail(Context context, Call call, Exception e, OnHttpListener listener);
}