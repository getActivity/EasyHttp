package com.hjq.http.config;

import com.hjq.http.model.BodyType;
import com.hjq.http.model.CacheMode;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求服务配置
 */
public interface IRequestServer extends
        IRequestHost, IRequestPath, IRequestClient,
        IRequestType, IRequestCache {

    @Override
    default BodyType getType() {
        // 默认以表单的方式提交
        return BodyType.FORM;
    }

    @Override
    default CacheMode getMode() {
        // 默认的缓存方式
        return CacheMode.DEFAULT;
    }

    @Override
    default String getPath() {
        // 服务器路径可填可不填
        return "";
    }
}