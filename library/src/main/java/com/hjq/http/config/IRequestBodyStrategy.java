package com.hjq.http.config;

import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import okhttp3.RequestBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2023/09/23
 *    desc   : 请求 Body 策略接口
 */
public interface IRequestBodyStrategy {

    /**
     * 添加参数
     */
    void addParams(HttpParams params, String key, Object value);

    /**
     * 创建 RequestBody
     */
    RequestBody createRequestBody(HttpRequest<?> httpRequest, HttpParams params);
}