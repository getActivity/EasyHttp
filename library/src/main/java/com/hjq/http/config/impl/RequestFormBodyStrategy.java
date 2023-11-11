package com.hjq.http.config.impl;

import android.text.TextUtils;
import com.hjq.http.EasyLog;
import com.hjq.http.body.UpdateStreamRequestBody;
import com.hjq.http.config.IRequestBodyStrategy;
import com.hjq.http.model.FileContentResolver;
import com.hjq.http.model.HttpParams;
import com.hjq.http.request.HttpRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Okio;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2023/09/23
 *    desc   : RequestBody 表单策略实现接口
 */
public class RequestFormBodyStrategy implements IRequestBodyStrategy {

    @Override
    public void addParams(HttpParams params, String key, Object value) {
        // 表单提交
        params.put(key, value);
    }

    @Override
    public RequestBody createRequestBody(HttpRequest<?> httpRequest, HttpParams params) {
        if (!params.isEmpty() && params.isMultipart())  {
            return createMultipartRequestBody(httpRequest, params);
        }
        return createFormRequestBody(params);
    }

    public RequestBody createFormRequestBody(HttpParams params) {
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

    public RequestBody createMultipartRequestBody(HttpRequest<?> httpRequest, HttpParams params) {
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
                    addFormData(httpRequest, bodyBuilder, String.valueOf(itemKey), itemValue);
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
                    addFormData(httpRequest, bodyBuilder, key, itemValue);
                }
                continue;
            }

            addFormData(httpRequest, bodyBuilder, key, value);
        }

        try {
            return bodyBuilder.build();
        } catch (IllegalStateException ignored) {
            // 如果参数为空则会抛出异常：Multipart body must have at least one part.
            return new FormBody.Builder().build();
        }
    }

    /**
     * 添加参数
     */
    private void addFormData(HttpRequest<?> httpRequest, MultipartBody.Builder bodyBuilder, String key, Object object) {
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
                    part = MultipartBody.Part.createFormData(key, fileName, new UpdateStreamRequestBody(
                        Okio.source(inputStream), fileContentResolver.getContentType(),
                        fileName, inputStream.available()));
                } else {
                    part = MultipartBody.Part.createFormData(key, fileName, new UpdateStreamRequestBody(file));
                }
                bodyBuilder.addPart(part);
            } catch (FileNotFoundException e) {
                // 文件不存在，将被忽略上传
                EasyLog.printLog(httpRequest, "File does not exist, will be ignored upload: " +
                    key + " = " + file.getPath());
            } catch (IOException e) {
                EasyLog.printThrowable(httpRequest, e);
                // 文件流读取失败，将被忽略上传
                EasyLog.printLog(httpRequest, "File stream reading failed and will be ignored upload: " +
                    key + " = " + file.getPath());
            }
            return;
        }

        if (object instanceof InputStream) {
            // 如果这是一个 InputStream 对象
            InputStream inputStream = (InputStream) object;
            try {
                bodyBuilder.addPart(MultipartBody.Part.createFormData(key, null, new UpdateStreamRequestBody(inputStream, key)));
            } catch (IOException e) {
                EasyLog.printThrowable(httpRequest, e);
            }
            return;
        }

        if (object instanceof RequestBody) {
            // 如果这是一个自定义的 RequestBody 对象
            RequestBody requestBody = (RequestBody) object;
            if (requestBody instanceof UpdateStreamRequestBody) {
                bodyBuilder.addPart(MultipartBody.Part.createFormData(key,
                    ((UpdateStreamRequestBody) requestBody).getKeyName(), requestBody));
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