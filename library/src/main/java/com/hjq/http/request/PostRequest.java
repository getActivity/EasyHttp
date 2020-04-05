package com.hjq.http.request;

import android.content.Context;

import com.hjq.http.EasyLog;
import com.hjq.http.callback.NormalCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.model.JsonBody;
import com.hjq.http.model.UpdateBody;

import java.io.File;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : Post 请求
 */
public final class PostRequest extends BaseRequest<PostRequest> {

    private CallProxy mCallProxy;

    public PostRequest(Context context) {
        super(context);
    }

    @Override
    protected Request create(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder request = new Request.Builder();
        request.url(url);
        if (tag != null) {
            request.tag(tag);
        }

        // 添加请求头
        if (!headers.isEmpty()) {
            for (String key : headers.getNames()) {
                request.addHeader(key, headers.get(key));
            }
        }

        RequestBody body;
        if (params.isMultipart()) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            if (!params.isEmpty()) {
                for (String key : params.getNames()) {
                    Object object = params.get(key);
                    // 如果这是一个文件
                    if (object instanceof File) {
                        File file = (File) object;
                        if (file.exists() && file.isFile()) {
                            // 文件名必须不能带中文，所以这里要编码
                            builder.addFormDataPart(key, URLEncoder.encode(file.getName()), new UpdateBody(file));
                        }
                    } else if (object instanceof RequestBody) {
                        builder.addFormDataPart(key, null, (RequestBody) object);
                    } else {
                        // 如果这是一个参数
                        builder.addFormDataPart(key, object.toString());
                    }
                }
            }
            body = builder.build();
        } else {
            if (type == BodyType.JSON) {
                if (!params.isEmpty()) {
                    body = new JsonBody(params.getParams());
                } else {
                    body = new JsonBody();
                }
            } else {
                FormBody.Builder builder = new FormBody.Builder();
                if (!params.isEmpty()) {
                    for (String key : params.getNames()) {
                        builder.add(key, params.get(key).toString());
                    }
                }
                body = builder.build();
            }
        }
        request.post(body);

        if (EasyLog.isEnable()) {
            EasyLog.print("PostUrl " + url);

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }

            for (String key : headers.getNames()) {
                EasyLog.print(key, headers.get(key));
            }

            if (!headers.isEmpty() && !params.isEmpty()) {
                EasyLog.print();
            }

            if (body instanceof JsonBody) {
                EasyLog.json(((JsonBody) body).getJsonObject().toString());
            } else {
                for (String key : params.getNames()) {
                    EasyLog.print(key, params.get(key).toString());
                }
            }

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }
        }
        return request.build();
    }

    /**
     * 执行请求
     */
    public PostRequest request(OnHttpListener listener) {
        mCallProxy = new CallProxy(create());
        mCallProxy.enqueue(new NormalCallback(getContext(), mCallProxy, listener));
        return this;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        mCallProxy.cancel();
    }
}