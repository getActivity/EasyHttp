package com.hjq.http.request;

import android.content.Context;

import com.hjq.http.EasyConfig;
import com.hjq.http.callback.CommonCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : Get 请求
 */
public final class GetRequest extends BaseRequest<GetRequest> {

    public GetRequest(Context context) {
        super(context);
    }

    @Override
    protected Request create(String url, String tag, HttpParams params, HttpHeaders headers) {
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

        HttpUrl.Builder body = HttpUrl.get(url).newBuilder();
        // 添加参数
        if (!params.isEmpty()) {
            for (String key : params.getNames()) {
                body.addEncodedQueryParameter(key, params.get(key));
            }
        }

        request.get().url(body.build());
        return request.build();
    }

    /**
     * 执行请求
     */
    public void request(OnHttpListener listener) {
        Call call = create();
        EasyConfig.getInstance().getHandler().requestStart(getContext(), call);
        call.enqueue(new CommonCallback(getContext(), listener));
    }
}