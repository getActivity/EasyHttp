package com.hjq.http.config;

import android.text.TextUtils;
import android.util.Log;

import com.hjq.http.EasyConfig;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/04/24
 *    desc   : 网络请求日志打印默认实现
 */
public final class LogStrategy implements ILogStrategy {

    @Override
    public void print(String log) {
        Log.d(EasyConfig.getInstance().getLogTag(), log != null ? log : "null");
    }

    @Override
    public void json(String json) {
        String text = ILogStrategy.stringToJSON(json);
        if (!TextUtils.isEmpty(text)) {
            // 打印 Json 数据最好换一行再打印会好看一点
            text = " \n" + text;

            int segmentSize = 3 * 1024;
            long length = text.length();
            if (length <= segmentSize) {
                // 长度小于等于限制直接打印
                print(text);
            } else {
                // 循环分段打印日志
                while (text.length() > segmentSize) {
                    String logContent = text.substring(0, segmentSize);
                    text = text.replace(logContent, "");
                    print(logContent);
                }

                // 打印剩余日志
                print(text);
            }
        }
    }

    @Override
    public void print(Throwable throwable) {
        Log.e(EasyConfig.getInstance().getLogTag(), throwable.getMessage(), throwable);
    }

    @Override
    public void print(String key, String value) {
        print(key + " = " + value);
    }
}