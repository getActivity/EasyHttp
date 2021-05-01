package com.hjq.http.model;

import okhttp3.MediaType;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2021/03/31
 *    desc   : RequestBody 包装类（用于获取上传进度）
 */
public final class ContentType {

    /** 字节流 */
    public static final MediaType STREAM = MediaType.parse("application/octet-stream");

    /** Json */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /** 纯文本 */
    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
}