package com.hjq.http.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyHttp;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/04/05
 *    desc   : 请求生命周期控制
 */
public class HttpLifecycle implements LifecycleEventObserver {

    public static void with(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(new HttpLifecycle());
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            source.getLifecycle().removeObserver(this);
            EasyHttp.cancel(source);
        }
    }
}