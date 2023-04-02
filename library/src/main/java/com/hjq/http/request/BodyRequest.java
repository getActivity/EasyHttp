package com.hjq.http.request;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.body.CustomTypeBody;
import com.hjq.http.body.JsonBody;
import com.hjq.http.body.ProgressBody;
import com.hjq.http.body.TextBody;
import com.hjq.http.body.UpdateBody;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.FileContentResolver;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Okio;

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
        return body(new TextBody(text));
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
            mRequestBody = new ProgressBody(this, mRequestBody, getLifecycleOwner(), mUpdateListener);
        }
        super.request(listener);
    }

    @Override
    protected void addHttpParams(HttpParams params, String key, Object value, BodyType bodyType) {
        switch (bodyType) {
            case JSON:
                // Json 提交
                params.put(key, EasyUtils.convertObject(value));
                break;
            case FORM:
            default:
                // 表单提交
                params.put(key, value);
                break;
        }
    }

    @Override
    protected void addRequestParams(Request.Builder requestBuilder, HttpParams params, @Nullable String contentType, BodyType type) {
        RequestBody body = mRequestBody != null ? mRequestBody : createRequestBody(params, contentType, type);
        requestBuilder.method(getRequestMethod(), body);
    }

    @Override
    protected void printRequestLog(Request request, HttpParams params, HttpHeaders headers, BodyType type) {
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
        } else if (body instanceof JsonBody) {
            // 打印 Json
            EasyLog.printJson(this, String.valueOf(body));
        } else if (body instanceof TextBody) {
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
    private RequestBody createRequestBody(HttpParams params, @Nullable String contentType, BodyType type) {
        RequestBody requestBody;

        if (params.isMultipart() && !params.isEmpty()) {
            requestBody = createMultipartBody(params);
        } else if (type == BodyType.JSON) {
            requestBody = createJsonBody(params);
        } else {
            requestBody = createFormBody(params);
        }

        // 如果外层需要自定义 Content-Type 这个字段，那么就使用装饰设计模式，对原有的 RequestBody 对象进行扩展
        if (contentType != null && !"".equals(contentType)) {
            MediaType mediaType = MediaType.parse(contentType);
            if (mediaType != null) {
                CustomTypeBody customTypeBody = new CustomTypeBody(requestBody);
                customTypeBody.setContentType(mediaType);
                requestBody = customTypeBody;
            }
        }

        // 如果当前设置了上传监听，那么久使用装饰设计模式，对原有的 RequestBody 对象进行扩展
        if (mUpdateListener != null) {
            requestBody = new ProgressBody(this, requestBody, getLifecycleOwner(), mUpdateListener);
        }

        return requestBody;
    }

    private RequestBody createMultipartBody(HttpParams params) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        for (String key : params.getKeys()) {
            Object value = params.get(key);

            if (value instanceof Map) {
                // 如果这是一个 Map 集合
                Map<?, ?> map = ((Map<?, ?>) value);
                for (Object itemKey : map.keySet()) {
                    if (itemKey == null) {
                        continue;
                    }
                    Object itemValue = map.get(itemKey);
                    if (itemValue == null) {
                        continue;
                    }
                    addFormData(bodyBuilder, String.valueOf(itemKey), itemValue);
                }
                continue;
            }

            if (value instanceof List) {
                // 如果这是一个 List 集合
                List<?> list = (List<?>) value;
                for (Object itemValue : list) {
                    if (itemValue == null) {
                        continue;
                    }
                    addFormData(bodyBuilder, key, itemValue);
                }
                continue;
            }

            addFormData(bodyBuilder, key, value);
        }

        try {
            return bodyBuilder.build();
        } catch (IllegalStateException ignored) {
            // 如果参数为空则会抛出异常：Multipart body must have at least one part.
            return new FormBody.Builder().build();
        }
    }

    private RequestBody createJsonBody(HttpParams params) {
        return new JsonBody(params.getParams());
    }

    private RequestBody createFormBody(HttpParams params) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (params.isEmpty()) {
            return bodyBuilder.build();
        }

        for (String key : params.getKeys()) {
            Object value = params.get(key);

            if (!(value instanceof List)) {
                bodyBuilder.add(key, String.valueOf(value));
                continue;
            }

            List<?> list = (List<?>) value;
            for (Object itemValue : list) {
                if (itemValue == null) {
                    continue;
                }
                bodyBuilder.add(key, String.valueOf(itemValue));
            }
        }
        return bodyBuilder.build();
    }

    /**
     * 添加参数
     */
    private void addFormData(MultipartBody.Builder bodyBuilder, String key, Object object) {
        if (object instanceof File) {
            // 如果这是一个 File 对象
            File file = (File) object;
            String fileName = null;
            if (file instanceof FileContentResolver) {
                fileName = ((FileContentResolver) file).getFileName();
            }
            if (TextUtils.isEmpty(fileName)) {
                fileName = file.getName();
            }

            try {
                MultipartBody.Part part;
                if (file instanceof FileContentResolver) {
                    FileContentResolver fileContentResolver = (FileContentResolver) file;
                    InputStream inputStream = fileContentResolver.openInputStream();
                    part = MultipartBody.Part.createFormData(key, fileName, new UpdateBody(
                            Okio.source(inputStream), fileContentResolver.getContentType(),
                            fileName, inputStream.available()));
                } else {
                    part = MultipartBody.Part.createFormData(key, fileName, new UpdateBody(file));
                }
                bodyBuilder.addPart(part);
            } catch (FileNotFoundException e) {
                // 文件不存在，将被忽略上传
                EasyLog.printLog(this, "File does not exist, will be ignored upload: " +
                        key + " = " + file.getPath());
            } catch (IOException e) {
                EasyLog.printThrowable(this, e);
                // 文件流读取失败，将被忽略上传
                EasyLog.printLog(this, "File stream reading failed and will be ignored upload: " +
                        key + " = " + file.getPath());
            }
            return;
        }

        if (object instanceof InputStream) {
            // 如果这是一个 InputStream 对象
            InputStream inputStream = (InputStream) object;
            try {
                bodyBuilder.addPart(MultipartBody.Part.createFormData(key, null, new UpdateBody(inputStream, key)));
            } catch (IOException e) {
                EasyLog.printThrowable(this, e);
            }
            return;
        }

        if (object instanceof RequestBody) {
            // 如果这是一个自定义的 RequestBody 对象
            RequestBody requestBody = (RequestBody) object;
            if (requestBody instanceof UpdateBody) {
                bodyBuilder.addPart(MultipartBody.Part.createFormData(key,
                        ((UpdateBody) requestBody).getKeyName(), requestBody));
            } else {
                bodyBuilder.addPart(MultipartBody.Part.createFormData(key, null, requestBody));
            }
            return;
        }

        if (object instanceof MultipartBody.Part) {
            // 如果这是一个自定义的 MultipartBody.Part 对象
            bodyBuilder.addPart((MultipartBody.Part) object);
            return;
        }

        // 如果这是一个普通参数
        bodyBuilder.addFormDataPart(key, String.valueOf(object));
    }
}