# 目录

* [集成文档](#集成文档)

    * [配置权限](#配置权限)

    * [Http 明文请求](#http-明文请求)

    * [服务器配置](#服务器配置)

    * [框架初始化](#框架初始化)

* [使用文档](#使用文档)

    * [配置接口](#配置接口)

    * [发起请求](#发起请求)

    * [上传文件](#上传文件)

    * [下载文件](#下载文件)

    * [分区存储适配](#分区存储适配)

    * [发起同步请求](#发起同步请求)

    * [设置请求缓存](#设置请求缓存)

    * [搭配协程使用](#搭配协程使用)

* [疑难解答](#疑难解答)

    * [如何设置 Cookie](#如何设置-cookie)

    * [如何添加或者删除全局参数](#如何添加或者删除全局参数)

    * [如何动态添加全局的参数或者请求头](#如何动态添加全局的参数或者请求头)

    * [如何在请求中忽略某个全局参数](#如何在请求中忽略某个全局参数)

    * [如何获取服务器配置](#如何获取服务器配置)

    * [如何修改接口的服务器配置](#如何修改接口的服务器配置)

    * [如何配置多域名](#如何配置多域名)

    * [如何修改参数的提交方式](#如何修改参数的提交方式)

    * [如何对接口进行加密或者解密](#如何对接口进行加密或者解密)

    * [如何忽略某个参数](#如何忽略某个参数)

    * [如何传入请求头](#如何传入请求头)

    * [如何传入动态的请求头](#如何传入动态的请求头)

    * [如何重命名参数或者请求头的名称](#如何重命名参数或者请求头的名称)

    * [如何上传文件](#如何上传文件)

    * [如何上传文件列表](#如何上传文件列表)

    * [如何设置超时重试](#如何设置超时重试)

    * [如何设置请求超时时间](#如何设置请求超时时间)

    * [如何设置不打印日志](#如何设置不打印日志)

    * [如何修改日志打印策略](#如何修改日志打印策略)

    * [如何取消已发起的请求](#如何取消已发起的请求)

    * [如何延迟发起一个请求](#如何延迟发起一个请求)

    * [如何对接口路径进行动态化拼接](#如何对接口路径进行动态化拼接)

    * [如何动态化整个请求的 url](#如何动态化整个请求的-url)

    * [Https 如何配置信任所有证书](#https-如何配置信任所有证书)

    * [我不想一个接口写一个类怎么办](#我不想一个接口写一个类怎么办)

    * [框架只能传入 LifecycleOwner 该怎么办](#框架只能传入-lifecycleowner-该怎么办)

    * [如何在 ViewModel 中使用 EasyHttp 请求网络](#如何在-viewmodel-中使用-easyhttp-请求网络)

    * [我想取消请求时显示的加载对话框该怎么办](#我想取消请求时显示的加载对话框该怎么办)

    * [我想用 Json 数组作为参数进行上传该怎么办](#我想用-json-数组作为参数进行上传该怎么办)

    * [接口参数的 Key 值是动态变化的该怎么办](#接口参数的-key-值是动态变化的该怎么办)

    * [如何设置自定义的 UA 标识](#如何设置自定义的-ua-标识)

    * [我想修改请求回调所在的线程该怎么办](#我想修改请求回调所在的线程该怎么办)

    * [我想自定义一个 RequestBody 进行请求该怎么办](#我想自定义一个-requestbody-进行请求该怎么办)

    * [我想自定义请求头中的 ContentType 该怎么做](#我想自定义请求头中的-contenttype-该怎么做)

    * [我想自定义 Get 请求参数中的 key 和 value 该怎么做](#我想自定义-get-请求参数中的-key-和-value-该怎么做)

    * [我想在 Post 请求中定义类似 Get 请求参数该怎么做](#我想在-post-请求中定义类似-get-请求参数该怎么做)

* [搭配 RxJava](#搭配-rxjava)

    * [准备工作](#准备工作)

    * [多个请求串行](#多个请求串行)

    * [发起轮询请求](#发起轮询请求)

    * [对返回的数据进行包装](#对返回的数据进行包装)

* [支持 Protobuf](#支持-protobuf)

    * [准备工作](#准备工作)

    * [请求体解析成 Protobuf](#请求体解析成-protobuf)

    * [响应体解析支持 Protobuf](#响应体解析支持-protobuf)

# 集成文档

#### 配置权限

```xml
<!-- 联网权限 -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- 访问网络状态 -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

#### Http 明文请求

* **Android 9.0** 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉。

* 如果当前应用的请求是 http 请求，而非 https，这样就会导系统禁止当前应用进行该请求，如果 WebView 的 url 用 http 协议，同样会出现加载失败，https 不受影响

* 在 res 下新建一个 xml 目录，然后创建一个名为：`network_security_config.xml` 文件 ，该文件内容如下

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

* 然后在 AndroidManifest.xml application 标签内应用上面的 xml 配置

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config" />
```

#### 服务器配置

```java
public class RequestServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.baidu.com/";
    }

    @NonNull
    @Override
    public RequestBodyType getBodyType() {
        // 参数以 Json 格式提交（默认是表单）
        return RequestBodyType.JSON;
    }
}
```

#### 框架初始化

* 需要配置请求结果处理，具体封装可以参考 [RequestHandler](app/src/main/java/com/hjq/easy/demo/http/model/RequestHandler.java)

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .build();

EasyConfig.with(okHttpClient)
        // 是否打印日志
        .setLogEnabled(BuildConfig.DEBUG)
        // 设置服务器配置（必须设置）
        .setServer(server)
        // 设置请求处理策略（必须设置）
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

* 上述是创建配置，更新配置可以使用

```java
EasyConfig.getInstance()
        .addParam("token", data.getData().getToken());
```

# 使用文档

#### 配置接口

```java
public final class LoginApi implements IRequestApi {

    @NonNull
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

    * @HttpRename：重新定义这个字段发送给后台的参数或者请求头名称

* 可在这个类实现一些接口

    * implements IRequestHost：实现这个接口之后可以重新指定这个请求的主机地址

    * implements IRequestType：实现这个接口之后可以重新指定这个请求的提交方式

    * implements IRequestCache：实现这个接口之后可以重新指定这个请求的缓存模式

    * implements IRequestClient：实现这个接口之后可以重新指定这个请求所用的 OkHttpClient 对象

* 字段作为请求参数的衡量标准

    * 假设某个字段的属性值为空，那么这个字段将不会作为请求参数发送给后台

    * 假设果某个字段类型是 String，属性值是空字符串，那么这个字段就会作为请求参数，如果是空对象则不会

    * 假设某个字段类型是 int，因为基本数据类型没有空值，所以这个字段一定会作为请求参数，但是可以换成 Integer 对象来避免，因为 Integer 的默认值是 null

* 我举个栗子：[https://www.baidu.com/api/user/getInfo](https://www.baidu.com/)，那么标准的写法就是

```java
public final class XxxApi implements IRequestServer, IRequestApi {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.baidu.com/";
    }

    @NonNull
    @Override
    public String getApi() {
        return "user/getInfo";
    }
}
```

#### 发起请求

* 需要配置请求状态及生命周期处理，具体封装可以参考 [BaseActivity](app/src/main/java/com/hjq/easy/demo/BaseActivity.java)

```java
EasyHttp.post(this)
        .api(new LoginApi()
                .setUserName("Android 轮子哥")
                .setPassword("123456"))
        .request(new HttpCallbackProxy<HttpData<LoginBean>>(activity) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<LoginBean> data) {
                toast("登录成功");
            }
        });
```

* 这里展示 post 用法，另外 EasyHttp 还支持 get、head、delete、put、patch 请求方式，这里不再过多演示

#### 上传文件

```java
public final class UpdateImageApi implements IRequestApi, IRequestType {

    @NonNull
    @Override
    public String getApi() {
        return "upload/";
    }

    @NonNull
    @Override
    public RequestBodyType getBodyType() {
        // 上传文件需要使用表单的形式提交
        return RequestBodyType.FORM;
    }

    /** 本地图片 */
    private File image;

    public UpdateImageApi(File image) {
        this.image = image;
    }

    public UpdateImageApi setImage(File image) {
        this.image = image;
        return this;
    }
}
```

```java
EasyHttp.post(this)
        .api(new UpdateImageApi(file))
        .request(new OnUpdateListener<Void>() {

            @Override
            public void onUpdateStart(@NonNull IRequestApi api) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUpdateProgressChange(int progress) {
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onUpdateSuccess(@NonNull Void result) {
                toast("上传成功");
            }

            @Override
            public void onUpdateFail(@NonNull Throwable throwable) {
                toast("上传失败");
            }

            @Override
            public void onUpdateEnd(@NonNull IRequestApi api) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
```

* **需要注意的是：如果上传的文件过多或者过大，可能会导致请求超时，可以重新设置本次请求超时时间，超时时间建议根据文件大小而定，具体设置超时方式文档有介绍，可以在本页面直接搜索。**

* 当然除了可以使用 `File` 类型的对象进行上传，还可以使用 `FileContentResolver`、`InputStream`、`RequestBody`、`MultipartBody.Part` 类型的对象进行上传，如果你需要批量上传，请使用 `List<File>`、`List<FileContentResolver>`、`List<InputStream>`、`List<RequestBody>`、`List<MultipartBody.Part>`、 类型的对象来做批量上传。

#### 下载文件

* 下载缓存策略：在指定下载文件 md5 或者后台有返回 md5 的情况下，下载框架默认开启下载缓存模式，如果这个文件已经存在手机中，并且经过 md5 校验文件完整，框架就不会重复下载，而是直接回调下载监听。减轻服务器压力，减少用户等待时间。

```java
EasyHttp.download(this)
        .method(HttpMethod.GET)
        .file(new File(Environment.getExternalStorageDirectory(), "微信.apk"))
        //.url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
        .url("http://dldir1.qq.com/weixin/android/weixin708android1540.apk")
        .md5("2E8BDD7686474A7BC4A51ADC3667CABF")
        // 设置断点续传（默认不开启）
        //.resumableTransfer(true)
        .listener(new OnDownloadListener() {

            @Override
            public void onDownloadStart(@NonNull File file) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDownloadProgressChange(@NonNull File file, int progress) {
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onDownloadSuccess(@NonNull File file) {
                toast("下载完成：" + file.getPath());
                installApk(XxxActivity.this, file);
            }

            @Override
            public void onDownloadFail(@NonNull File file, @NonNull Throwable throwable) {
                toast("下载失败：" + throwable.getMessage());
                file.delete();
            }

            @Override
            public void onDownloadEnd(@NonNull File file) {
                mProgressBar.setVisibility(View.GONE);
            }

        }).start();
```

#### 分区存储适配

* 在 Android 10 之前，我们在读写外部存储的时候，可以直接使用 File 对象来上传或者下载文件，但是在 Android 10 之后，如果你的项目需要 Android 10 分区存储的特性，那么在读写外部存储文件的时候，就不能直接使用 File 对象，因为 `ContentResolver.insert` 返回是一个 `Uri` 对象，这个时候就需要使用到框架提供的 `FileContentResolver` 对象了（这个对象是 File 的子类），具体使用案例如下：

```java
File outputFile;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    ContentValues values = new ContentValues();
    .........
    // 生成一个新的 uri 路径
    Uri outputUri = getContentResolver().insert(MediaStore.Xxx.Media.EXTERNAL_CONTENT_URI, values);
    // 适配 Android 10 分区存储特性
    outputFile = new FileContentResolver(context, outputUri);
} else {
    outputFile = new File(xxxx);
}

EasyHttp.post(this)
        .api(new XxxApi()
                .setImage(outputFile))
        .request(new HttpCallbackProxy<Xxx <Xxx>>(this) {

            @Override
            public void onHttpSuccess(@NonNull Xxx<Xxx> data) {

            }
        });
```

* 这是上传的案例，下载也同理，这里不再赘述。

#### 发起同步请求

* 需要注意的是：同步请求是耗时操作，不能在主线程中执行，请务必保证此操作在子线程中执行

```java
PostRequest postRequest = EasyHttp.post(MainActivity.this);
try {
    HttpData<SearchBean> data = postRequest
            .api(new SearchBlogsApi()
                    .setKeyword("搬砖不再有"))
            .execute(new ResponseClass<HttpData<SearchBean>>() {});
    toast("请求成功，请看日志");
} catch (Throwable throwable) {
    toast(throwable.getMessage());
}
```

#### 设置请求缓存

* 需要先实现读取和写入缓存的接口，如果已配置则可以跳过，这里以 MMKV 为例

```java
public final class RequestHandler implements IRequestHandler {

    @Nullable
    @Override
    public Object readCache(@NonNull HttpRequest<?> httpRequest, @NonNull Type type, long cacheTime) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        String cacheValue = HttpCacheManager.readHttpCache(cacheKey);
        if (cacheValue == null || cacheValue.isEmpty() || "{}".equals(cacheValue)) {
            return null;
        }
        EasyLog.printLog(httpRequest, "----- read cache key -----");
        EasyLog.printJson(httpRequest, cacheKey);
        EasyLog.printLog(httpRequest, "----- read cache value -----");
        EasyLog.printJson(httpRequest, cacheValue);
        EasyLog.printLog(httpRequest, "cacheTime = " + cacheTime);
        boolean cacheInvalidate = HttpCacheManager.isCacheInvalidate(cacheKey, cacheTime);
        EasyLog.printLog(httpRequest, "cacheInvalidate = " + cacheInvalidate);
        if (cacheInvalidate) {
            // 表示缓存已经过期了，直接返回 null 给外层，表示缓存不可用
            return null;
        }
        return GsonFactory.getSingletonGson().fromJson(cacheValue, type);
    }

    @Override
    public boolean writeCache(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Object result) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        String cacheValue = GsonFactory.getSingletonGson().toJson(result);
        if (cacheValue == null || cacheValue.isEmpty() || "{}".equals(cacheValue)) {
            return false;
        }
        EasyLog.printLog(httpRequest, "----- write cache key -----");
        EasyLog.printJson(httpRequest, cacheKey);
        EasyLog.printLog(httpRequest, "----- write cache value -----");
        EasyLog.printJson(httpRequest, cacheValue);
        boolean writeHttpCacheResult = HttpCacheManager.writeHttpCache(cacheKey, cacheValue);
        EasyLog.printLog(httpRequest, "writeHttpCacheResult = " + writeHttpCacheResult);
        boolean refreshHttpCacheTimeResult = HttpCacheManager.setHttpCacheTime(cacheKey, System.currentTimeMillis());
        EasyLog.printLog(httpRequest, "refreshHttpCacheTimeResult = " + refreshHttpCacheTimeResult);
        return writeHttpCacheResult && refreshHttpCacheTimeResult;
    }

    @Override
    public boolean deleteCache(@NonNull HttpRequest<?> httpRequest) {
        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
        EasyLog.printLog(httpRequest, "----- delete cache key -----");
        EasyLog.printJson(httpRequest, cacheKey);
        boolean deleteHttpCacheResult = HttpCacheManager.deleteHttpCache(cacheKey);
        EasyLog.printLog(httpRequest, "deleteHttpCacheResult = " + deleteHttpCacheResult);
        return deleteHttpCacheResult;
    }

    @Override
    public void clearCache() {
        HttpCacheManager.clearCache();
    }
```

```java
public final class HttpCacheManager {

    private static final MMKV HTTP_CACHE_CONTENT = MMKV.mmkvWithID("http_cache_content");;

    private static final MMKV HTTP_CACHE_TIME = MMKV.mmkvWithID("http_cache_time");

    /**
     * 生成缓存的 key
     */
    @NonNull
    public static String generateCacheKey(@NonNull HttpRequest<?> httpRequest) {
        IRequestApi requestApi = httpRequest.getRequestApi();
        return "请替换成当前的用户 id" + "\n" + requestApi.getApi() + "\n" + GsonFactory.getSingletonGson().toJson(requestApi);
    }

    /**
     * 读取缓存
     */
    public static String readHttpCache(@NonNull String cacheKey) {
        String cacheValue = HTTP_CACHE_CONTENT.getString(cacheKey, null);
        if (cacheValue == null || cacheValue.isEmpty() || "{}".equals(cacheValue)) {
            return null;
        }
        return cacheValue;
    }

    /**
     * 写入缓存
     */
    public static boolean writeHttpCache(String cacheKey, String cacheValue) {
        return HTTP_CACHE_CONTENT.putString(cacheKey, cacheValue).commit();
    }

    /**
     * 删除缓存
     */
    public static boolean deleteHttpCache(String cacheKey) {
        return HTTP_CACHE_CONTENT.remove(cacheKey).commit();
    }

    /**
     * 清理缓存
     */
    public static void clearCache() {
        HTTP_CACHE_CONTENT.clearMemoryCache();
        HTTP_CACHE_CONTENT.clearAll();

        HTTP_CACHE_TIME.clearMemoryCache();
        HTTP_CACHE_TIME.clearAll();
    }

    /**
     * 获取 Http 写入缓存的时间
     */
    public static long getHttpCacheTime(String cacheKey) {
        return HTTP_CACHE_TIME.getLong(cacheKey, 0);
    }

    /**
     * 设置 Http 写入缓存的时间
     */
    public static boolean setHttpCacheTime(String cacheKey, long cacheTime) {
        return HTTP_CACHE_TIME.putLong(cacheKey, cacheTime).commit();
    }

    /**
     * 判断缓存是否过期
     */
    public static boolean isCacheInvalidate(String cacheKey, long maxCacheTime) {
        if (maxCacheTime == Long.MAX_VALUE) {
            // 表示缓存长期有效，永远不会过期
            return false;
        }
        long httpCacheTime = getHttpCacheTime(cacheKey);
        if (httpCacheTime == 0) {
            // 表示不知道缓存的时间，这里默认当做已经过期了
            return true;
        }
        return httpCacheTime + maxCacheTime < System.currentTimeMillis();
    }
}
```

* 最后记得在应用启动的时候初始化 MMKV

```java
public final class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        MMKV.initialize(this);
    }
}
```

* 首先请求缓存模式有四种方式，都在 `CacheMode` 这个枚举类中

```java
public enum CacheMode {

    /**
     * 默认（按照 Http 协议来缓存）
     */
    DEFAULT,

    /**
     * 不使用缓存（禁用 Http 协议缓存）
     */
    NO_CACHE,

    /**
     * 只使用缓存
     *
     * 已有缓存的情况下：读取缓存 -> 回调成功
     * 没有缓存的情况下：请求网络 -> 写入缓存 -> 回调成功
     */
    USE_CACHE_ONLY,

    /**
     * 优先使用缓存
     *
     * 已有缓存的情况下：先读缓存 —> 回调成功 —> 请求网络 —> 刷新缓存
     * 没有缓存的情况下：请求网络 -> 写入缓存 -> 回调成功
     */
    USE_CACHE_FIRST,

    /**
     * 只在网络请求失败才去读缓存
     */
    USE_CACHE_AFTER_FAILURE
}
```

* 为某个接口设置缓存模式

```java
public final class XxxApi implements IRequestApi, IRequestCache {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/";
    }

    @NonNull
    @Override
    public CacheMode getCacheMode() {
        // 设置优先使用缓存
        return CacheMode.USE_CACHE_FIRST;
    }
}
```

* 全局设置缓存模式

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public CacheMode getCacheMode() {
        // 只在请求失败才去读缓存
        return CacheMode.USE_CACHE_AFTER_FAILURE;
    }
}
```

#### 搭配协程使用

* 可以使用同步请求搭配协程做处理，使用代码如下：

```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    try {
        val bean = EasyHttp.post(this@XxxActivity)
            .api(XxxApi().apply {
                setXxx(xxx)
                setXxxx(xxxx)
            })
            .execute(object : ResponseClass<HttpData<XxxBean?>>() {})
        withContext(Dispatchers.Main) {
            // 在这里进行 UI 刷新
        }
    } catch (throwable: Throwable) {
        toast(throwable.message)
    }
}
```

* 如果你对协程的使用不太熟悉，推荐你看一下[这篇文章](https://www.jianshu.com/p/2e0746c7d4f3)

# 疑难解答

#### 如何设置 Cookie

* EasyHttp 是基于 OkHttp 封装的，而 OkHttp 本身就是支持设置 Cookie，所以用法和 OkHttp 是一样的

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .cookieJar(new XxxCookieJar())
        .build();

EasyConfig.with(okHttpClient)
        .setXxx()
        .into();
```

#### 如何添加或者删除全局参数

* 添加全局请求参数

```java
EasyConfig.getInstance().addParam("key", "value");
```

* 移除全局请求参数

```java
EasyConfig.getInstance().removeParam("key");
```

* 添加全局请求头

```java
EasyConfig.getInstance().addHeader("key", "value");
```

* 移除全局请求头

```java
EasyConfig.getInstance().removeHeader("key");
```

#### 如何动态添加全局的参数或者请求头

```java
EasyConfig.getInstance().setInterceptor(new IRequestInterceptor() {

    @Override
    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
        // 添加请求头
        headers.put("key", "value");
        // 添加参数
        params.put("key", "value");
    }
});
```

#### 如何在请求中忽略某个全局参数

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @HttpIgnore
    private String token;
}
```

#### 如何获取服务器配置

```java
IRequestServer server = EasyConfig.getInstance().getServer();
// 获取当前全局的服务器主机地址
String host = server.getHost();
```

#### 如何修改接口的服务器配置

* 先定义一个服务器配置

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
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

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

* 如果不想单独定义一个类，也可以这样写

```java
public final class XxxApi implements IRequestServer, IRequestApi {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

#### 如何配置多域名

* 先定义一个普通接口的测试服和正式服的配置

```java
public class TestServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.test.xxxxxxx.com/";
    }
}
```

```java
public class ReleaseServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }
}
```

* 再将它应用到全局配置中

```java
IRequestServer server;
if (BuildConfig.DEBUG) {
    server = new TestServer();
} else {
    server = new ReleaseServer();
}
EasyConfig.getInstance().setServer(server);
```

* 假设要为 H5 业务模块设定特定服务器配置，可以这样做

```java
public class H5Server implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        IRequestServer server = EasyConfig.getInstance().getServer();
        if (server instanceof TestServer) {
            return "https://www.test.h5.xxxxxxx.com/";
        }
        return "https://www.h5.xxxxxxx.com/";
    }
}
```

* 在配置接口的时候继承 H5Server 就可以了，其他 H5 模块的配置也是雷同

```java
public final class UserAgreementApi extends H5Server implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "user/agreement";
    }
}
```

#### 如何修改参数的提交方式

* 以表单的形式提交参数（默认）

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public RequestBodyType getBodyType() {
        return RequestBodyType.FORM;
    }
}
```

* 以 Json 的形式提交参数

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public RequestBodyType getBodyType() {
        return RequestBodyType.JSON;
    }
}
```

* 当然也支持对某个接口进行单独配置

```java
public final class XxxApi implements IRequestApi, IRequestType {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @NonNull
    @Override
    public RequestBodyType getBodyType() {
        return RequestBodyType.JSON;
    }
}
```

* 表单和 Json 方式提交的优缺点对比

|  场景  | 表单方式  | Json 方式 |
| :----: | :------: |  :-----: |
|   多级参数  |  不支持  |   支持  |
|   文件上传  |   支持  |  不支持  |

#### 如何对接口进行加密或者解密

* 关于这个问题，其实可以利用框架中提供的 IRequestInterceptor 接口来实现，通过重写接口中的对应方法进行拦截，修改对象的内容从而达到加密的效果。

```java
public interface IRequestInterceptor {

    /**
     * 拦截参数
     *
     * @param httpRequest   接口对象
     * @param params        请求参数
     * @param headers       请求头参数
     */
    default void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {}

    /**
     * 拦截请求头
     *
     * @param httpRequest   接口对象
     * @param request       请求头对象
     * @return              返回新的请求头
     */
    @NonNull
    default Request interceptRequest(@NonNull HttpRequest<?> httpRequest, @NonNull Request request) {
        return request;
    }

    /**
     * 拦截器响应头
     *
     * @param httpRequest   接口对象
     * @param response      响应头对象
     * @return              返回新的响应头
     */
    @NonNull
    default Response interceptResponse(@NonNull HttpRequest<?> httpRequest, @NonNull Response response) {
        return response;
    }
}
```

```java
// 在框架初始化的时候设置拦截器
EasyConfig.with(okHttpClient)
        // 设置请求参数拦截器
        .setInterceptor(new XxxInterceptor())
        .into();
```

* 如果你只想对某个接口进行加解密，可以让 Api 类单独实现 IRequestInterceptor 接口，这样它就不会走全局的配置。

#### 如何忽略某个参数

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @HttpIgnore
    private String address;
}
```

#### 如何传入请求头

* 给字段加上 `@HttpHeader` 注解即可，则表示这个字段是一个请求头，如果没有加上此注解，则框架默认将字段作为请求参数

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @HttpHeader
    private String time;
}
```

#### 如何传入动态的请求头

* 定义一个字段，并在上面添加 `@HTTPHeader` 注解即可

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @HttpHeader
    private String time;
}
```

#### 如何重命名参数或者请求头的名称

* 传入一个 HashMap 类型的字段，并在上面添加 `@HTTPHeader` 注解即可

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @HTTPHeader
    private HashMap<String,String> headers;
}
```

#### 如何上传文件

* 使用 File 对象上传

```java
public final class XxxApi implements IRequestApi {

    @NonNull
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

    @NonNull
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

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    private RequestBody requestBody;
}
```

#### 如何上传文件列表

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    private List<File> files;
}
```

#### 如何设置超时重试

```java
// 设置请求重试次数
EasyConfig.getInstance().setRetryCount(3);
// 设置请求重试时间
EasyConfig.getInstance().setRetryTime(1000);
```

#### 如何设置请求超时时间

* 全局配置（针对所有接口都生效）

```java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
builder.readTimeout(5000, TimeUnit.MILLISECONDS);
builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
builder.connectTimeout(5000, TimeUnit.MILLISECONDS);

EasyConfig.with(builder.build())
        .into();
```

* 局部配置（只在某个接口上生效）

```java
public final class XxxApi implements IRequestApi, IRequestClient {

    @NonNull
    @Override
    public String getApi() {
        return "xxxx/";
    }

    @NonNull
    @Override
    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = EasyConfig.getInstance().getOkHttpClient().newBuilder();
        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        return builder.build();
    }
}
```

#### 如何设置不打印日志

```java
EasyConfig.getInstance().setLogEnabled(false);
```

#### 如何修改日志打印策略

* 可以先定义一个类实现 [IRequestLogStrategy](library/src/main/java/com/hjq/http/config/IRequestLogStrategy.java) 接口，然后在框架初始化的时候传入即可

```java
EasyConfig.with(okHttpClient)
        .......
        // 设置自定义的日志打印策略
        .setLogStrategy(new XxxStrategy())
        .into();
```

* 需要修改日志打印策略的场景

    * 需要将请求的日志写入到本地

    * 需要修改打印的请求日志格式

#### 如何取消已发起的请求

```java
// 根据 TAG 取消请求任务
EasyHttp.cancelByTag(Object tag);
// 取消指定 Tag 标记的请求
EasyHttp.cancelByTag(Object tag);
// 取消所有请求
EasyHttp.cancelAll();
```

#### 如何延迟发起一个请求

```java
EasyHttp.post(MainActivity.this)
        .api(new XxxApi())
        // 延迟 5 秒后请求
        .delay(5000)
        .request(new HttpCallbackProxy<HttpData<XxxBean>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<XxxBean> result) {

            }
        });
```

#### 如何对接口路径进行动态化拼接

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "article/query/" + pageNumber + "/json";
    }

    @HttpIgnore
    private int pageNumber;

    public XxxApi setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }
}
```

#### 如何动态化整个请求的 url

```java
EasyHttp.post(this)
        .api(new RequestUrl("https://xxxx.com/aaaa"))
        .request(new HttpCallbackProxy<Xxx>(this) {

            @Override
            public void onHttpSuccess(@NonNull Xxx result) {

            }
        });
```

#### Https 如何配置信任所有证书

* 在初始化 OkHttp 的时候这样设置

```java
HttpSslConfig sslConfig = HttpSslFactory.generateSslConfig();
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .sslSocketFactory(sslConfig.getsSLSocketFactory(), sslConfig.getTrustManager())
        .hostnameVerifier(HttpSslFactory.generateUnSafeHostnameVerifier())
        .build();
```
* 但是不推荐这样做，因为这样是不安全的，意味着每个请求都不会用 Https 去校验

* 当然框架中也提供了一些生成的证书的 API，具体请参见 com.hjq.http.ssl 包下的类

#### 我不想一个接口写一个类怎么办

* 先定义一个 URL 管理类，将 URL 配置到这个类中

```java
public final class HttpUrls {

    /** 获取用户信息 */
    public static final String GET_USER_INFO =  "user/getUserInfo";
}
```

* 然后在 EasyHttp 引入接口路径

```java
EasyHttp.post(this)
        .api(HttpUrls.GET_USER_INFO)
        .request(new HttpCallbackProxy<HttpData<XxxBean>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<XxxBean> result) {

            }
        });
```

* 不过这种方式只能应用于没有参数的接口，有参数的接口还是需要写一个类，因为框架只会在 Api 类中去解析参数。

* 虽然 EasyHttp 开放了这种写法，但是身为作者的我并不推荐你这样写，因为这样写会导致扩展性很差，比如后续加参数，还要再改回来，并且无法对接口进行动态化配置。

#### 框架只能传入 LifecycleOwner 该怎么办

* 其中 `androidx.appcompat.app.AppCompatActivity` 和 `androidx.fragment.app.Fragment` 都是 LifecycleOwner 子类的，这个是毋庸置疑的，可以直接当做 LifecycleOwner 传给框架

* 但是你如果传入的是 `android.app.Activity` 对象，并非 `androidx.appcompat.app.AppCompatActivity` 对象，那么你可以这样写

```java
EasyHttp.post(new ActivityLifecycle(this))
        .api(new XxxApi())
        .request(new HttpCallbackProxy<HttpData<XxxBean>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<XxxBean> result) {

            }
        });
```

* 如果你传入的是 `android.app.Fragment` 对象，并非 `androidx.fragment.app.Fragment` 对象，请将 Fragment 直接继承框架中的 LifecycleAppFragment 类，又或者在项目中封装一个带有 Lifecycle 特性的 Fragment 基类

* 你如果想在 `android.app.Service` 中使用 EasyHttp，请将 Service 直接继承框架中的 LifecycleService 类，又或者在项目中封装一个带有 Lifecycle 特性的 Service 基类

* 如果以上条件都不满足，但是你就是想在某个地方请求网络，那么你可以这样写

```java
EasyHttp.post(ApplicationLifecycle.getInstance())
        .api(new XxxApi())
        .tag("abc")
        .request(new OnHttpListener<HttpData<XxxBean>>() {

            @Override
            public void onHttpSuccess(@NonNull HttpData<XxxBean> result) {

            }

            @Override
            public void onHttpFail(@NonNull Throwable throwable) {

            }
        });
```

* 需要注意的是，传入 ApplicationLifecycle 将意味着框架无法自动把控请求的生命周期，如果在 Application 中这样写是完全可以的，但是不能在 Activity 或者 Service 中这样写，因为这样可能会导致内存泄漏。

* 除了 Application，如果你在 Activity 或者 Service 中采用了 ApplicationLifecycle 的写法，那么为了避免内存泄漏或者崩溃的事情发生，需要你在请求的时候设置对应的 Tag，然后在恰当的时机手动取消请求（一般在 Activity 或者 Service 销毁或者退出的时候取消请求）。

```java
EasyHttp.cancelByTag("abc");
```

#### 如何在 ViewModel 中使用 EasyHttp 请求网络

* 第一步：封装一个 BaseViewModel，并将 LifecycleOwner 特性植入进去

```java
public class BaseViewModel extends ViewModel implements LifecycleOwner {

    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    public BaseViewModel() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }
}
```

* 第二步：让业务 ViewModel 类继承至 BaseViewModel，具体案例如下

```java
public class XxxViewModel extends BaseViewModel {

    public void xxxx() {
        EasyHttp.post(this)
                .api(new XxxApi())
                .request(new OnHttpListener<HttpData<Xxx>>() {

                    @Override
                    public void onHttpSuccess(@NonNull HttpData<Xxx> result) {

                    }

                    @Override
                    public void onHttpFail(@NonNull Throwable throwable) {

                    }
                });
    }
}
```

#### 我想取消请求时显示的加载对话框该怎么办

* 首先这个加载对话框不是框架自带的，是可以修改或者取消的，主要有两种方式可供选择

* 第一种方式：重写 HttpCallbackProxy 类回调方法

```java
EasyHttp.post(this)
        .api(new XxxApi())
        .request(new HttpCallbackProxy<Xxx>(this) {

            @Override
            public void onHttpStart(@NonNull IRequestApi api) {
                // 重写方法并注释父类调用
                //super.onHttpStart(call);
            }

            @Override
            public void onHttpEnd(@NonNull IRequestApi api) {
                // 重写方法并注释父类调用
                //super.onHttpEnd(call);
            }
        });
```

* 第二种方式：直接实现 OnHttpListener 接口

```java

EasyHttp.post(this)
        .api(new XxxApi())
        .request(new OnHttpListener<Xxx>() {

            @Override
            public void onHttpSuccess(@NonNull Xxx result) {

            }

            @Override
            public void onHttpFail(@NonNull Throwable throwable) {

            }
        });
```

#### 我想用 Json 数组作为参数进行上传该怎么办

* 由于 Api 类最终会转换成一个 JsonObject 类型的字符串，如果你需要上传 JsonArray 类型的字符串，请使用以下方式实现

```java
List<Xxx> parameter = new ArrayList<>();
list.add(xxx);
list.add(xxx);
String json = gson.toJson(parameter);

EasyHttp.post(this)
        .api(new XxxApi())
        .body(new JsonRequestBody(json))
        .request(new HttpCallbackProxy<HttpData<Xxx>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<Xxx> result) {

            }
        });
```

* 但是我个人不推荐将 JsonArray 作为参数的根部类型，因为这样的接口后续的扩展性极差。

#### 接口参数的 Key 值是动态变化的该怎么办

* 框架是通过反射解析 Api 类中的字段来作为参数的，字段名作为参数的 Key 值，字段值作为参数的 Value 值，由于 Java 无法动态更改类的字段名，所以无法通过正常的手段进行修改，你如果有这种需求，请通过以下方式进行实现

```java
HashMap<String, Object> parameter = new HashMap<>();

// 添加全局参数
HashMap<String, Object> globalParams = EasyConfig.getInstance().getParams();
Set<String> keySet = globalParams.keySet();
for (String key : keySet) {
    parameter.put(key, globalParams.get(key));
}

// 添加自定义参数
parameter.put("key1", value1);
parameter.put("key2", value2);

String json = gson.toJson(parameter);
JsonRequestBody jsonRequestBody = new JsonRequestBody(json)

EasyHttp.post(this)
        .api(new XxxApi())
        .body(jsonRequestBody)
        .request(new HttpCallbackProxy<HttpData<Xxx>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<Xxx> result) {

            }
        });
```

#### 如何设置自定义的 UA 标识

* 首先 UA 是 User Agent 的简称，当我们没有设置自定义 UA 标识的时候，那么 OkHttp 会在 BridgeInterceptor 拦截器添加一个默认的 UA 标识，那么如何在 EasyHttp 设置自定义 UA 标识呢？其实很简单，UA 标识本质上其实就是一个请求头，在 EasyHttp 中添加一个请求头为 `User-Agent` 的参数即可，至于怎么添加请求头，前面的文档已经有介绍了，这里不再赘述。

#### 我想修改请求回调所在的线程该怎么办

```java
EasyHttp.post(this)
        .api(new XxxApi())
        // 表示回调是在子线程中进行
        .schedulers(ThreadSchedulers.IO)
        .request(new HttpCallbackProxy<HttpData<Xxx>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<Xxx> result) {

            }
        });
```

#### 我想自定义一个 RequestBody 进行请求该怎么办

* 在一些极端的情况下，框架无法满足使用的前提下，这个时候需要自定义 `RequestBody` 来实现，那么怎么使用自定义 `RequestBody` 呢？框架其实有开放方法，具体使用示例如下：

```java
EasyHttp.post(this)
        .api(new XxxApi())
        .body(RequestBody body)
        .request(new HttpCallbackProxy<HttpData<Xxx>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<Xxx> result) {

            }
        });
```

* 需要注意的是：由于 Post 请求是将参数放置到 `RequestBody` 上面，而一个请求只能设置一个 `RequestBody`，如果你设置了自定义 `body(RequestBody body)`，那么框架将不会去将 `XxxApi` 类中的字段解析成参数。另外除了 Post 请求，Put 请求和 Patch 请求也可以使用这种方式进行设置，这里不再赘述。

#### 我想自定义请求头中的 ContentType 该怎么做

* 具体的写法示例如下：

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxx";
    }

    @HttpHeader
    @HttpRename("Content-Type")
    private String contentType = "application/x-www-form-urlencoded;charset=utf-8";
}
```

* 需要注意的是：此功能仅是在框架 **11.5** 版本的时候加上的，之前的版本没有这一功能

#### 我想自定义 Get 请求参数中的 key 和 value 该怎么做

* 先自定义一个 Api 类，然后通过 `getApi` 方法将参数动态拼接上去

```java
public final class CustomParameterApi implements IRequestApi {

   @HttpIgnore
   @NonNull
   private final Map<String, String> parameters;

   public CustomParameterApi() {
      this(new HashMap<>());
   }

   public CustomParameterApi(@NonNull Map<String, String> parameters) {
      this.parameters = parameters;
   }

   @NonNull
   @Override
   public String getApi() {
      Set<String> keys = parameters.keySet();

      StringBuilder builder = new StringBuilder();
      int index = 0;
      for (String key : keys) {
         String value = parameters.get(key);

         if (index == 0) {
            builder.append("?");
         }
         builder.append(key)
                 .append("=")
                 .append(value);
         if (index < keys.size() - 1) {
            builder.append("&");
         }
         index++;
      }

      return "xxx/xxx" + builder;
   }

   public CustomParameterApi putParameter(String key, String value) {
      parameters.put(key, value);
      return this;
   }

   public CustomParameterApi removeParameter(String key) {
      parameters.remove(key);
      return this;
   }
}
```

* 外层可以通过以下方式进行调用

```java
CustomParameterApi api = new CustomParameterApi();
api.putParameter("key1", "value1");
api.putParameter("key2", "value2");

EasyHttp.get(this)
        .api(api)
        .request(new HttpCallbackProxy<Xxx>(this) {

            @Override
            public void onHttpSuccess(@NonNull Xxx result) {

            }
        });
```

* 需要注意的是：这种实现方式仅适用于在框架设计无法满足需求的情况下，其他情况下作者并不提倡用这种方式，因为这样不方便管理请求参数的 key，还是推荐大家使用在类上面定义字段的方式来实现。

#### 我想在 Post 请求中定义类似 Get 请求参数该怎么做

* 直接拼接请求的参数到 url 上面，并且忽略某个字段的值（避免被解析成 Post 参数）

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "article/query?pageNumber=" + pageNumber;
    }

    @HttpIgnore
    private int pageNumber;

    public XxxApi setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }
}
```

* Ps：一般情况下我是不建议这样写的，这样的请求看起来不伦不类，即长得像一个 Get 请求，但是实际上却是一个 Post 请求。

# 搭配 RxJava

#### 准备工作

* 添加远程依赖

```groovy
dependencies {
    // RxJava：https://github.com/ReactiveX/RxJava
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
    implementation 'io.reactivex.rxjava2:rxjava:2.2.12'
}
```

* 请注意 RxJava 需要自行处理生命周期，以免发生内存泄漏

#### 多个请求串行

```java
Observable.create(new ObservableOnSubscribe<HttpData<SearchBean>>() {

    @Override
    public void subscribe(ObservableEmitter<HttpData<SearchBean>> emitter) throws Exception {

        HttpData<SearchBean> data1;
        try {
            data1 = EasyHttp.post(MainActivity.this)
                    .api(new SearchBlogsApi()
                            .setKeyword("搬砖不再有"))
                    .execute(new ResponseClass<HttpData<SearchBean>>() {});
        } catch (Throwable throwable) {
            if (throwable instanceof Exception) {
                throw (Exception) throwable;
            } else {
                throw new RuntimeException(throwable);
            }
        }

        HttpData<SearchBean> data2;
        try {
            data2 = EasyHttp.post(MainActivity.this)
                    .api(new SearchBlogsApi()
                            .setKeyword(data1.getMessage()))
                    .execute(new ResponseClass<HttpData<SearchBean>>() {});
        } catch (Throwable throwable) {
            if (throwable instanceof Exception) {
                throw (Exception) throwable;
            } else {
                throw new RuntimeException(throwable);
            }
        }

        emitter.onNext(data2);
        emitter.onComplete();
    }
})
// 让被观察者执行在IO线程
.subscribeOn(Schedulers.io())
// 让观察者执行在主线程
.observeOn(AndroidSchedulers.mainThread())
.subscribe(new Consumer<HttpData<SearchBean>>() {

    @Override
    public void accept(HttpData<SearchBean> data) throws Exception {
        Log.i("EasyHttp", "最终结果为：" + data.getMessage());
    }

}, new Consumer<Throwable>() {

    @Override
    public void accept(Throwable throwable) throws Exception {
        toast(throwable.getMessage());
    }
});
```

#### 发起轮询请求

* 如果轮询的次数是有限，可以考虑使用 Http 请求来实现，但是如果轮询的次数是无限的，那么不推荐使用 Http 请求来实现，应当使用 WebSocket 来做，又或者其他长链接协议来做。

```java
// 发起轮询请求，共发起三次请求，第一次请求在 5 秒后触发，剩下两次在 1 秒 和 2 秒后触发
Observable.intervalRange(1, 3, 5000, 1000, TimeUnit.MILLISECONDS)
        // 让被观察者执行在 IO 线程
        .subscribeOn(Schedulers.io())
        // 让观察者执行在主线程
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                EasyHttp.post(MainActivity.this)
                        .api(new SearchBlogsApi()
                                .setKeyword("搬砖不再有"))
                        .request(new HttpCallbackProxy<HttpData<SearchBean>>(MainActivity.this) {

                            @Override
                            public void onHttpSuccess(@NonNull HttpData<SearchBean> result) {

                            }
                        });
            }
        });
```

#### 对返回的数据进行包装

```java
Observable.create(new ObservableOnSubscribe<HttpData<SearchBean>>() {

    @Override
    public void subscribe(ObservableEmitter<HttpData<SearchBean>> emitter) throws Exception {
        EasyHttp.post(MainActivity.this)
                .api(new SearchBlogsApi()
                        .setKeyword("搬砖不再有"))
                .request(new HttpCallbackProxy<HttpData<SearchBean>>(MainActivity.this) {

                    @Override
                    public void onHttpSuccess(@NonNull HttpData<SearchBean> result) {
                        emitter.onNext(result);
                        emitter.onComplete();
                    }

                    @Override
                    public void onHttpFail(@NonNull Throwable throwable) {
                        super.onHttpFail(throwable);
                        emitter.onError(throwable);
                    }
                });
    }
})
.map(new Function<HttpData<SearchBean>, String>() {

    @Override
    public String apply(HttpData<SearchBean> data) throws Exception {
        int curPage = data.getData().getCurPage();
        int pageCount = data.getData().getPageCount();
        return curPage + "/" + pageCount;
    }
})
// 让被观察者执行在 IO 线程
.subscribeOn(Schedulers.io())
// 让观察者执行在主线程
.observeOn(AndroidSchedulers.mainThread())
.subscribe(new Consumer<String>() {

    @Override
    public void accept(String s) throws Exception {
        Log.i("EasyHttp", "当前页码位置" + s);
    }

}, new Consumer<Throwable>() {

    @Override
    public void accept(Throwable throwable) throws Exception {
        toast(throwable.getMessage());
    }
});
```

# 支持 Protobuf

#### 准备工作

* 在项目根目录下得 `build.gradle` 文件加入以下配置

```groovy
buildscript {

    ......

    dependencies {
        // 自动生成 Protobuf 类插件：https://github.com/google/protobuf-gradle-plugin
        classpath 'com.google.protobuf:protobuf-gradle-plugin:0.9.3'
    }
}
```

* 在项目 app 模块下的 `build.gradle` 文件中加入远程依赖

```groovy
......

apply plugin: 'com.google.protobuf'

android {
    ......
    
    sourceSets {
        main {
            proto {
                // 指定 Protobuf 文件路径
                srcDir 'src/main/proto'
            }
        }
    }
}

protobuf {

    protoc {
        // 也可以配置本地编译器路径
        artifact = 'com.google.protobuf:protoc:3.23.0'
    }

    generateProtoTasks {
        all().each { task ->
            task.builtins {
                remove java
            }
            task.builtins {
                // 生产java源码
                java {}
            }
        }
    }
}

dependencies {

    ......

    // Protobuf：https://github.com/protocolbuffers/protobuf
    implementation 'com.google.protobuf:protobuf-java:3.23.1'
    implementation 'com.google.protobuf:protoc:3.23.0'
}
```

* 在 `app/src/main/` 新建一个名为 `proto` 文件夹，用于存放 Protobuf 相关文件，然后创建 `Person.proto` 文件，具体内容如下：

```text
syntax = "proto3";
package tutorial;

message Person {
    string name = 1;
    int32 id = 2;
    string email = 3;
    string phone = 4;
}
```

* 然后 `Rebuild Project`，就能看到插件自动生成的 `PersonOuterClass` 类，`PersonOuterClass` 类中还有一个名为 `Person` 的静态内部类

#### 请求体解析成 Protobuf

* 创建一个自定义的 RequestBody 类，用于将 Protocol 对象解析成流，建议存放在 `com.xxx.xxx/http/model` 包名下，

```java
public class ProtocolRequestBody extends RequestBody {

    /** MessageLite 对象 */
    private final MessageLite mMessageLite;
    /** 字节数组 */
    private final byte[] mBytes;

    public ProtocolRequestBody(MessageLite messageLite) {
        mMessageLite = messageLite;
        mBytes = messageLite.toByteArray();
    }

    @Override
    public MediaType contentType() {
        return ContentType.JSON;
    }

    @Override
    public long contentLength() {
        // 需要注意：这里需要用字节数组的长度来计算
        return mBytes.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.write(mBytes, 0, mBytes.length);
    }

    @NonNull
    @Override
    public String toString() {
        return mMessageLite.toString();
    }

    /**
     * 获取 MessageLite 对象
     */
    @NonNull
    public MessageLite getMessageLite() {
        return mMessageLite;
    }
}
```

* 发起请求示例

```java
// 假装生成一个 Protobuf 对象
Person person = Person.parseFrom("xxxxxxxxx".getBytes());

EasyHttp.post(this)
    .api(new XxxApi())
    .body(new ProtocolRequestBody(person))
    .request(new HttpCallbackProxy<HttpData<SearchBlogsApi.Bean>>(this) {

        @Override
        public void onHttpSuccess(@NonNull HttpData<SearchBlogsApi.Bean> result) {
            
        }
    });
```

#### 响应体解析支持 Protobuf

* 这个支持很简单了，只需要修改 `IRequestHandler` 接口的实现即可，具体的代码实现如下：

```
public final class RequestHandler implements IRequestHandler {

    ......

    @NonNull
    @Override
    public Object requestSuccess(@NonNull HttpRequest<?> httpRequest, @NonNull Response response,
                                 @NonNull Type type) throws Throwable {
        ......

        final Object result;

        try {
            if (type instanceof Class<?> && AbstractParser.class.isAssignableFrom((Class<?>) type)) {
                String simpleName = ((Class<?>) type).getSimpleName();
                Class<?> clazz = Class.forName("tutorial." + simpleName + ".OuterClass." + simpleName);
                Method parseFromMethod = clazz.getMethod("parseFrom", byte[].class);
                // 调用静态方法
                result = parseFromMethod.invoke(null, (Object) text.getBytes());
            } else {
                result = GsonFactory.getSingletonGson().fromJson(text, type);
            }
        } catch (JsonSyntaxException e) {
            // 返回结果读取异常
            throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
        }

        ......
        return result;
    }
}
```