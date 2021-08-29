package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.body.JsonBody;
import com.hjq.http.body.ProgressBody;
import com.hjq.http.body.StringBody;
import com.hjq.http.body.UpdateBody;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CacheMode;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : 带 RequestBody 请求
 */
@SuppressWarnings("unchecked")
public abstract class BodyRequest<T extends BodyRequest<?>> extends BaseRequest<T> {

    private OnUpdateListener<?> mUpdateListener;

    private RequestBody mRequestBody;

    public BodyRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    /**
     * 自定义 json 字符串
     */
    public T json(Map<?, ?> map) {
        if (map == null) {
            return (T) this;
        }
        return body(new JsonBody(map));
    }

    public T json(List<?> list) {
        if (list == null) {
            return (T) this;
        }
        return body(new JsonBody(list));
    }

    public T json(String json) {
        if (json == null) {
            return (T) this;
        }
        return body(new JsonBody(json));
    }

    /**
     * 自定义文本字符串
     */
    public T text(String text) {
        if (text == null) {
            return (T) this;
        }
        return body(new StringBody(text));
    }

    /**
     * 自定义 RequestBody
     */
    public T body(RequestBody body) {
        mRequestBody = body;
        return (T) this;
    }

    @Override
    protected Request createRequest(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);

        EasyLog.print("RequestUrl", url);
        EasyLog.print("RequestMethod", getRequestMethod());

        if (tag != null) {
            requestBuilder.tag(tag);
        }

        // 如果设置了不缓存数据
        if (getRequestCache().getMode() == CacheMode.NO_CACHE) {
            requestBuilder.cacheControl(new CacheControl.Builder().noCache().build());
        }

        // 添加请求头
        if (!headers.isEmpty()) {
            for (String key : headers.getNames()) {
                requestBuilder.addHeader(key, headers.get(key));
            }
        }

        RequestBody body = mRequestBody != null ? mRequestBody : createBody(params, type);
        requestBuilder.method(getRequestMethod(), body);

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

            if (body instanceof FormBody ||
                    body instanceof MultipartBody ||
                    body instanceof ProgressBody) {
                // 打印表单
                for (String key : params.getNames()) {
                    Object value = params.get(key);
                    if (value instanceof String) {
                        EasyLog.print(key, "\"" + value + "\"");
                    } else {
                        EasyLog.print(key, String.valueOf(value));
                    }
                }
            } else if (body instanceof JsonBody) {
                // 打印 Json
                EasyLog.json(body.toString());
            } else {
                // 打印文本
                EasyLog.print(body.toString());
            }

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }
        }

        return getRequestHandler().requestStart(getLifecycleOwner(), getRequestApi(), requestBuilder.build());
    }

    /**
     * 执行异步请求（执行传入上传进度监听器）
     */
    @Override
    public void request(OnHttpListener<?> listener) {
        if (listener instanceof OnUpdateListener) {
            mUpdateListener = (OnUpdateListener<?>) listener;
        }
        super.request(listener);
    }

    /**
     * 组装 RequestBody 对象
     */
    private RequestBody createBody(HttpParams params, BodyType type) {
        RequestBody requestBody;

        if (params.isMultipart() && !params.isEmpty()) {
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
            bodyBuilder.setType(MultipartBody.FORM);
            for (String key : params.getNames()) {
                Object object = params.get(key);

                // 如果这是一个 File 对象
                if (object instanceof File) {
                    MultipartBody.Part part = EasyUtils.createPart(key, (File) object);
                    if (part != null) {
                        bodyBuilder.addPart(part);
                    }
                    continue;
                }

                // 如果这是一个 InputStream 对象
                if (object instanceof InputStream) {
                    MultipartBody.Part part = EasyUtils.createPart(key, (InputStream) object);
                    if (part != null) {
                        bodyBuilder.addPart(part);
                    }
                    continue;
                }

                // 如果这是一个自定义的 MultipartBody.Part 对象
                if (object instanceof MultipartBody.Part) {
                    bodyBuilder.addPart((MultipartBody.Part) object);
                    continue;
                }

                // 如果这是一个自定义的 RequestBody 对象
                if (object instanceof RequestBody) {
                    if (object instanceof UpdateBody) {
                        bodyBuilder.addFormDataPart(key, EasyUtils.encodeString(((UpdateBody) object).getName()), (RequestBody) object);
                    } else {
                        bodyBuilder.addFormDataPart(key, null, (RequestBody) object);
                    }
                    continue;
                }

                // 如果这是一个 List<File> 对象
                if (object instanceof List && EasyUtils.isFileList((List<?>) object)) {
                    for (Object item : (List<?>) object) {
                        MultipartBody.Part part = EasyUtils.createPart(key, (File) item);
                        if (part != null) {
                            bodyBuilder.addPart(part);
                        }
                    }
                    continue;
                }

                // 如果这是一个普通参数
                bodyBuilder.addFormDataPart(key, String.valueOf(object));
            }

            try {
                requestBody = bodyBuilder.build();
            } catch (IllegalStateException ignored) {
                // 如果参数为空则会抛出异常：Multipart body must have at least one part.
                requestBody = new FormBody.Builder().build();
            }

        } else if (type == BodyType.JSON) {
            requestBody = new JsonBody(params.getParams());
        } else {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            if (!params.isEmpty()) {
                for (String key : params.getNames()) {
                    bodyBuilder.add(key, String.valueOf(params.get(key)));
                }
            }
            requestBody = bodyBuilder.build();
        }

        return mUpdateListener == null ? requestBody : new ProgressBody(requestBody, getLifecycleOwner(), mUpdateListener);
    }
}