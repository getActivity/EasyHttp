package com.hjq.http.model;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/28
 *    desc   : Json 提交模型
 */
public final class JsonBody extends RequestBody {

    private static final MediaType CONTENT_TYPE = MediaType.get("application/json; charset=utf-8");

    private final JSONObject mJsonObject;

    public JsonBody() {
        this(new JSONObject());
    }

    public JsonBody(Map map) {
        this(new JSONObject(map));
    }

    public JsonBody(JSONObject jsonObject) {
        mJsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] bytes = mJsonObject.toString().getBytes();
        sink.write(bytes, 0, bytes.length);
    }
}