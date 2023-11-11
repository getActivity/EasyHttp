package com.hjq.http.config;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.http.EasyUtils;
import com.hjq.http.request.HttpRequest;
import java.io.File;
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
     * 下载开始
     *
     * @param httpRequest   请求接口对象
     * @param file          下载的文件对象
     */
    default void downloadStart(@NonNull HttpRequest<?> httpRequest, @NonNull File file) {}

    /**
     * 下载成功
     *
     * @param httpRequest   请求接口对象
     * @param response      响应对象
     * @param file          下载的文件对象
     */
    default void downloadSuccess(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull File file) throws Throwable {}

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
     * 读取缓存
     *
     * @param httpRequest   请求接口对象
     * @param cacheTime     缓存的有效期（以毫秒为单位）
     * @return              返回新的请求对象
     */
    @Nullable
    default Object readCache(@NonNull HttpRequest<?> httpRequest, @NonNull Type type, long cacheTime) throws Throwable {
        return null;
    }

    /**
     * 写入缓存
     *
     * @param httpRequest   请求接口对象
     * @param result        请求结果对象
     * @return              缓存写入结果
     */
    default boolean writeCache(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Object result) throws Throwable {
        return false;
    }

    /**
     * 清空缓存
     */
    default void clearCache() {}

    /**
     * 解析泛型
     */
    default Type getGenericType(Object object) {
        return EasyUtils.getGenericType(object);
    }
}