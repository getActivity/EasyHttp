package com.hjq.http.request;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 主体请求
 */
public final class PostRequest extends BodyRequest<PostRequest> {

    public PostRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @NonNull
    @Override
    public String getRequestMethod() {
        return HttpMethod.POST.toString();
    }
}