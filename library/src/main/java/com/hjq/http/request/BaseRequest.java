package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.annotation.HttpHeader;
import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.annotation.HttpRename;
import com.hjq.http.callback.NormalCallback;
import com.hjq.http.config.IRequestApi;
import com.hjq.http.config.IRequestHost;
import com.hjq.http.config.IRequestInterceptor;
import com.hjq.http.config.IRequestPath;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.config.IRequestType;
import com.hjq.http.config.RequestApi;
import com.hjq.http.config.RequestServer;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.DataClass;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 请求基类
 */
@SuppressWarnings("unchecked")
public abstract class BaseRequest<T extends BaseRequest> {

    /** OkHttp 客户端 */
    private OkHttpClient mClient = EasyConfig.getInstance().getClient();

    /** 接口主机地址 */
    private IRequestHost mRequestHost = EasyConfig.getInstance().getServer();
    /** 接口路径地址 */
    private IRequestPath mRequestPath = EasyConfig.getInstance().getServer();
    /** 提交参数类型 */
    private IRequestType mRequestType = EasyConfig.getInstance().getServer();

    /** 请求接口配置 */
    private IRequestApi mRequestApi;

    /** 请求生命周期控制 */
    private LifecycleOwner mLifecycleOwner;

    /** 请求执行代理类 */
    private CallProxy mCallProxy;

    /** 请求标记 */
    private String mTag;

    public BaseRequest(LifecycleOwner lifecycle) {
        if (lifecycle == null) {
            throw new IllegalArgumentException("are you ok?");
        }
        mLifecycleOwner = lifecycle;
    }

    public T api(Class<? extends IRequestApi> api) {
        try {
            return api(api.newInstance());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public T api(String api) {
        return api(new RequestApi(api));
    }

    /**
     * 设置请求配置
     */
    public T api(IRequestApi api) {
        mRequestApi = api;
        if (api instanceof IRequestHost) {
            mRequestHost = (IRequestHost) api;
        }
        if (api instanceof IRequestPath) {
            mRequestPath = (IRequestPath) api;
        }
        if (api instanceof IRequestType) {
            mRequestType = (IRequestType) api;
        }
        return (T) this;
    }

    public T server(Class<? extends IRequestServer> api) {
        try {
            return server(api.newInstance());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
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
        mRequestType = server;
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

        BodyType type = mRequestType.getType();

        HttpParams params = new HttpParams();
        HttpHeaders headers = new HttpHeaders();

        Field[] fields = mRequestApi.getClass().getDeclaredFields();

        params.setMultipart(EasyUtils.isMultipart(fields));
        // 如果参数中包含流参数并且当前请求方式不是表单的话
        if (params.isMultipart() && type != BodyType.FORM) {
            // 就强制设置成以表单形式提交参数
            type = BodyType.FORM;
        }

        for (Field field : fields) {
            // 允许访问私有字段
            field.setAccessible(true);
            // 规避非静态内部类持有的外部类对象
            if (mRequestApi.getClass().toString().startsWith(field.getType().toString())) {
                continue;
            }

            try {
                // 获取字段的对象
                Object value = field.get(mRequestApi);

                // 获取字段的名称
                String key;
                if (field.isAnnotationPresent(HttpRename.class)) {
                    key = field.getAnnotation(HttpRename.class).value();
                } else {
                    key = field.getName();
                }

                // 如果这个字段需要忽略，则进行忽略
                if (field.isAnnotationPresent(HttpIgnore.class)) {
                    if (field.isAnnotationPresent(HttpHeader.class)) {
                        headers.remove(key);
                    } else {
                        params.remove(key);
                    }
                    // 遍历下一个字段
                    continue;
                }

                // 前提是这个字段值不能为空（基本数据类型有默认的值，而对象默认的值为 null）
                if (EasyUtils.isEmpty(value)) {
                    // 遍历下一个字段
                    continue;
                }

                // 如果这是一个请求头参数
                if (field.isAnnotationPresent(HttpHeader.class)) {
                    if (value instanceof Map) {
                        Map map = ((Map) value);
                        for (Object o : map.keySet()) {
                            if (o != null && map.get(o) != null) {
                                headers.put(o.toString(), map.get(o).toString());
                            }
                        }
                    } else {
                        headers.put(key, value.toString());
                    }
                } else {
                    switch (type) {
                        case FORM:
                            if (value instanceof Map) {
                                Map map = ((Map) value);
                                for (Object o : map.keySet()) {
                                    if (o != null && map.get(o) != null) {
                                        params.put(o.toString(), map.get(o));
                                    }
                                }
                            } else {
                                params.put(key, value);
                            }
                            break;
                        case JSON:
                            if (value instanceof List) {
                                // 如果这是一个 List 参数
                                params.put(key, EasyUtils.listToJsonArray(((List) value)));
                            } else if (value instanceof Map) {
                                // 如果这是一个 Map 参数
                                params.put(key, EasyUtils.mapToJsonObject(((Map) value)));
                            } else if (EasyUtils.isBeanType(value)) {
                                // 如果这是一个 Bean 参数
                                params.put(key, EasyUtils.mapToJsonObject(EasyUtils.beanToHashMap(value)));
                            } else {
                                // 如果这是一个普通的参数
                                params.put(key, value);
                            }
                            break;
                        default:
                            break;
                    }
                }

            } catch (IllegalAccessException e) {
                EasyLog.print(e);
            }
        }

        String url = mRequestHost.getHost() + mRequestPath.getPath() + mRequestApi.getApi();
        IRequestInterceptor interceptor = EasyConfig.getInstance().getInterceptor();
        if (interceptor != null) {
            interceptor.intercept(url, mTag, params, headers);
        }
        return mClient.newCall(create(url, mTag, params, headers, type));
    }

    /**
     * 执行异步请求
     */
    public T request(OnHttpListener listener) {
        mCallProxy = new CallProxy(create());
        mCallProxy.enqueue(new NormalCallback(getLifecycle(), mCallProxy, listener));
        return (T) this;
    }

    /**
     * 执行同步请求
     * @param t                 需要解析泛型的对象
     * @return                  返回解析完成的对象
     * @throws Exception        如果请求失败或者解析失败则抛出异常
     */
    public <T> T execute(DataClass<T> t) throws Exception {
        try {
            mCallProxy = new CallProxy(create());
            Response response = mCallProxy.execute();
            return (T) EasyConfig.getInstance().getHandler().requestSucceed(getLifecycle(), response, EasyUtils.getReflectType(t));
        } catch (Exception e) {
            throw EasyConfig.getInstance().getHandler().requestFail(getLifecycle(), e);
        }
    }

    /**
     * 取消请求
     */
    public T cancel() {
        if (mCallProxy != null) {
            mCallProxy.cancel();
        }
        return (T) this;
    }

    protected LifecycleOwner getLifecycle() {
        return mLifecycleOwner;
    }

    protected abstract String getMethod();

    protected abstract Request create(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type);
}