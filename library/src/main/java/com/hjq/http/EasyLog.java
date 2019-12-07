package com.hjq.http;

import android.util.Log;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求打印类
 */
public final class EasyLog {

    private static final String TAG = "EasyHttp";

    /**
     * 日志开关
     */
    public static boolean isEnable() {
        return EasyConfig.getInstance().isLog();
    }

    /**
     * 打印日志
     */
    public static void print(String log) {
        Log.d(TAG, log != null ? log : "null");
    }

    public static void print(String key, String value) {
        Log.d(TAG, key + " = " + value);
    }

    public static void print() {
        Log.d(TAG, "--------------------");
    }

    /**
     * 打印 Json
     */
    public static void json(String json) {
        String text = stringToJSON(json);
        if (text != null && !"".equals(text)) {
            // 打印 Json 数据最好换一行再打印会好看一点
            text = " \n" + text;

            int segmentSize = 3 * 1024;
            long length = text.length();
            if (length <= segmentSize ) {
                // 长度小于等于限制直接打印
                Log.d(TAG, text);
            }else {
                // 循环分段打印日志
                while (text.length() > segmentSize ) {
                    String logContent = text.substring(0, segmentSize );
                    text = text.replace(logContent, "");
                    Log.d(TAG, logContent);
                }
                // 打印剩余日志
                Log.d(TAG, text);
            }
        }
    }

    /**
     * 将字符串格式化成JSON的格式
     */
    private static String stringToJSON(String strJson) {
        if (strJson == null) {
            return null;
        }
        // 计数tab的个数
        int tabNum = 0;
        StringBuilder builder = new StringBuilder();
        int length = strJson.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = strJson.charAt(i);
            if (c == '{') {
                tabNum++;
                builder.append(c).append("\n")
                        .append(getSpaceOrTab(tabNum));
            } else if (c == '}') {
                tabNum--;
                builder.append("\n")
                        .append(getSpaceOrTab(tabNum))
                        .append(c);
            } else if (c == ',') {
                builder.append(c).append("\n")
                        .append(getSpaceOrTab(tabNum));
            } else if (c == ':') {
                if (i > 0 && strJson.charAt(i - 1) == '"') {
                    builder.append(c).append(" ");
                } else {
                    builder.append(c);
                }
            } else if (c == '[') {
                tabNum++;
                char next = strJson.charAt(i + 1);
                if (next == ']') {
                    builder.append(c);
                } else {
                    builder.append(c).append("\n")
                            .append(getSpaceOrTab(tabNum));
                }
            } else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    builder.append(c);
                } else {
                    builder.append("\n").append(getSpaceOrTab(tabNum)).append(c);
                }
            } else {
                builder.append(c);
            }
            last = c;
        }
        return builder.toString();
    }

    private static String getSpaceOrTab(int tabNum) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tabNum; i++) {
            sb.append('\t');
        }
        return sb.toString();
    }
}