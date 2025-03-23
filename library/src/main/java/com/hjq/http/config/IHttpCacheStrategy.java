package com.hjq.http.config;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.http.request.HttpRequest;
import java.lang.reflect.Type;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2025/03/23
 *    desc   : 请求缓存策略
 */
public interface IHttpCacheStrategy {

    /**
     * 读取缓存
     *
     * @param httpRequest   请求接口对象
     * @param type          解析类型
     * @param cacheTime     缓存的有效期（以毫秒为单位）
     * @return              返回新的请求对象
     */
    @Nullable
    Object readCache(@NonNull HttpRequest<?> httpRequest, @NonNull Type type, long cacheTime) throws Throwable;

    /**
     * 写入缓存
     *
     * @param httpRequest   请求接口对象
     * @param response      响应对象
     * @param result        请求结果对象
     * @return              缓存写入结果
     */
    boolean writeCache(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Object result) throws Throwable;

    /**
     * 删除缓存
     *
     * @param httpRequest   请求接口对象
     * @return              删除缓存的结果
     */
    boolean deleteCache(@NonNull HttpRequest<?> httpRequest);

    /**
     * 清空缓存
     */
    void clearCache();
} 