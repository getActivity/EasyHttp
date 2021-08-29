package com.hjq.http.exception;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求异常
 */
public class HttpException extends Exception {

    private final String mMessage;

    public HttpException(String message) {
        super(message);
        mMessage = message;
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
        mMessage = message;
    }

    /**
     * 获取错误信息
     */
    @Override
    public String getMessage() {
        return mMessage;
    }
}