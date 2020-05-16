package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyLog;
import com.hjq.http.callback.NormalCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : Get 请求
 */
public final class GetRequest extends BaseRequest<GetRequest> {

    private CallProxy mCallProxy;

    public GetRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @Override
    protected Request create(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder request = new Request.Builder();

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
        request.get().url(link);

        EasyLog.print("GetUrl", link.toString());
        return request.build();
    }

    /**
     * 执行请求
     */
    public GetRequest request(OnHttpListener listener) {
        mCallProxy = new CallProxy(create());
        mCallProxy.enqueue(new NormalCallback(getLifecycleOwner(), mCallProxy, listener));
        return this;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        mCallProxy.cancel();
    }
}