package com.hjq.http.request;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.model.HttpMethod;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2021/04/29
 *    desc   : Options 请求
 */
public final class OptionsRequest extends UrlRequest<OptionsRequest> {

    public OptionsRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @NonNull
    @Override
    public String getRequestMethod() {
        return HttpMethod.OPTIONS.toString();
    }
}