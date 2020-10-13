package com.hjq.http.body;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/28
 *    desc   : Json 参数提交
 */
public abstract class JsonBaseBody extends RequestBody {

    private static final MediaType CONTENT_TYPE = MediaType.get("application/json; charset=utf-8");

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    public void writeTo(BufferedSink sink, String json) throws IOException {
        byte[] bytes = json.getBytes();
        sink.write(bytes, 0, bytes.length);
    }
}