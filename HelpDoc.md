# 目录

> 文档大致分为三类

* [集成文档](#集成文档)

* [使用文档](#使用文档)

* [疑难解答](#疑难解答)

# 集成文档

#### 配置权限

```xml
<!-- 联网权限 -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- 访问网络状态 -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

#### 关于 Http 明文请求

> Android P 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉。
如果当前应用的请求是 http 请求，而非 https ,这样就会导系统禁止当前应用进行该请求，如果 WebView 的 url 用 http 协议，同样会出现加载失败，https 不受影响

> 在 res 下新建一个 xml 目录，然后创建一个名为：network_security_config.xml 文件 ，该文件内容如下

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

> 然后在 AndroidManifest.xml application 标签内应用上面的xml配置

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config" />
```

#### 服务器配置

```java
public class RequestServer implements IRequestServer {

    @Override
    public String getHost() {
        return "https://www.baidu.com/";
    }

    @Override
    public String getPath() {
        return "api/";
    }

    @Override
    public BodyType getType() {
        // 参数以 Json 格式提交（默认是表单）
        return BodyType.JSON;
    }
}
```

#### 框架初始化

> 需要配置请求结果处理，具体封装可以参考 [RequestHandler](app/src/main/java/com/hjq/http/demo/http/model/RequestHandler.java)

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .build();

EasyConfig.with(okHttpClient)
        // 是否打印日志
        .setLogEnabled(BuildConfig.DEBUG)
        // 设置服务器配置
        .setServer(server)
        // 设置请求处理策略
        .setHandler(new RequestHandler())
        // 设置请求重试次数
        .setRetryCount(3)
        // 添加全局请求参数
        //.addParam("token", "6666666")
        // 添加全局请求头
        //.addHeader("time", "20191030")
        // 启用配置
        .into();
```

> 上述是创建配置，更新配置可以使用

```java
EasyConfig.getInstance()
        .addParam("token", data.getData().getToken());
```

# 使用文档

#### 配置接口

```java
public final class LoginApi implements IRequestApi {

    @Override
    public String getApi() {
        return "user/login";
    }

    /** 用户名 */
    private String userName;

    /** 登录密码 */
    private String password;

    public LoginApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public LoginApi setPassword(String password) {
        this.password = password;
        return this;
    }
}
```

* 可为这个类的字段加上一些注解

	* @HttpHeader：标记这个字段是一个请求头参数

	* @HttpIgnore：标记这个字段不会被发送给后台

	* @HttpRename：重新定义这个字段发送给后台的参数名称

* 可在这个类实现一些接口

	* implements IRequestHost：实现这个接口之后可以重新指定这个请求的主机地址

	* implements IRequestPath：实现这个接口之后可以重新指定这个请求的接口路径

	* implements IRequestType：实现这个接口之后可以重新指定这个请求的提交方式

#### 发起请求

> 需要配置请求状态及生命周期处理，具体封装可以参考 [BaseActivity](app/src/main/java/com/hjq/http/demo/BaseActivity.java)

```java
EasyHttp.post(this)
        .api(new LoginApi()
                .setUserName("Android 轮子哥")
                .setPassword("123456"))
        .request(new HttpCallback<HttpData<LoginBean>>(activity) {

            @Override
            public void onSucceed(HttpData<LoginBean> data) {
                ToastUtils.show("登录成功");
            }
        });
```

#### 上传文件

```java
EasyHttp.post(this)
        .api(new UpdateImageApi(file))
        .request(new OnUpdateListener<Void>() {

            @Override
            public void onStart(Call call) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUpdate(long totalByte, long updateByte, int progress) {
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onSucceed(Void result) {
                ToastUtils.show("上传成功");
            }

            @Override
            public void onFail(Exception e) {
                ToastUtils.show("上传失败");
            }

            @Override
            public void onEnd(Call call) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
```

#### 下载文件

> 下载缓存策略：在指定下载文件 md5 或者后台有返回 md5 的情况下，下载框架默认开启下载缓存模式，如果这个文件已经存在手机中，并且经过 md5 校验文件完整，框架就不会重复下载，而是直接回调下载监听。减轻服务器压力，减少用户等待时间。

```java
EasyHttp.download(this)
        .method(HttpMethod.GET)
        .file(new File(Environment.getExternalStorageDirectory(), "微信.apk"))
        //.url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
        .url("http://dldir1.qq.com/weixin/android/weixin708android1540.apk")
        .md5("2E8BDD7686474A7BC4A51ADC3667CABF")
        .listener(new OnDownloadListener() {

            @Override
            public void onStart(File file) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgress(File file, long totalByte, long downloadByte, int progress) {
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onComplete(File file) {
                ToastUtils.show("下载完成：" + file.getPath());
                installApk(MainActivity.this, file);
            }

            @Override
            public void onError(File file, Exception e) {
                ToastUtils.show("下载出错：" + e.getMessage());
            }

            @Override
            public void onEnd(File file) {
                mProgressBar.setVisibility(View.GONE);
            }

        }).start();
```

#### 同步请求

```java
try {
    HttpData<SearchBean> data = EasyHttp.post(MainActivity.this)
            .api(new SearchBlogsApi()
                    .setKeyword("搬砖不再有"))
            .execute(new DataClass<HttpData<SearchBean>>() {});
    ToastUtils.show("请求成功，请看日志");
} catch (Exception e) {
    e.printStackTrace();
    ToastUtils.show(e.getMessage());
}
```

# 疑难解答

#### 如何添加全局参数？

```java
// 添加全局请求参数
EasyConfig.getInstance().addParam("token", "abc");
// 添加全局请求头
EasyConfig.getInstance().addHeader("token", "abc");
```

#### 如何定义全局的动态参数？

```java
EasyConfig.getInstance().setInterceptor(new IRequestInterceptor() {

    @Override
    public void intercept(String url, String tag, HttpParams params, HttpHeaders headers) {
        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
    }
});
```

#### 如何在请求中忽略某个全局参数？

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpIgnore
    private String token;
}
```

#### 如何获取服务器配置？

```java
IRequestServer server = EasyConfig.getInstance().getServer();
// 获取当前全局的服务器主机地址
String host = server.getHost();
// 获取当前全局的服务器路径地址
String path = server.getPath();
```

#### 如何修改服务器配置？

* 先定义一个服务器配置

```java
public class XxxServer implements IRequestServer {

    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @Override
    public String getPath() {
        return "api/";
    }
}
```

* 再将它应用到全局配置中

```java
EasyConfig.getInstance().setServer(new XxxServer());
```

* 如果只是针对某个接口可以这样配置

```java
public final class XxxApi extends XxxServer implements IRequestApi {

    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

* 如果不想单独定义一个类，也可以这样写

```java
public final class XxxApi implements IRequestServer, IRequestApi {

    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @Override
    public String getPath() {
        return "api/";
    }

    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

#### 如何修改参数的提交方式？

* 以表单的形式提交参数（默认）

```java
public class XxxServer implements IRequestServer {

    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @Override
    public String getPath() {
        return "api/";
    }
    
    @Override
    public BodyType getType() {
        return BodyType.FORM;
    }
}
```

* 以 Json 的形式提交参数

```java
public class XxxServer implements IRequestServer {

    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @Override
    public String getPath() {
        return "api/";
    }
    
    @Override
    public BodyType getType() {
        return BodyType.JSON;
    }
}
```

* 当然也支持对某个接口进行单独配置

```java
public final class XxxApi implements IRequestApi, IRequestType {

    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @Override
    public BodyType getType() {
        return BodyType.JSON;
    }
}
```

* 表单和 Json 方式提交的优缺点对比

|  场景  | 表单方式  | Json 方式 |
| :----: | :------: |  :-----: |
|   参数嵌套  |  不支持  |   支持  |
|   文件上传  |   支持  |  不支持  |

#### 如何忽略某个参数？

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpIgnore
    private String address;
}
```

#### 如何传入请求头？

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpHeader
    private String time;
}
```

#### 如何重命名参数名称？

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    @HttpRename("k")
    private String keyword;
}
```

#### 如何上传文件？

* 使用 File 对象上传

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private File file;
}
```

* 使用 InputStream 对象上传

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private InputStream inputStream;
}
```

* 使用 RequestBody 对象上传

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private RequestBody requestBody;
}
```

#### 如何上传文件列表？

```java
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private List<File> files;
}
```

#### 如何设置超时重试？

```java
// 设置请求重试次数
EasyConfig.getInstance().setRetryCount(3);
// 设置请求重试时间
EasyConfig.getInstance().setRetryTime(1000);
```

#### 如何设置请求超时时间？

* 全局配置（所有接口都生效）

```java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
builder.readTimeout(5000, TimeUnit.MILLISECONDS);
builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
EasyConfig.with(builder.build())
        .into();
```

* 局部配置（只在某个请求生效）

```java
OkHttpClient.Builder builder = EasyConfig.getInstance().getClient().newBuilder();
builder.readTimeout(5000, TimeUnit.MILLISECONDS);
builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
builder.connectTimeout(5000, TimeUnit.MILLISECONDS);

EasyHttp.post(this)
        .api(new XxxApi())
        .client(builder.build())
        .request(new HttpCallback<HttpData<Void>>(this) {

            @Override
            public void onSucceed(HttpData<Void> data) {

            }
        });
```

#### 如何设置不打印日志？

```java
EasyConfig.getInstance().setLogEnabled(false);
```

#### 框架指定只能传入 LifecycleOwner，我想传入其他对象怎么办？

* 其中 AppCompatActivity 和 AndroidX.Fragment 都是 LifecycleOwner 子类的，这个是毋庸置疑的

* 但是你如果传入的是 Activity 对象，并非 AppCompatActivity 对象，那么你可以这样写

```java
EasyHttp.post(new ActivityLifecycle(this))
        .api(new XxxApi())
        .request(new HttpCallback<HttpData<XxxBean>>(this) {

            @Override
            public void onSucceed(HttpData<XxxBean> result) {

            }
        });
```

* 如果以上条件都不满足，但是你就是想在某个地方请求网络，那么你可以这样写

```java
EasyHttp.post(new ApplicationLifecycle())
        .api(new XxxApi())
        .tag("abc")
        .request(new HttpCallback<HttpData<XxxBean>>(this) {

            @Override
            public void onSucceed(HttpData<XxxBean> result) {

            }
        });
```

* 但是你需要注意，传入 ApplicationLifecycle 将意味着框架无法自动把控请求的生命周期

* 如果你采用了这样的写法，那么为了避免内存泄漏或者崩溃的事情发生

* 需要你在请求的时候设置对应的 Tag，然后在恰当的时机手动取消请求（一般都在销毁或者退出的时候取消请求）

#### 如何取消已发起的请求？

```java
// 取消和这个 LifecycleOwner 关联的请求
EasyHttp.cancel(LifecycleOwner lifecycleOwner);
// 取消指定 Tag 标记的请求
EasyHttp.cancel(Object tag);
// 取消所有请求
EasyHttp.cancel();
```