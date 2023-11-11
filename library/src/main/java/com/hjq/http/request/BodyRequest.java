package com.hjq.http.request;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.body.CustomTypeBody;
import com.hjq.http.body.JsonRequestBody;
import com.hjq.http.body.ProgressMonitorRequestBody;
import com.hjq.http.body.TextRequestBody;
import com.hjq.http.config.IRequestBodyStrategy;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import java.util.List;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.MediaType;
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
public abstract class BodyRequest<T extends BodyRequest<?>> extends HttpRequest<T> {

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
        return body(new JsonRequestBody(map));
    }

    public T json(List<?> list) {
        if (list == null) {
            return (T) this;
        }
        return body(new JsonRequestBody(list));
    }

    public T json(String json) {
        if (json == null) {
            return (T) this;
        }
        return body(new JsonRequestBody(json));
    }

    /**
     * 自定义文本字符串
     */
    public T text(String text) {
        if (text == null) {
            return (T) this;
        }
        return body(new TextRequestBody(text));
    }

    /**
     * 自定义 RequestBody
     */
    public T body(RequestBody body) {
        mRequestBody = body;
        return (T) this;
    }

    /**
     * 执行异步请求（执行传入上传进度监听器）
     */
    @Override
    public void request(@Nullable OnHttpListener<?> listener) {
        if (listener instanceof OnUpdateListener) {
            mUpdateListener = (OnUpdateListener<?>) listener;
        }
        if (mRequestBody != null) {
            mRequestBody = new ProgressMonitorRequestBody(this, mRequestBody, getLifecycleOwner(), mUpdateListener);
        }
        super.request(listener);
    }

    @Override
    protected void addHttpParams(HttpParams params, String key, Object value,
                                    IRequestBodyStrategy requestBodyStrategy) {
        requestBodyStrategy.addParams(params, key, value);
    }

    @Override
    protected void addRequestParams(Request.Builder requestBuilder, HttpParams params,
                                    @Nullable String contentType, IRequestBodyStrategy requestBodyStrategy) {
        RequestBody body = mRequestBody != null ? mRequestBody : createRequestBody(params, contentType, requestBodyStrategy);
        requestBuilder.method(getRequestMethod(), body);
    }

    @Override
    protected void printRequestLog(Request request, HttpParams params,
                                    HttpHeaders headers, IRequestBodyStrategy requestBodyStrategy) {
        if (!EasyConfig.getInstance().isLogEnabled()) {
            return;
        }

        EasyLog.printKeyValue(this, "RequestUrl", String.valueOf(request.url()));
        EasyLog.printKeyValue(this, "RequestMethod", getRequestMethod());

        RequestBody body = request.body();

        // 打印请求头和参数的日志
        if (!headers.isEmpty() || !params.isEmpty()) {
            EasyLog.printLine(this);
        }

        for (String key : headers.getKeys()) {
            EasyLog.printKeyValue(this, key, headers.get(key));
        }

        if (!headers.isEmpty() && !params.isEmpty()) {
            EasyLog.printLine(this);
        }

        body = EasyUtils.findRealRequestBody(body);

        if (body instanceof FormBody ||
                body instanceof MultipartBody) {
            // 打印表单
            for (String key : params.getKeys()) {
                Object value = params.get(key);

                if (value instanceof Map) {
                    // 如果这是一个 Map 集合
                    Map<?, ?> map = ((Map<?, ?>) value);
                    for (Object itemKey : map.keySet()) {
                        if (itemKey == null) {
                            continue;
                        }
                        printKeyValue(String.valueOf(itemKey), map.get(itemKey));
                    }
                    continue;
                }

                if (value instanceof List) {
                    // 如果这是一个 List 集合
                    List<?> list = (List<?>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object itemValue = list.get(i);
                        printKeyValue(key + "[" + i + "]", itemValue);
                    }
                    continue;
                }

                printKeyValue(key, value);
            }
        } else if (body instanceof JsonRequestBody) {
            // 打印 Json
            EasyLog.printJson(this, String.valueOf(body));
        } else if (body instanceof TextRequestBody) {
            // 打印文本
            EasyLog.printLog(this, String.valueOf(body));
        } else if (body != null) {
            EasyLog.printLog(this, String.valueOf(body));
        }

        if (!headers.isEmpty() || !params.isEmpty()) {
            EasyLog.printLine(this);
        }
    }

    /**
     * 组装 RequestBody 对象
     */
    private RequestBody createRequestBody(HttpParams params, @Nullable String contentType,
                                            IRequestBodyStrategy requestBodyStrategy) {
        RequestBody requestBody = requestBodyStrategy.createRequestBody(this, params);

        // 如果外层需要自定义 Content-Type 这个字段，那么就使用装饰设计模式，对原有的 RequestBody 对象进行扩展
        if (contentType != null && !"".equals(contentType)) {
            MediaType mediaType = MediaType.parse(contentType);
            if (mediaType != null) {
                CustomTypeBody customTypeBody = new CustomTypeBody(requestBody);
                customTypeBody.setContentType(mediaType);
                requestBody = customTypeBody;
            }
        }

        // 如果当前设置了上传监听，那么就使用装饰设计模式，对原有的 RequestBody 对象进行扩展
        if (mUpdateListener != null) {
            requestBody = new ProgressMonitorRequestBody(this, requestBody, getLifecycleOwner(), mUpdateListener);
        }

        return requestBody;
    }
}