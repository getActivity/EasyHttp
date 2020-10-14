package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : Delete 请求
 */
public final class DeleteRequest extends BodyRequest<DeleteRequest> {

    public DeleteRequest(LifecycleOwner lifecycle) {
        super(lifecycle);
    }

    @Override
    protected String getMethod() {
        return HttpMethod.DELETE.toString();
    }
}