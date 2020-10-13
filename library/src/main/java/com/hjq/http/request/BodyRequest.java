package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.body.JsonObjectBody;
import com.hjq.http.body.MultipartBodyProxy;
import com.hjq.http.body.UpdateBody;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : Post 请求
 */
public abstract class BodyRequest<T extends BodyRequest> extends BaseRequest<T> {

    private OnUpdateListener mListener;

    private RequestBody mRequestBody;

    public BodyRequest(LifecycleOwner lifecycle) {
        super(lifecycle);
    }

    /**
     * 自定义请求参数的 Map
     */
    public T body(Map map) {
        return body(new JsonObjectBody(map));
    }

    /**
     * 自定义请求 Body
     */
    public T body(RequestBody body) {
        mRequestBody = body;
        return (T) this;
    }

    @Override
    protected Request create(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder request = new Request.Builder();
        request.url(url);

        EasyLog.print("RequestUrl", url);
        EasyLog.print("RequestMethod", getMethod());

        if (tag != null) {
            request.tag(tag);
        }

        // 添加请求头
        if (!headers.isEmpty()) {
            for (String key : headers.getNames()) {
                request.addHeader(key, headers.get(key));
            }
        }

        RequestBody body = mRequestBody;
        if (mRequestBody == null) {
            if (params.isMultipart() && !params.isEmpty()) {
                MultipartBodyProxy.Builder builder = new MultipartBodyProxy.Builder();
                builder.setType(MultipartBody.FORM);
                builder.setLifecycleOwner(getLifecycle());
                builder.setOnUpdateListener(mListener);
                for (String key : params.getNames()) {
                    Object object = params.get(key);
                    if (object instanceof File) {
                        // 如果这是一个文件
                        MultipartBody.Part part = MultipartBodyProxy.createPart(key, (File) object);
                        if (part != null) {
                            builder.addPart(part);
                        }
                    } else if (object instanceof InputStream) {
                        // 如果这是一个输入流
                        MultipartBody.Part part = MultipartBodyProxy.createPart(key, (InputStream) object);
                        if (part != null) {
                            builder.addPart(part);
                        }
                    } else if (object instanceof RequestBody) {
                        // 如果这是一个自定义 RequestBody
                        if (object instanceof UpdateBody) {
                            builder.addFormDataPart(key, EasyUtils.encodeString(((UpdateBody) object).getName()), (RequestBody) object);
                        } else {
                            builder.addFormDataPart(key, null, (RequestBody) object);
                        }
                    } else {
                        if (object instanceof List && EasyUtils.isFileList((List) object)) {
                            // 上传文件列表
                            for (Object item : (List) object) {
                                MultipartBody.Part part = MultipartBodyProxy.createPart(key, (File) item);
                                if (part != null) {
                                    builder.addPart(part);
                                }
                            }
                        } else {
                            // 如果这是一个普通参数
                            builder.addFormDataPart(key, object.toString());
                        }
                    }
                }
                try {
                    body = builder.build();
                } catch (IllegalStateException ignore) {
                    // 如果里面没有任何参数会抛出异常
                    // java.lang.IllegalStateException: Multipart body must have at least one part.
                    body = new FormBody.Builder().build();
                }
            } else {
                if (type == BodyType.JSON) {
                    if (!params.isEmpty()) {
                        body = new JsonObjectBody(params.getParams());
                    } else {
                        body = new JsonObjectBody();
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
        }
        request.method(getMethod(), body);

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

            if (body instanceof JsonObjectBody) {
                EasyLog.json(((JsonObjectBody) body).getJsonObject().toString());
            } else {
                for (String key : params.getNames()) {
                    Object value = params.get(key);
                    if (value instanceof String) {
                        EasyLog.print(key, "\"" + value.toString() + "\"");
                    } else {
                        EasyLog.print(key, value.toString());
                    }
                }
            }

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }
        }

        return request.build();
    }

    /**
     * 执行异步请求（执行传入上传进度监听器）
     */
    @Override
    public T request(OnHttpListener listener) {
        if (listener instanceof OnUpdateListener) {
            mListener = (OnUpdateListener) listener;
        }
        return super.request(listener);
    }
}