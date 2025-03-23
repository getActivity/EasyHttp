package com.hjq.http.config.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.http.config.IHttpCacheStrategy;
import com.hjq.http.request.HttpRequest;
import java.lang.reflect.Type;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2025/03/23
 *    desc   : 请求缓存默认实现的空策略
 */
public class EasyNullCacheStrategy implements IHttpCacheStrategy {

    @Nullable
    @Override
    public Object readCache(@NonNull HttpRequest<?> httpRequest, @NonNull Type type, long cacheTime) throws Throwable {
        return null;
    }

    @Override
    public boolean writeCache(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Object result) throws Throwable {
        return false;
    }

    @Override
    public boolean deleteCache(@NonNull HttpRequest<?> httpRequest) {
        return false;
    }

    @Override
    public void clearCache() {}
}