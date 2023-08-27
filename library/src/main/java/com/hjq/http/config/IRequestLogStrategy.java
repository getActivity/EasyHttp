package com.hjq.http.config;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/04/24
 *    desc   : 日志打印策略
 */
public interface IRequestLogStrategy {

    /**
     * 打印分割线
     */
    default void printLine(@NonNull String tag) {
        printLog(tag, "----------------------------------------");
    }

    /**
     * 打印日志
     */
    void printLog(@NonNull String tag, @Nullable String message);

    /**
     * 打印 Json
     */
    void printJson(@NonNull String tag, @Nullable String json);

    /**
     * 打印键值对
     */
    void printKeyValue(@NonNull String tag, @Nullable String key, @Nullable String value);

    /**
     * 打印异常
     */
    void printThrowable(@NonNull String tag, @Nullable Throwable throwable);

    /**
     * 打印堆栈
     */
    void printStackTrace(@NonNull String tag, @Nullable StackTraceElement[] stackTrace);
}