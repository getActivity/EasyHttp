package com.hjq.http.lifecycle;

import android.app.Service;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2021/03/08
 *    desc   : Service 生命周期管理基类
 */
public abstract class LifecycleService extends Service implements LifecycleOwner {

    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }
}