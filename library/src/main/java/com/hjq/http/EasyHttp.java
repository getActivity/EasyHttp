package com.hjq.http;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.request.DownloadRequest;
import com.hjq.http.request.GetRequest;
import com.hjq.http.request.PostRequest;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求类
 */
public final class EasyHttp {

    /**
     * Get 请求
     */
    public static GetRequest get(LifecycleOwner lifecycleOwner) {
        return new GetRequest(lifecycleOwner).tag(lifecycleOwner);
    }

    /**
     * Post 请求
     */
    public static PostRequest post(LifecycleOwner lifecycleOwner) {
        return new PostRequest(lifecycleOwner).tag(lifecycleOwner);
    }

    /**
     * 下载请求
     */
    public static DownloadRequest download(LifecycleOwner lifecycleOwner) {
        return new DownloadRequest(lifecycleOwner).tag(lifecycleOwner);
    }

    /**
     * 取消请求
     */
    public static void cancel(LifecycleOwner lifecycleOwner) {
        cancel(lifecycleOwner.getLifecycle().toString());
    }

    /**
     * 根据 TAG 取消请求任务
     */
    public static void cancel(Object tag) {
        if (tag == null) {
            return;
        }

        OkHttpClient client = EasyConfig.getInstance().getClient();

        // 清除排队等候的任务
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

        // 清除正在执行的任务
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 清除所有请求任务
     */
    public static void cancel() {
        OkHttpClient client = EasyConfig.getInstance().getClient();

        // 清除排队等候的任务
        for (Call call : client.dispatcher().queuedCalls()) {
            call.cancel();
        }

        // 清除正在执行的任务
        for (Call call : client.dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}