package com.hjq.http.config.impl;

import androidx.annotation.NonNull;
import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestServer;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2022/03/03
 *    desc   : 请求 url 简单配置类
 */
public final class SimpleRequestUrl implements IRequestServer, IRequestApi {

   /** 主机地址 */
   @HttpIgnore
   private final String mHost;

   /** 接口地址 */
   @HttpIgnore
   private final String mApi;

   public SimpleRequestUrl(String url) {
       URI uri = null;
       try {
           uri = new URI(url);
       } catch (URISyntaxException e) {
           e.printStackTrace();
       }

       if (uri == null) {
           // 如果解析失败的情况下，就直接把 url 充当 host，api 则什么都不设置
           mHost = url;
           mApi = "";
           return;
       }

       StringBuilder hostBuilder = new StringBuilder();
       if (uri.getScheme() != null) {
           hostBuilder.append(uri.getScheme()).append("://");
       }
       if (uri.getPort() != -1) {
           hostBuilder.append(uri.getHost()).append(":").append(uri.getPort());
       } else {
           hostBuilder.append(uri.getHost());
       }

       mHost = hostBuilder.toString();
       mApi = url.replace(mHost, "");
   }

   public SimpleRequestUrl(String host, String api) {
      mHost = host;
      mApi = api;
   }

   @NonNull
   @Override
   public String getHost() {
      return mHost;
   }

   @NonNull
   @Override
   public String getApi() {
      return mApi;
   }

   @NonNull
   @Override
   public String toString() {
      return mHost + mApi;
   }
}