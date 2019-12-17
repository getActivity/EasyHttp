package com.hjq.http.demo.http.request;

import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestHost;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/14
 *    desc   : 上传图片
 */
public class UpdateImageApi implements IRequestHost, IRequestApi {

    @Override
    public String getHost() {
        return "https://graph.baidu.com/";
    }

    @Override
    public String getApi() {
        return "upload/";
    }

    /** 本地图片 */
    private File image;

    public UpdateImageApi setImage(File image) {
        this.image = image;
        return this;
    }
}