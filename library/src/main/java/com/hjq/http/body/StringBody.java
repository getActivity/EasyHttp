package com.hjq.http.body;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/26
 *    desc   : 文本参数提交
 */
public final class StringBody extends RequestBody {

    private final String mText;

    public StringBody() {
        this("");
    }

    public StringBody(String text) {
        mText = text;
    }

    @Override
    public MediaType contentType() {
        return MediaType.get("text/plain; charset=utf-8");
    }

    @Override
    public long contentLength() {
        return mText.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] bytes = mText.getBytes();
        sink.write(bytes, 0, bytes.length);
    }

    @NonNull
    @Override
    public String toString() {
        return mText;
    }
}