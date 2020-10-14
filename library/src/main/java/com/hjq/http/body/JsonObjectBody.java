package com.hjq.http.body;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okio.BufferedSink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/28
 *    desc   : JsonObject 参数提交
 */
public final class JsonObjectBody extends JsonBaseBody {

    private final JSONObject mJsonObject;

    public JsonObjectBody() {
        this(new JSONObject());
    }

    public JsonObjectBody(Map map) {
        this(new JSONObject(map));
    }

    public JsonObjectBody(JSONObject jsonObject) {
        mJsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        writeTo(sink, mJsonObject.toString());
    }
}