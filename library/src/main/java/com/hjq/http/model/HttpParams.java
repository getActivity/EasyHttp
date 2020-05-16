package com.hjq.http.model;

import com.hjq.http.EasyConfig;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import okhttp3.RequestBody;

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
            if (value instanceof File || value instanceof InputStream || value instanceof RequestBody) {
                mMultipart = true;
            } else if (value instanceof List) {
                List list = (List) value;
                if (!list.isEmpty()) {
                    // 判断一下这个集合装载的类型是不是 File
                    boolean isFileList = true;
                    for (Object object : list) {
                        if (!(object instanceof File)) {
                            isFileList = false;
                            break;
                        }
                    }
                    // 如果是的话就设置成上传多个文件
                    if (isFileList) {
                        // 标记成有流参数
                        mMultipart = true;
                    }
                }
            }
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

    /**
     * 是否有流参数
     */
    public boolean isMultipart() {
        return mMultipart;
    }
}