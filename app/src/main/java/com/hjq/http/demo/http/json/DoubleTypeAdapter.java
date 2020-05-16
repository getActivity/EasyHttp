package com.hjq.http.demo.http.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/05/05
 *    desc   : double / Double 解析适配器 {@link com.google.gson.internal.bind.TypeAdapters#DOUBLE}
 */
public class DoubleTypeAdapter extends TypeAdapter<Number> {

    @Override
    public Number read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                // 如果后台返回数值，则按照正常逻辑解析
                return in.nextDouble();
            case STRING:
                try {
                    return Double.parseDouble(in.nextString());
                } catch (NumberFormatException e) {
                    // 如果是空字符串则会抛出这个异常
                    return 0;
                }
            default:
                in.skipValue();
                return 0;
        }
    }
    @Override
    public void write(JsonWriter out, Number value) throws IOException {
        out.value(value);
    }
}