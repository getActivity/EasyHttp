package com.hjq.http.config;

import com.hjq.http.model.BodyType;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求服务配置
 */
public interface IRequestServer extends IRequestHost, IRequestPath, IRequestType {

    @Override
    default BodyType getType() {
        // 默认以表单的方式提交
        return BodyType.FROM;
    }

    @Override
    default String getPath() {
        // 服务器路径可填可不填
        return "";
    }
}