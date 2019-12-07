package com.hjq.http.model;

import com.hjq.http.EasyConfig;

import java.util.HashMap;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 请求头封装
 */
public final class HttpHeaders {

    private HashMap<String, String> mHeaders = EasyConfig.getInstance().getHeaders();

    public void put(String key, String value) {
        if (key != null && value != null) {
            if (mHeaders == EasyConfig.getInstance().getHeaders()) {
                mHeaders = new HashMap<>(mHeaders);
            }
            mHeaders.put(key, value);
        }
    }

    public String get(String key) {
        return mHeaders.get(key);
    }

    public boolean isEmpty() {
        return mHeaders == null || mHeaders.isEmpty();
    }

    public Set<String> getNames() {
        return mHeaders.keySet();
    }
}