package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : 不带 RequestBody 的请求
 */
@SuppressWarnings("unchecked")
public abstract class UrlRequest<T extends UrlRequest> extends BaseRequest<T> {

    private CacheControl mCacheControl;

    public UrlRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    /**
     * 设置缓存模式
     */
    public T cache(CacheControl cacheControl) {
        mCacheControl = cacheControl;
        return (T) this;
    }

    @Override
    protected Request createRequest(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder request = new Request.Builder();
        if (mCacheControl != null) {
            request.cacheControl(mCacheControl);
        }

        if (tag != null) {
            request.tag(tag);
        }

        // 添加请求头
        if (!headers.isEmpty()) {
            for (String key : headers.getNames()) {
                request.addHeader(key, headers.get(key));
            }
        }

        HttpUrl.Builder builder = HttpUrl.get(url).newBuilder();
        // 添加参数
        if (!params.isEmpty()) {
            for (String key : params.getNames()) {
                builder.addEncodedQueryParameter(key, String.valueOf(params.get(key)));
            }
        }
        HttpUrl link = builder.build();
        request.url(link);
        request.method(getRequestMethod(), null);

        EasyLog.print("RequestUrl", String.valueOf(link));
        EasyLog.print("RequestMethod", getRequestMethod());

        // 打印请求头和参数的日志
        if (EasyConfig.getInstance().isLogEnabled()) {

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }

            for (String key : headers.getNames()) {
                EasyLog.print(key, headers.get(key));
            }

            if (!headers.isEmpty() && !params.isEmpty()) {
                EasyLog.print();
            }

            for (String key : params.getNames()) {
                EasyLog.print(key, String.valueOf(params.get(key)));
            }

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }
        }
        return request.build();
    }
}