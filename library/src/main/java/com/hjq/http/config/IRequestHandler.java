package com.hjq.http.config;

import androidx.annotation.NonNull;
import com.hjq.http.EasyUtils;
import com.hjq.http.request.HttpRequest;
import java.lang.reflect.Type;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 请求处理策略
 */
public interface IRequestHandler {

    /**
     * 请求成功
     *
     * @param httpRequest   请求接口对象
     * @param response      响应对象
     * @param type          解析类型
     * @return              返回结果
     *
     * @throws Throwable    如果抛出则回调失败
     */
    @NonNull
    Object requestSuccess(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Type type) throws Throwable;

    /**
     * 请求失败
     *
     * @param httpRequest   请求接口对象
     * @param e             错误对象
     * @return              错误对象
     */
    @NonNull
    Throwable requestFail(@NonNull HttpRequest<?> httpRequest, @NonNull Throwable e);

    /**
     * 下载失败
     *
     * @param httpRequest   请求接口对象
     * @param throwable             错误对象
     * @return              错误对象
     */
    @NonNull
    default Throwable downloadFail(@NonNull HttpRequest<?> httpRequest, @NonNull Throwable throwable) {
        return requestFail(httpRequest, throwable);
    }

    /**
     * 解析泛型
     */
    default Type getGenericType(Object object) {
        return EasyUtils.getGenericType(object);
    }
}