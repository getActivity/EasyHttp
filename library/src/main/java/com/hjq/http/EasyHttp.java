package com.hjq.http;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.request.DeleteRequest;
import com.hjq.http.request.DownloadRequest;
import com.hjq.http.request.GetRequest;
import com.hjq.http.request.HeadRequest;
import com.hjq.http.request.PatchRequest;
import com.hjq.http.request.PostRequest;
import com.hjq.http.request.PutRequest;

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
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static GetRequest get(LifecycleOwner lifecycleOwner) {
        return new GetRequest(lifecycleOwner);
    }

    /**
     * Post 请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static PostRequest post(LifecycleOwner lifecycleOwner) {
        return new PostRequest(lifecycleOwner);
    }

    /**
     * Head 请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static HeadRequest head(LifecycleOwner lifecycleOwner) {
        return new HeadRequest(lifecycleOwner);
    }

    /**
     * Delete 请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static DeleteRequest delete(LifecycleOwner lifecycleOwner) {
        return new DeleteRequest(lifecycleOwner);
    }

    /**
     * Put 请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static PutRequest put(LifecycleOwner lifecycleOwner) {
        return new PutRequest(lifecycleOwner);
    }

    /**
     * Patch 请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static PatchRequest patch(LifecycleOwner lifecycleOwner) {
        return new PatchRequest(lifecycleOwner);
    }

    /**
     * 下载请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static DownloadRequest download(LifecycleOwner lifecycleOwner) {
        return new DownloadRequest(lifecycleOwner);
    }

    /**
     * 取消请求
     */
    public static void cancel(LifecycleOwner lifecycleOwner) {
        cancel(String.valueOf(lifecycleOwner));
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