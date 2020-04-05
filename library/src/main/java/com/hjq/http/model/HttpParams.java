package com.hjq.http.model;

import com.hjq.http.EasyConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 请求参数封装
 */
public final class HttpParams {

    private HashMap<String, Object> mParams = EasyConfig.getInstance().getParams();
    private boolean mMultipart;

    public void put(String key, Object value) {
        if (key != null && value != null) {
            if (mParams == EasyConfig.getInstance().getParams()) {
                mParams = new HashMap<>(mParams);
            }
            mParams.put(key, value);
            if (value instanceof File) {
                mMultipart = true;
            }
        }
    }

    public Object get(String key) {
        return mParams.get(key);
    }

    public boolean isEmpty() {
        return mParams == null || mParams.isEmpty();
    }

    public Set<String> getNames() {
        return mParams.keySet();
    }

    public HashMap<String, Object> getParams() {
        return mParams;
    }

    /**
     * 是否有文件上传
     */
    public boolean isMultipart() {
        return mMultipart;
    }
}