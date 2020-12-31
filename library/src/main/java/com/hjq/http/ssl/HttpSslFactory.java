package com.hjq.http.ssl;

import com.hjq.http.EasyLog;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/11/30
 *    desc   : Https 证书校验工厂
 */
public final class HttpSslFactory {

    /**
     * 生成信任任何证书的配置
     */
    public static HttpSslConfig generateSslConfig() {
        return generateSslConfigBase(null, null, null);
    }

    /**
     * https 单向认证
     */
    public static HttpSslConfig generateSslConfig(X509TrustManager trustManager) {
        return generateSslConfigBase(trustManager, null, null);
    }

    /**
     * https 单向认证
     */
    public static HttpSslConfig generateSslConfig(InputStream... certificates) {
        return generateSslConfigBase(null, null, null, certificates);
    }

    /**
     * https 双向认证
     */
    public static HttpSslConfig generateSslConfig(InputStream bksFile, String password, InputStream... certificates) {
        return generateSslConfigBase(null, bksFile, password, certificates);
    }

    /**
     * https 双向认证
     */
    public static HttpSslConfig generateSslConfig(InputStream bksFile, String password, X509TrustManager trustManager) {
        return generateSslConfigBase(trustManager, bksFile, password);
    }

    /**
     * 生成认证配置
     *
     * @param trustManager          可以额外配置信任服务端的证书策略，否则默认是按CA证书去验证的，若不是CA可信任的证书，则无法通过验证
     * @param bksFile               客户端使用 bks 证书校验服务端证书
     * @param password
     * @param certificates          用含有服务端公钥的证书校验服务端证书
     */
    private static HttpSslConfig generateSslConfigBase(X509TrustManager trustManager, InputStream bksFile, String password, InputStream... certificates) {
        try {
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            X509TrustManager manager;
            if (trustManager != null) {
                // 优先使用用户自定义的TrustManager
                manager = trustManager;
            } else if (trustManagers != null) {
                // 然后使用默认的TrustManager
                manager = chooseTrustManager(trustManagers);
            } else {
                // 否则使用不安全的TrustManager
                manager = new UnSafeTrustManager();
            }
            // 创建TLS类型的SSLContext对象， that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
            // 第一个参数是授权的密钥管理器，用来授权验证，比如授权自签名的证书验证。第二个是被授权的证书管理器，用来验证服务器端的证书
            sslContext.init(keyManagers, new TrustManager[]{manager}, null);
            // 通过sslContext获取SSLSocketFactory对象
            return new HttpSslConfig(sslContext.getSocketFactory(), manager);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new AssertionError(e);
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) {
                return null;
            }
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(keyStore, password.toCharArray());
            return factory.getKeyManagers();
        } catch (IOException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            EasyLog.print(e);
            return null;
        }
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) {
            return null;
        }
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            // 创建一个默认类型的KeyStore，存储我们信任的证书
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certStream : certificates) {
                String certificateAlias = Integer.toString(index++);
                // 证书工厂根据证书文件的流生成证书 cert
                Certificate cert = certificateFactory.generateCertificate(certStream);
                // 将 cert 作为可信证书放入到keyStore中
                keyStore.setCertificateEntry(certificateAlias, cert);
                try {
                    if (certStream != null) {
                        certStream.close();
                    }
                } catch (IOException e) {
                    EasyLog.print(e);
                }
            }
            // 我们创建一个默认类型的TrustManagerFactory
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // 用我们之前的keyStore实例初始化TrustManagerFactory，这样tmf就会信任keyStore中的证书
            factory.init(keyStore);
            // 通过tmf获取TrustManager数组，TrustManager也会信任keyStore中的证书
            return factory.getTrustManagers();
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
            EasyLog.print(e);
            return null;
        }
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    public static HostnameVerifier generateUnSafeHostnameVerifier() {
        return new UnSafeHostnameVerifier();
    }
}