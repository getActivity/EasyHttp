package com.hjq.http.model;

import com.hjq.http.EasyConfig;

import java.util.HashMap;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 请求参数封装
 */
public final class HttpParams {

    private HashMap<String, String> mParams = EasyConfig.getInstance().getParams();

    public void put(String key, String value) {
        if (key != null && value != null) {
            if (mParams == EasyConfig.getInstance().getParams()) {
                mParams = new HashMap<>(mParams);
            }
            mParams.put(key, value);
        }
    }

    public String get(String key) {
        return mParams.get(key);
    }

    public boolean isEmpty() {
        return mParams == null || mParams.isEmpty();
    }

    public Set<String> getNames() {
        return mParams.keySet();
    }
}