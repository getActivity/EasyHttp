package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.callback.NormalCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;
import com.hjq.http.model.JsonBody;
import com.hjq.http.model.UpdateBody;

import java.io.File;
import java.io.InputStream;
import java.util.List;

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

    public PostRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @Override
    protected Request create(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder request = new Request.Builder();
        request.url(url);

        EasyLog.print("PostUrl", url);

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
                    if (object instanceof File) {
                        // 如果这是一个文件
                        MultipartBody.Part part = UpdateBody.createPart(key, (File) object);
                        if (part != null) {
                            builder.addPart(part);
                        }
                    } else if (object instanceof InputStream) {
                        // 如果这是一个输入流
                        MultipartBody.Part part = UpdateBody.createPart(key, (InputStream) object);
                        if (part != null) {
                            builder.addPart(part);
                        }
                    } else if (object instanceof RequestBody) {
                        // 如果这是一个自定义 RequestBody
                        builder.addFormDataPart(key, null, (RequestBody) object);
                    } else {
                        if (object instanceof List && EasyUtils.isFileList((List) object)) {
                            // 上传文件列表
                            for (Object item : (List) object) {
                                MultipartBody.Part part = UpdateBody.createPart(key, (File) item);
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

            if (body instanceof JsonBody) {
                EasyLog.json(((JsonBody) body).getJsonObject().toString());
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
     * 执行请求
     */
    public PostRequest request(OnHttpListener listener) {
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