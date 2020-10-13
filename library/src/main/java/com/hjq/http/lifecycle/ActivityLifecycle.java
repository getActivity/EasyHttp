package com.hjq.http.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/09/01
 *    desc   : Activity 生命周期策略
 */
public final class ActivityLifecycle implements
        LifecycleOwner, LifecycleEventObserver,
        Application.ActivityLifecycleCallbacks {

    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    private Activity mActivity;

    public ActivityLifecycle(Activity activity) {
        mActivity = activity;

        if (mActivity instanceof LifecycleOwner) {
            ((LifecycleOwner) mActivity).getLifecycle().addObserver(this);
        } else {
            mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    /**
     * {@link LifecycleOwner}
     */

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * {@link LifecycleEventObserver}
     */

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        mLifecycle.handleLifecycleEvent(event);
        switch (event) {
            case ON_DESTROY:
                source.getLifecycle().removeObserver(this);
                mActivity = null;
                break;
            default:
                break;
        }
    }

    /**
     * {@link Application.ActivityLifecycleCallbacks}
     */

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            mActivity = null;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
}