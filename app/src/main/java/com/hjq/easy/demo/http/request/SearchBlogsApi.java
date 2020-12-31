package com.hjq.easy.demo.http.request;

import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/06/07
 *    desc   : 搜索文章
 */
public final class SearchBlogsApi implements IRequestApi {

    @Override
    public String getApi() {
        return "article/query/0/json";
    }

    /** 搜索关键字 */
    @HttpRename("k")
    private String keyword;

    public SearchBlogsApi setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }
}