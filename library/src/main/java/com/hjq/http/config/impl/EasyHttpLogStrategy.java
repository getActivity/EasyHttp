package com.hjq.http.config.impl;

import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.http.EasyUtils;
import com.hjq.http.config.IRequestLogStrategy;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/04/24
 *    desc   : 网络请求日志打印默认实现
 */
public final class EasyHttpLogStrategy implements IRequestLogStrategy {

    @Override
    public void printLog(@NonNull String tag, @Nullable String message) {
        // 这里解释一下，为什么不用 Log.d，而用 Log.i，因为 Log.d 在魅族 16th 手机上面无法输出日志
        Log.i(tag, message != null ? message : "null");
    }

    @Override
    public void printJson(@NonNull String tag, @Nullable String json) {
        String text = EasyUtils.formatJson(json);
        if (TextUtils.isEmpty(text)) {
            return;
        }

        // 打印 Json 数据最好换一行再打印会好看一点
        text = " \n" + text;

        // 测试了一些设备，日志限制的长度大概在 3700 ~ 3800
        // 为了保险起见，这里最大长度设置成 3600
        int segmentSize = 3600;
        long length = text.length();
        if (length <= segmentSize) {
            // 长度小于等于限制直接打印
            printLog(tag, text);
            return;
        }

        // 循环分段打印日志
        while (text.length() > segmentSize) {
            String logContent = text.substring(0, segmentSize);
            text = text.replace(logContent, "");
            printLog(tag, logContent);
        }

        // 打印剩余日志
        printLog(tag, text);
    }

    @Override
    public void printKeyValue(@NonNull String tag, @Nullable String key, @Nullable String value) {
        printLog(tag, key + " = " + value);
    }

    @Override
    public void printThrowable(@NonNull String tag, @Nullable Throwable throwable) {
        if (throwable == null) {
            Log.e(tag, "An empty throwable object appears");
            return;
        }
        Log.e(tag, throwable.getMessage(), throwable);
    }

    @Override
    public void printStackTrace(@NonNull String tag, @Nullable StackTraceElement[] stackTrace) {
        if (stackTrace == null) {
            return;
        }
        for (StackTraceElement element : stackTrace) {
            // 获取代码行数
            int lineNumber = element.getLineNumber();
            // 获取类的全路径
            String className = element.getClassName();
            if (lineNumber <= 0 || className.startsWith("com.hjq.http")) {
                continue;
            }

            printLog(tag, "RequestCode = (" + element.getFileName() + ":" + lineNumber + ") ");
            break;
        }
    }
}