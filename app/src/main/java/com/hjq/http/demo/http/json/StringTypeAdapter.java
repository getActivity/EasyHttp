package com.hjq.http.demo.http.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/05/05
 *    desc   : String 解析适配器 {@link com.google.gson.internal.bind.TypeAdapters#STRING}
 */
public class StringTypeAdapter extends TypeAdapter<String> {

    @Override
    public String read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            case BOOLEAN:
                // 对于布尔类型比较特殊，需要做针对性处理
                return Boolean.toString(in.nextBoolean());
            default:
                // 其他类型的数据直接按照字符串来处理
                return in.nextString();
        }
    }

    @Override
    public void write(JsonWriter out, String value) throws IOException {
        out.value(value);
    }
}