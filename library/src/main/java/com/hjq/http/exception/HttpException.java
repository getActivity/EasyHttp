package com.hjq.http.exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求异常
 */
public class HttpException extends Exception {

    private String mMessage;
    private Throwable mThrowable;

    public HttpException(String message) {
        super(message);
        mMessage = message;
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
        mMessage = message;
        mThrowable = cause;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    /**
     * 获取错误信息
     */
    @Override
    public String getMessage() {
        return mMessage;
    }

    @NonNull
    @Override
    public StackTraceElement[] getStackTrace() {
        if (mThrowable != null) {
            return mThrowable.getStackTrace();
        }
        return super.getStackTrace();
    }

    @Nullable
    @Override
    public synchronized Throwable getCause() {
        if (mThrowable != null) {
            return mThrowable.getCause();
        }
        return super.getCause();
    }
}