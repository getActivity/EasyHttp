package com.hjq.http.config;

import com.hjq.http.request.HttpRequest;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 请求处理器
 */
public interface IRequestHandler {

    /**
     * 请求成功时回调
     *
     * @param httpRequest   请求接口对象
     * @param response      响应对象
     * @param type          解析类型
     * @return              返回结果
     *
     * @throws Exception    如果抛出则回调失败
     */
    Object requestSucceed(HttpRequest<?> httpRequest, Response response, Type type) throws Exception;

    /**
     * 请求失败
     *
     * @param httpRequest   请求接口对象
     * @param e             错误对象
     * @return              错误对象
     */
    Exception requestFail(HttpRequest<?> httpRequest, Exception e);

    /**
     * 读取缓存
     *
     * @param httpRequest   请求接口对象
     * @param cacheTime     缓存的有效期（以毫秒为单位）
     * @return              返回新的请求对象
     */
    default Object readCache(HttpRequest<?> httpRequest, Type type, long cacheTime) {
        return null;
    }

    /**
     * 写入缓存
     *
     * @param httpRequest   请求接口对象
     * @param result        请求结果对象
     * @return              缓存写入结果
     */
    default boolean writeCache(HttpRequest<?> httpRequest, Response response, Object result) {
        return false;
    }
}