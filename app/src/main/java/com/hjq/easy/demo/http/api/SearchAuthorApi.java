package com.hjq.easy.demo.http.api;

import com.hjq.http.config.IRequestApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/18
 *    desc   : 按照作者昵称搜索文章
 */
public final class SearchAuthorApi implements IRequestApi {

    @Override
    public String getApi() {
        return "wxarticle/chapters/json";
    }

    /** 公众号 id */
    private int id;

    public SearchAuthorApi setId(int id) {
        this.id = id;
        return this;
    }

    public final static class Bean {

        private int courseId;
        private int id;
        private String name;
        private int order;
        private int parentChapterId;
        private boolean userControlSetTop;
        private int visible;

        public int getCourseId() {
            return courseId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getOrder() {
            return order;
        }

        public int getParentChapterId() {
            return parentChapterId;
        }

        public boolean isUserControlSetTop() {
            return userControlSetTop;
        }

        public int getVisible() {
            return visible;
        }
    }
}