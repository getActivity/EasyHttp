package com.hjq.http;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/05/10
 *    desc   : 日志打印类
 */
public final class EasyLog {

    /**
     * 打印分割线
     */
    public static void print() {
        print("----------------------------------------");
    }

    /**
     * 打印日志
     */
    public static void print(String log) {
        if (EasyConfig.getInstance().isLogEnabled()) {
            EasyConfig.getInstance().getLogStrategy().print(log);
        }
    }

    /**
     * 打印 Json
     */
    public static void json(String json) {
        if (EasyConfig.getInstance().isLogEnabled()) {
            EasyConfig.getInstance().getLogStrategy().json(json);
        }
    }

    /**
     * 打印键值对
     */
    public static void print(String key, String value) {
        if (EasyConfig.getInstance().isLogEnabled()) {
            EasyConfig.getInstance().getLogStrategy().print(key, value);
        }
    }

    /**
     * 打印异常
     */
    public static void print(Throwable throwable) {
        if (EasyConfig.getInstance().isLogEnabled()) {
            EasyConfig.getInstance().getLogStrategy().print(throwable);
        }
    }

    /**
     * 打印堆栈
     */
    public static void print(StackTraceElement[] stackTrace) {
        if (EasyConfig.getInstance().isLogEnabled()) {
            EasyConfig.getInstance().getLogStrategy().print(stackTrace);
        }
    }
}