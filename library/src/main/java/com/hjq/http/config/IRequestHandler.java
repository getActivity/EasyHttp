package com.hjq.http.config;

import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.Type;

import okhttp3.Request;
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
     *
     * @param lifecycle     有生命周期的对象（例如 Activity、Fragment）
     * @param request       请求对象
     * @return              返回新的请求对象
     */
    default Request requestStart(LifecycleOwner lifecycle, IRequestApi api, Request request) {
        return request;
    }

    /**
     * 请求成功时回调
     *
     * @param lifecycle     有生命周期的对象（例如 Activity、Fragment）
     * @param response      响应对象
     * @param type          解析类型
     * @return              返回结果
     *
     * @throws Exception    如果抛出则回调失败
     */
    Object requestSucceed(LifecycleOwner lifecycle, IRequestApi api, Response response, Type type) throws Exception;

    /**
     * 请求失败
     *
     * @param lifecycle     有生命周期的对象（例如 Activity、Fragment）
     * @param e             错误对象
     * @return              错误对象
     */
    Exception requestFail(LifecycleOwner lifecycle, IRequestApi api, Exception e);
}