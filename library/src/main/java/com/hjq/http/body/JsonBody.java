package com.hjq.http.body;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/28
 *    desc   : Json 参数提交
 */
public final class JsonBody extends RequestBody {

    /** Json 文本数据 */
    private final String mJson;

    public JsonBody(Map map) {
        this(new JSONObject(map));
    }

    public JsonBody(JSONObject jsonObject) {
       mJson = jsonObject.toString();
    }

    public JsonBody(List list) {
        this(new JSONArray(list));
    }

    public JsonBody(JSONArray jsonArray) {
        mJson = jsonArray.toString();
    }

    @Override
    public MediaType contentType() {
        return MediaType.get("application/json; charset=utf-8");
    }

    @Override
    public long contentLength() {
        return mJson.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] bytes = mJson.getBytes();
        sink.write(bytes, 0, bytes.length);
    }

    @NonNull
    @Override
    public String toString() {
        return mJson;
    }
}