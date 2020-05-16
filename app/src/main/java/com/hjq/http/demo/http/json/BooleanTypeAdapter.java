package com.hjq.http.demo.http.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/05/05
 *    desc   : boolean / Boolean 解析适配器 {@link com.google.gson.internal.bind.TypeAdapters#BOOLEAN}
 */
public class BooleanTypeAdapter extends TypeAdapter<Boolean> {

    @Override
    public Boolean read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            case STRING:
                // 如果后台返回 "true" 或者 "TRUE"，则默认处理为 true
                return Boolean.parseBoolean(in.nextString());
            case NUMBER:
                // 如果这个后台返回是 1，则表示 true，否则表示 false
                return in.nextInt() == 1;
            default:
                return in.nextBoolean();
        }
    }
    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        out.value(value);
    }
}
