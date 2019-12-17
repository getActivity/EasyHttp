package com.hjq.http.request;

import android.content.Context;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.annotation.HttpHeader;
import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.annotation.HttpRename;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestHost;
import com.hjq.http.config.IRequestPath;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.config.RequestApi;
import com.hjq.http.config.RequestServer;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import java.lang.reflect.Field;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 请求基类
 */
@SuppressWarnings("unchecked")
public abstract class BaseRequest<T extends BaseRequest> {

    /** Http 客户端 */
    private OkHttpClient mClient = EasyConfig.getInstance().getClient();

    /** 请求主机配置 */
    private IRequestHost mRequestHost = EasyConfig.getInstance().getServer();
    /** 请求路径配置 */
    private IRequestPath mRequestPath = EasyConfig.getInstance().getServer();
    /** 请求接口配置 */
    private IRequestApi mRequestApi;

    /** 请求的上下文 */
    private Context mContext;
    /** 请求标记 */
    private String mTag;

    public BaseRequest(Context context) {
        mContext = context;
    }

    public T api(Class<? extends IRequestApi> api) {
        try {
            return api(api.newInstance());
        } catch (InstantiationException e) {
            EasyLog.print(e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            EasyLog.print(e);
            throw new RuntimeException(e);
        }
    }

    public T api(String api) {
        return api(new RequestApi(api));
    }

    /**
     * 设置请求配置
     */
    public T api(IRequestApi config) {
        mRequestApi = config;
        // 判断这个接口是否实现了服务器配置
        if (config instanceof IRequestServer) {
            mRequestHost = (IRequestHost) config;
            mRequestPath = (IRequestPath) config;
        } else {
            if (config instanceof IRequestHost) {
                mRequestHost = (IRequestHost) config;
            }
            if (config instanceof IRequestPath) {
                mRequestPath = (IRequestPath) config;
            }
        }
        return (T) this;
    }

    public T server(Class<? extends IRequestServer> api) {
        try {
            return server(api.newInstance());
        } catch (InstantiationException e) {
            EasyLog.print(e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            EasyLog.print(e);
            throw new RuntimeException(e);
        }
    }

    public T server(String host) {
        return server(new RequestServer(host));
    }

    /**
     * 替换服务器配器（推荐使用 api 的方式来替代 server，具体实现可见 api 方法源码）
     */
    public T server(IRequestServer server) {
        mRequestHost = server;
        mRequestPath = server;
        return (T) this;
    }

    /**
     * 设置标记
     */
    public T tag(Object tag) {
        if (tag != null) {
            return tag(tag.toString());
        }
        return (T) this;
    }

    public T tag(String tag) {
        mTag = tag;
        return (T) this;
    }

    /**
     * 替换 OkHttpClient
     */
    public T client(OkHttpClient client) {
        mClient = client;
        return (T) this;
    }

    public Call create() {

        final HttpParams params = new HttpParams();
        final HttpHeaders headers = new HttpHeaders();

        Field[] fields = mRequestApi.getClass().getDeclaredFields();
        for (Field field : fields) {

            // 允许访问私有字段
            field.setAccessible(true);

            // 如果这个字段需要忽略，则进行忽略
            if (field.isAnnotationPresent(HttpIgnore.class)) {
                continue;
            }

            try {

                // 获取字段的对象
                Object object = field.get(mRequestApi);

                // 前提是这个字段对象不能为空（基本数据类型有默认的值，而对象默认的值为 null）
                if (object == null) {
                    break;
                }

                // 获取字段的名称
                String key;
                if (field.isAnnotationPresent(HttpRename.class)) {
                    key = field.getAnnotation(HttpRename.class).value();
                } else {
                    key = field.getName();
                }

                if (field.isAnnotationPresent(HttpHeader.class)) {
                    // 如果这个是一个请求头参数
                    if (object instanceof Map) {
                        Map map = ((Map) object);
                        for (Object o : map.keySet()) {
                            if (o != null && map.get(o) != null) {
                                headers.put(o.toString(), map.get(o).toString());
                            }
                        }
                    } else {
                        headers.put(key, object.toString());
                    }
                } else {
                    // 如果这个是一个普通的参数
                    if (object instanceof Map) {
                        Map map = ((Map) object);
                        for (Object o : map.keySet()) {
                            if (o != null && map.get(o) != null) {
                                params.put(o.toString(), map.get(o));
                            }
                        }
                    } else {
                        params.put(key, object);
                    }
                }

            } catch (IllegalAccessException e) {
                EasyLog.print(e);
            }
        }

        if (EasyLog.isEnable()) {
            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }

            for (String key : headers.getNames()) {
                EasyLog.print(key, headers.get(key));
            }

            if (!headers.isEmpty() && !params.isEmpty()) {
                EasyLog.print();
            }

            for (String key : params.getNames()) {
                EasyLog.print(key, params.get(key).toString());
            }

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.print();
            }
        }

        String url = mRequestHost.getHost() + mRequestPath.getPath() + mRequestApi.getApi();
        EasyLog.print("RequestUrl：" + url);
        return mClient.newCall(create(url, mTag, params, headers));
    }

    protected Context getContext() {
        return mContext;
    }

    protected abstract Request create(String url, String tag, HttpParams params, HttpHeaders headers);
}