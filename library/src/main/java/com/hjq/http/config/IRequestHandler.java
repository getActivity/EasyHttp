package com.hjq.http.config;

import android.content.Context;

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
     * 请求开始时回调
     *
     * @param context           上下文对象
     * @param call              执行对象
     */
    void requestStart(Context context, Call call);

    /**
     * 请求结束时回调
     *
     * @param context           上下文对象
     * @param call              执行对象
     */
    void requestEnd(Context context, Call call);

    /**
     * 请求成功时回调
     *
     * @param context           上下文对象
     * @param response          响应对象
     * @param clazz             解析类型
     * @return                  返回结果
     * @throws Exception        回调失败方法
     */
    Object requestSucceed(Context context, Response response, Class clazz) throws Exception;

    /**
     * 请求失败
     * @param context           上下文对象
     * @param e                 错误对象
     * @return                  错误对象
     */
    Exception requestFail(Context context, Exception e);
}