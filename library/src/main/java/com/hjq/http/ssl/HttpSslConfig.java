package com.hjq.http.ssl;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/11/30
 *    desc   : Https 配置类
 */
public final class HttpSslConfig {

    private final SSLSocketFactory sSLSocketFactory;
    private final X509TrustManager trustManager;

    HttpSslConfig(SSLSocketFactory sSLSocketFactory, X509TrustManager trustManager) {
        this.sSLSocketFactory = sSLSocketFactory;
        this.trustManager = trustManager;
    }

    public SSLSocketFactory getsSLSocketFactory() {
        return sSLSocketFactory;
    }

    public X509TrustManager getTrustManager() {
        return trustManager;
    }
}