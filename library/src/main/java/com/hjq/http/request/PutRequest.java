package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : Put 请求
 */
public final class PutRequest extends BodyRequest<PutRequest> {

    public PutRequest(LifecycleOwner lifecycle) {
        super(lifecycle);
    }

    @Override
    protected String getMethod() {
        return HttpMethod.PUT.toString();
    }
}