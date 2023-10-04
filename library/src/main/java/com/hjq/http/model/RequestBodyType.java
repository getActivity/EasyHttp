package com.hjq.http.model;

import com.hjq.http.config.impl.RequestFormBodyStrategy;
import com.hjq.http.config.impl.RequestJsonBodyStrategy;
import com.hjq.http.config.IRequestBodyStrategy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/18
 *    desc   : 参数提交方式
 */
public class RequestBodyType {

    /**
     * 表单提交
     */
    public static final IRequestBodyStrategy FORM = new RequestFormBodyStrategy();

    /**
     * JSON 提交
     */
    public static final IRequestBodyStrategy JSON = new RequestJsonBodyStrategy();
}