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
     * @param api           请求接口对象
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
     * @param api           请求接口对象
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
     * @param api           请求接口对象
     * @param e             错误对象
     * @return              错误对象
     */
    Exception requestFail(LifecycleOwner lifecycle, IRequestApi api, Exception e);

    /**
     * 读取缓存
     *
     * @param lifecycle     有生命周期的对象（例如 Activity、Fragment）
     * @param api           请求接口对象
     * @return              返回新的请求对象
     */
    default Object readCache(LifecycleOwner lifecycle, IRequestApi api, Type type) throws Throwable {
        return null;
    }

    /**
     * 写入缓存
     *
     * @param lifecycle     有生命周期的对象（例如 Activity、Fragment）
     * @param api           请求接口对象
     * @param result        请求结果对象
     * @return              缓存写入结果
     */
    default boolean writeCache(LifecycleOwner lifecycle, IRequestApi api, Response response, Object result) throws Throwable {
        return false;
    }
}