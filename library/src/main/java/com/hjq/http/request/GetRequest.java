package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : Get 请求
 */
public final class GetRequest extends UrlRequest<GetRequest> {

    public GetRequest(LifecycleOwner lifecycle) {
        super(lifecycle);
    }

    @Override
    protected String getMethod() {
        return HttpMethod.GET.toString();
    }
}