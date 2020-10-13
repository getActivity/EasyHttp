package com.hjq.http.body;

import org.json.JSONArray;

import java.io.IOException;
import java.util.List;

import okio.BufferedSink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/28
 *    desc   : JsonArray 参数提交
 */
public final class JsonArrayBody extends JsonBaseBody {

    private final JSONArray mJsonArray;

    public JsonArrayBody() {
        this(new JSONArray());
    }

    public JsonArrayBody(List list) {
        this(new JSONArray(list));
    }

    public JsonArrayBody(JSONArray jsonArray) {
        mJsonArray = jsonArray;
    }

    public JSONArray getJsonObject() {
        return mJsonArray;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        writeTo(sink, mJsonArray.toString());
    }
}