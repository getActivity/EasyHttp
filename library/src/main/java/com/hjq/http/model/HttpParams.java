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

    /** 请求参数存放集合 */
    private HashMap<String, Object> mParams = EasyConfig.getInstance().getParams();

    /** 是否有流参数 */
    private boolean mMultipart;

    public void put(String key, Object value) {
        if (key != null && value != null) {
            if (mParams == EasyConfig.getInstance().getParams()) {
                mParams = new HashMap<>(mParams);
            }
            mParams.put(key, value);
        }
    }

    public void remove(String key) {
        if (key != null) {
            if (mParams == EasyConfig.getInstance().getParams()) {
                mParams = new HashMap<>(mParams);
            }
            mParams.remove(key);
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

    public boolean isMultipart() {
        return mMultipart;
    }

    public void setMultipart(boolean multipart) {
        mMultipart = multipart;
    }
}