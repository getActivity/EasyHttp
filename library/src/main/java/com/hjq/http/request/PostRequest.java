package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 主体请求
 */
public final class PostRequest extends BodyRequest<PostRequest> {

    public PostRequest(LifecycleOwner lifecycle) {
        super(lifecycle);
    }

    @Override
    protected String getMethod() {
        return HttpMethod.POST.toString();
    }
}