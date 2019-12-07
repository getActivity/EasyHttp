package com.hjq.http;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;

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
    public static GetRequest get(Activity activity) {
        return new GetRequest(activity).tag(activity);
    }

    public static GetRequest get(Fragment fragment) {
        return new GetRequest(fragment.getActivity()).tag(fragment);
    }

    public static GetRequest get(android.support.v4.app.Fragment fragment) {
        return new GetRequest(fragment.getContext()).tag(fragment);
    }

    public static GetRequest get(Dialog dialog) {
        return new GetRequest(dialog.getContext()).tag(dialog);
    }

    public static GetRequest get(Context context) {
        return new GetRequest(context).tag(context);
    }

    /**
     * Post 请求
     */
    public static PostRequest post(Activity activity) {
        return new PostRequest(activity).tag(activity);
    }

    public static PostRequest post(Fragment fragment) {
        return new PostRequest(fragment.getActivity()).tag(fragment);
    }

    public static PostRequest post(android.support.v4.app.Fragment fragment) {
        return new PostRequest(fragment.getContext()).tag(fragment);
    }

    public static PostRequest post(Dialog dialog) {
        return new PostRequest(dialog.getContext()).tag(dialog);
    }
    public static PostRequest post(Context context) {
        return new PostRequest(context).tag(context);
    }

    /**
     * 下载请求
     */
    public static DownloadRequest download(Activity activity) {
        return new DownloadRequest(activity).tag(activity);
    }

    public static DownloadRequest download(Fragment fragment) {
        return new DownloadRequest(fragment.getActivity()).tag(fragment);
    }

    public static DownloadRequest download(android.support.v4.app.Fragment fragment) {
        return new DownloadRequest(fragment.getContext()).tag(fragment);
    }

    public static DownloadRequest download(Dialog dialog) {
        return new DownloadRequest(dialog.getContext()).tag(dialog);
    }

    public static DownloadRequest download(Context context) {
        return new DownloadRequest(context).tag(context);
    }

    /**
     * 取消请求
     */
    public static void cancel(Object tag) {
        if (tag != null) {
            cancel(tag.toString());
        } else {
            cancel(null);
        }
    }

    public static void cancel(String tag) {
        OkHttpClient client = EasyConfig.getInstance().getClient();

        // 清除排队等候的任务
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag != null) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            } else {
                call.cancel();
            }
        }

        // 清除正在执行的任务
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag != null) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            } else {
                call.cancel();
            }
        }
    }
}