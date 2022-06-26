package com.hjq.http.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/09/01
 *    desc   : 全局的生命周期策略
 */
public final class ApplicationLifecycle implements LifecycleOwner {

    private static final ApplicationLifecycle INSTANCE = new ApplicationLifecycle();

    public static ApplicationLifecycle getInstance() {
        return INSTANCE;
    }

    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    private ApplicationLifecycle() {}

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }
}