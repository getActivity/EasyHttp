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
     * 打印异常
     */
    void print(Throwable throwable);

    /**
     * 打印键值对
     */
    void print(String key, String value);

    /**
     * 将字符串格式化成JSON的格式
     */
    static String stringToJSON(String strJson) {
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

    static String getSpaceOrTab(int tabNum) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tabNum; i++) {
            sb.append('\t');
        }
        return sb.toString();
    }
}