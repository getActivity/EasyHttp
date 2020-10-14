package com.hjq.http.demo.http.json;

import com.google.gson.stream.JsonReader;

import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/05/05
 *    desc   : int / Integer 类型解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapters#INTEGER}
 */
public class IntegerTypeAdapter extends DoubleTypeAdapter {

    @Override
    public Number read(JsonReader in) throws IOException {
        Number number = super.read(in);
        if (number != null) {
            return number.intValue();
        }
        return null;
    }
}