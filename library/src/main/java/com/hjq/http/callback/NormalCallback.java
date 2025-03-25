package com.hjq.http.callback;

import androidx.annotation.NonNull;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.CacheMode;
import com.hjq.http.request.HttpRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/25
 *    desc   : 正常接口回调
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class NormalCallback extends BaseCallback {

    /** 请求配置 */
    private final HttpRequest mHttpRequest;
    /** 接口回调 */
    private OnHttpListener mListener;
    /** 解析类型 */
    private Type mReflectType;

    public NormalCallback(@NonNull HttpRequest request) {
        super(request);
        mHttpRequest = request;
    }

    public NormalCallback setListener(OnHttpListener listener) {
        mListener = listener;
        mReflectType = mHttpRequest.getRequestHandler().getGenericType(mListener);
        return this;
    }

    public NormalCallback setReflectType(Type reflectType) {
        mReflectType = reflectType;
        return this;
    }

    @Override
    public void start() {
        CacheMode cacheMode = mHttpRequest.getRequestCacheConfig().getCacheMode();
        if (cacheMode != CacheMode.USE_CACHE_ONLY &&
                cacheMode != CacheMode.USE_CACHE_FIRST) {
            super.start();
            return;
        }

        try {
            Object result = mHttpRequest.getHttpCacheStrategy().readCache(mHttpRequest,
                    mReflectType, mHttpRequest.getRequestCacheConfig().getCacheTime());
            EasyLog.printLog(mHttpRequest, "ReadCache result：" + result);

            // 如果没有缓存，就请求网络
            if (result == null) {
                super.start();
                return;
            }

            // 读取缓存成功
            EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> {
                dispatchHttpStartCallback();
                dispatchHttpSuccessCallback(result, true);
            });

            // 如果当前模式是先读缓存再写请求
            if (cacheMode == CacheMode.USE_CACHE_FIRST) {
                EasyUtils.postDelayedRunnable(() -> {
                    if (!HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
                        return;
                    }
                    // 将回调置为空，避免出现两次回调
                    mListener = null;
                    super.start();
                }, 1);
            }

        } catch (Throwable cacheThrowable) {
            EasyLog.printLog(mHttpRequest, "ReadCache error");
            EasyLog.printThrowable(mHttpRequest, cacheThrowable);
            super.start();
        }
    }

    @Override
    protected void onStart() {
        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), this::dispatchHttpStartCallback);
    }

    @Override
    protected void onHttpResponse(Response response) throws Throwable {
        // 打印请求耗时时间
        EasyLog.printLog(mHttpRequest, "RequestConsuming：" +
                (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) + " ms");

        IRequestInterceptor interceptor = mHttpRequest.getRequestInterceptor();
        if (interceptor != null) {
            response = interceptor.interceptResponse(mHttpRequest, response);
        }

        // 解析 Bean 类对象
        final Object result = mHttpRequest.getRequestHandler().requestSuccess(
                mHttpRequest, response, mReflectType);

        CacheMode cacheMode = mHttpRequest.getRequestCacheConfig().getCacheMode();
        if (cacheMode == CacheMode.USE_CACHE_ONLY ||
                cacheMode == CacheMode.USE_CACHE_FIRST ||
                cacheMode == CacheMode.USE_CACHE_AFTER_FAILURE) {
            try {
                boolean writeCacheResult = mHttpRequest.getHttpCacheStrategy().writeCache(mHttpRequest, response, result);
                EasyLog.printLog(mHttpRequest, "write cache result：" + writeCacheResult);
            } catch (Throwable cacheThrowable) {
                EasyLog.printLog(mHttpRequest, "write cache error");
                EasyLog.printThrowable(mHttpRequest, cacheThrowable);
            }
        }

        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchHttpSuccessCallback(result, false));
    }

    @Override
    protected void onHttpFailure(Throwable throwable) {
        // 打印错误堆栈
        EasyLog.printThrowable(mHttpRequest, throwable);
        // 如果设置了只在网络请求失败才去读缓存
        if (throwable instanceof IOException && mHttpRequest.getRequestCacheConfig().getCacheMode() == CacheMode.USE_CACHE_AFTER_FAILURE) {
            try {
                Object result = mHttpRequest.getHttpCacheStrategy().readCache(mHttpRequest,
                        mReflectType, mHttpRequest.getRequestCacheConfig().getCacheTime());
                EasyLog.printLog(mHttpRequest, "ReadCache result：" + result);
                if (result != null) {
                    EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchHttpSuccessCallback(result, true));
                    return;
                }
            } catch (Throwable cacheThrowable) {
                EasyLog.printLog(mHttpRequest, "ReadCache error");
                EasyLog.printThrowable(mHttpRequest, cacheThrowable);
            }
        }

        final Throwable finalThrowable = mHttpRequest.getRequestHandler().requestFail(mHttpRequest, throwable);
        if (finalThrowable != throwable) {
            EasyLog.printThrowable(mHttpRequest, finalThrowable);
        }

        EasyUtils.runOnAssignThread(mHttpRequest.getThreadSchedulers(), () -> dispatchHttpFailCallback(finalThrowable));
    }

    private void dispatchHttpStartCallback() {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onHttpStart(mHttpRequest.getRequestApi());
        }
        EasyLog.printLog(mHttpRequest,  "Http request start");
    }

    private void dispatchHttpSuccessCallback(@NonNull Object result, boolean cache) {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onHttpSuccess(result, cache);
            mListener.onHttpEnd(mHttpRequest.getRequestApi());
        }
        EasyLog.printLog(mHttpRequest,  "Http request success");
    }

    private void dispatchHttpFailCallback(Throwable throwable) {
        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mHttpRequest.getLifecycleOwner())) {
            mListener.onHttpFail(throwable);
            mListener.onHttpEnd(mHttpRequest.getRequestApi());
        }
        EasyLog.printLog(mHttpRequest,  "Http request fail");
    }

    @Override
    protected void closeResponse(Response response) {
        if (Response.class.equals(mReflectType) ||
                ResponseBody.class.equals(mReflectType) ||
                InputStream.class.equals(mReflectType)) {
            // 如果反射是这几个类型，则不关闭 Response，否则会导致拉取不到里面的流
            return;
        }
        super.closeResponse(response);
    }
}