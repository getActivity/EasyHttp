package com.hjq.http.model;

import com.hjq.http.config.impl.HttpPostFormBodyStrategy;
import com.hjq.http.config.impl.HttpPostJsonBodyStrategy;
import com.hjq.http.config.IHttpPostBodyStrategy;

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
    public static final IHttpPostBodyStrategy FORM = new HttpPostFormBodyStrategy();

    /**
     * JSON 提交
     */
    public static final IHttpPostBodyStrategy JSON = new HttpPostJsonBodyStrategy();
}