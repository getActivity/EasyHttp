package com.hjq.http.request;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : Delete 请求（参数使用 Body 传递）
 */
public final class DeleteBodyRequest extends BodyRequest<DeleteBodyRequest> {

    public DeleteBodyRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @NonNull
    @Override
    public String getRequestMethod() {
        return HttpMethod.DELETE.toString();
    }
}