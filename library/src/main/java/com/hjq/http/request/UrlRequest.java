package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

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
 *    desc   : url 类型请求
 */
public abstract class UrlRequest<T extends UrlRequest> extends BaseRequest<T> {

    private CacheControl mCacheControl;

    public UrlRequest(LifecycleOwner lifecycle) {
        super(lifecycle);
    }

    /**
     * 设置缓存模式
     */
    public T cache(CacheControl cacheControl) {
        mCacheControl = cacheControl;
        return (T) this;
    }

    @Override
    protected Request create(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
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
                builder.addEncodedQueryParameter(key, params.get(key).toString());
            }
        }
        HttpUrl link = builder.build();
        request.url(link);
        request.method(getMethod(), null);

        EasyLog.print("RequestUrl", link.toString());
        EasyLog.print("RequestMethod", getMethod());
        return request.build();
    }
}