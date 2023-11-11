package com.hjq.http;

import android.text.TextUtils;
import androidx.lifecycle.LifecycleOwner;
import com.hjq.http.request.DeleteBodyRequest;
import com.hjq.http.request.DeleteUrlRequest;
import com.hjq.http.request.DownloadRequest;
import com.hjq.http.request.GetRequest;
import com.hjq.http.request.HeadRequest;
import com.hjq.http.request.OptionsRequest;
import com.hjq.http.request.PatchRequest;
import com.hjq.http.request.PostRequest;
import com.hjq.http.request.PutRequest;
import com.hjq.http.request.TraceRequest;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求类
 */
@SuppressWarnings("unused")
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
     * Delete 请求（默认使用 Url 传递参数）
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static DeleteUrlRequest delete(LifecycleOwner lifecycleOwner) {
        return deleteByUrl(lifecycleOwner);
    }

    /**
     * Delete 请求（参数使用 Url 传递）
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static DeleteUrlRequest deleteByUrl(LifecycleOwner lifecycleOwner) {
        return new DeleteUrlRequest(lifecycleOwner);
    }

    /**
     * Delete 请求（参数使用 Body 传递）
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static DeleteBodyRequest deleteByBody(LifecycleOwner lifecycleOwner) {
        return new DeleteBodyRequest(lifecycleOwner);
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
     * Options 请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static OptionsRequest options(LifecycleOwner lifecycleOwner) {
        return new OptionsRequest(lifecycleOwner);
    }

    /**
     * Trace 请求
     *
     * @param lifecycleOwner      请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
     *                            如需传入其他对象请参考以下两个类
     *                            {@link com.hjq.http.lifecycle.ActivityLifecycle}
     *                            {@link com.hjq.http.lifecycle.ApplicationLifecycle}
     */
    public static TraceRequest trace(LifecycleOwner lifecycleOwner) {
        return new TraceRequest(lifecycleOwner);
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
     * 根据 TAG 取消请求任务
     */
    public static void cancelByTag(Object tag) {
        cancelByTag(EasyUtils.getObjectTag(tag));
    }

    public static void cancelByTag(String tag) {
        if (tag == null) {
            return;
        }

        OkHttpClient client = EasyConfig.getInstance().getClient();

        // 清除排队等候的任务
        for (Call call : client.dispatcher().queuedCalls()) {
            Object requestTag = call.request().tag();
            if (requestTag == null) {
                continue;
            }
            if (!TextUtils.equals(tag, String.valueOf(requestTag))) {
                continue;
            }
            call.cancel();
        }

        // 清除正在执行的任务
        for (Call call : client.dispatcher().runningCalls()) {
            Object requestTag = call.request().tag();
            if (requestTag == null) {
                continue;
            }
            if (!TextUtils.equals(tag, String.valueOf(requestTag))) {
                continue;
            }
            call.cancel();
        }

        // 移除延迟发起的网络请求
        EasyUtils.removeDelayedRunnable(tag.hashCode());
    }

    /**
     * 清除所有请求任务
     */
    public static void cancelAll() {
        OkHttpClient client = EasyConfig.getInstance().getClient();

        // 清除排队等候的任务
        for (Call call : client.dispatcher().queuedCalls()) {
            call.cancel();
        }

        // 清除正在执行的任务
        for (Call call : client.dispatcher().runningCalls()) {
            call.cancel();
        }

        // 移除延迟发起的网络请求
        EasyUtils.removeAllRunnable();
    }
}