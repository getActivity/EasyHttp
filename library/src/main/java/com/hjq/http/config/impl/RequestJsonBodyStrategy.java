package com.hjq.http.config.impl;

import com.hjq.http.EasyUtils;
import com.hjq.http.body.JsonRequestBody;
import com.hjq.http.config.IRequestBodyStrategy;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import okhttp3.RequestBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2023/09/23
 *    desc   : RequestBody Json 策略实现接口
 */
public class RequestJsonBodyStrategy implements IRequestBodyStrategy {

    @Override
    public void addParams(HttpParams params, String key, Object value) {
        // Json 提交
        params.put(key, EasyUtils.convertObject(value));
    }

    @Override
    public RequestBody createRequestBody(HttpRequest<?> httpRequest, HttpParams params) {
        return new JsonRequestBody(params.getParams());
    }
}