package com.hjq.http.exception;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/06/25
 *    desc   : 返回结果异常
 */
public final class ResultException extends HttpException {

    private final Object mData;

    public ResultException(String message, Object data) {
        super(message);
        mData = data;
    }

    public ResultException(String message, Throwable cause, Object data) {
        super(message, cause);
        mData = data;
    }

    public Object getData() {
        return mData;
    }
}