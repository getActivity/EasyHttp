package com.hjq.http.config;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/04/24
 *    desc   : 日志打印策略
 */
public interface ILogStrategy {

    /**
     * 打印分割线
     */
    default void print() {
        print("--------------------");
    }

    /**
     * 打印日志
     */
    void print(String log);

    /**
     * 打印 Json
     */
    void json(String json);

    /**
     * 打印键值对
     */
    void print(String key, String value);

    /**
     * 打印异常
     */
    void print(Throwable throwable);

    /**
     * 打印堆栈
     */
    void print(StackTraceElement[] stackTrace);

    /**
     * 将字符串格式化成 JSON 格式
     */
    static String formatJson(String json) {
        if (json == null) {
            return "";
        }
        // 计数tab的个数
        int tabNum = 0;
        StringBuilder builder = new StringBuilder();
        int length = json.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = json.charAt(i);
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
                // 是否格式化处理
                boolean formatFlag = true;
                // 获取冒号最后所在位置
                int colonIndex = json.lastIndexOf(":", i);
                // 再获取引号最后所在位置
                int quoteIndex = json.lastIndexOf(":\"", i);
                if (colonIndex != -1) {
                    if (quoteIndex == colonIndex) {
                        // {"code":"12.0101.0122651.00,300.200000,210428,,,,,,10002,,01"}
                        if (json.charAt(i - 1) != '"') {
                            formatFlag = false;
                        }
                    }
                }

                if (formatFlag) {
                    builder.append(c).append("\n").append(getSpaceOrTab(tabNum));
                } else {
                    builder.append(c);
                }

            } else if (c == ':') {
                if (i > 0 && json.charAt(i - 1) == '"') {
                    builder.append(" ").append(c).append(" ");
                } else {
                    builder.append(c);
                }
            } else if (c == '[') {
                tabNum++;
                char next = json.charAt(i + 1);
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

    /**
     * 创建对应数量的制表符
     */
    static String getSpaceOrTab(int tabNum) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tabNum; i++) {
            builder.append('\t');
        }
        return builder.toString();
    }
}