# Table of Contents

* [Integration Documentation](#integration-documentation)

    * [Configure Permissions](#configure-permissions)

    * [HTTP Plaintext Requests](#http-plaintext-requests)

    * [Server Configuration](#server-configuration)

    * [Framework Initialization](#framework-initialization)

* [Usage Documentation](#usage-documentation)

    * [Configure API](#configure-api)

    * [Make a Request](#make-a-request)

    * [Upload Files](#upload-files)

    * [Download Files](#download-files)

    * [Scoped Storage Adaptation](#scoped-storage-adaptation)

    * [Make Synchronous Requests](#make-synchronous-requests)

    * [Set Request Cache](#set-request-cache)

    * [Use with Coroutines](#use-with-coroutines)

* [FAQ](#faq)

    * [How to Set Cookies](#how-to-set-cookies)

    * [How to Add or Remove Global Parameters](#how-to-add-or-remove-global-parameters)

    * [How to Dynamically Add Global Parameters or Headers](#how-to-dynamically-add-global-parameters-or-headers)

    * [How to Ignore a Global Parameter in a Request](#how-to-ignore-a-global-parameter-in-a-request)

    * [How to Get Server Configuration](#how-to-get-server-configuration)

    * [How to Modify API Server Configuration](#how-to-modify-api-server-configuration)

    * [How to Configure Multiple Domains](#how-to-configure-multiple-domains)

    * [How to Change Parameter Submission Method](#how-to-change-parameter-submission-method)

    * [How to Encrypt or Decrypt API](#how-to-encrypt-or-decrypt-api)

    * [How to Ignore a Parameter](#how-to-ignore-a-parameter)

    * [How to Pass Request Headers](#how-to-pass-request-headers)

    * [How to Pass Dynamic Request Headers](#how-to-pass-dynamic-request-headers)

    * [How to Rename Parameter or Header Name](#how-to-rename-parameter-or-header-name)

    * [How to Upload Files](#how-to-upload-files)

    * [How to Upload a List of Files](#how-to-upload-a-list-of-files)

    * [How to Set Timeout Retry](#how-to-set-timeout-retry)

    * [How to Set Request Timeout](#how-to-set-request-timeout)

    * [How to Disable Log Printing](#how-to-disable-log-printing)

    * [How to Change Log Printing Strategy](#how-to-change-log-printing-strategy)

    * [How to Cancel an Ongoing Request](#how-to-cancel-an-ongoing-request)

    * [How to Delay a Request](#how-to-delay-a-request)

    * [How to Dynamically Concatenate API Path](#how-to-dynamically-concatenate-api-path)

    * [How to Dynamically Set the Entire Request URL](#how-to-dynamically-set-the-entire-request-url)

    * [How to Trust All Certificates for HTTPS](#how-to-trust-all-certificates-for-https)

    * [What if I Don't Want to Write a Class for Each API](#what-if-i-dont-want-to-write-a-class-for-each-api)

    * [What if the Framework Only Accepts LifecycleOwner](#what-if-the-framework-only-accepts-lifecycleowner)

    * [How to Use EasyHttp in ViewModel](#how-to-use-easyhttp-in-viewmodel)

    * [How to Cancel the Loading Dialog When Cancelling a Request](#how-to-cancel-the-loading-dialog-when-cancelling-a-request)

    * [How to Upload a JSON Array as a Parameter](#how-to-upload-a-json-array-as-a-parameter)

    * [What if the Key of API Parameter is Dynamic](#what-if-the-key-of-api-parameter-is-dynamic)

    * [How to Set a Custom UA Identifier](#how-to-set-a-custom-ua-identifier)

    * [How to Change the Thread for Request Callback](#how-to-change-the-thread-for-request-callback)

    * [How to Use a Custom RequestBody for Requests](#how-to-use-a-custom-requestbody-for-requests)

    * [How to Customize ContentType in Request Header](#how-to-customize-contenttype-in-request-header)

    * [How to Customize Key and Value in Get Request Parameters](#how-to-customize-key-and-value-in-get-request-parameters)

    * [How to Define Get-like Parameters in Post Request](#how-to-define-get-like-parameters-in-post-request)

* [Use with RxJava](#use-with-rxjava)

    * [Preparation](#preparation)

    * [Multiple Requests in Series](#multiple-requests-in-series)

    * [Polling Requests](#polling-requests)

    * [Wrap Returned Data](#wrap-returned-data)

* [Support Protobuf](#support-protobuf)

    * [Preparation](#preparation-1)

    * [Parse Request Body as Protobuf](#parse-request-body-as-protobuf)

    * [Parse Response Body as Protobuf](#parse-response-body-as-protobuf)

# Integration Documentation

#### Configure Permissions

```xml
<!-- Network permission -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Access network status -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

#### HTTP Plaintext Requests

* **Android 9.0** restricts plaintext traffic network requests, non-encrypted traffic requests will be prohibited by the system.

* If the current application's request is an http request, but not https, this will lead to the system prohibiting the current application from performing this request, if the WebView's url uses the http protocol, it will also fail to load, https is unaffected

* Create a new xml directory in res, then create a file named: `network_security_config.xml`, the content of this file is as follows

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

* Then apply the above xml configuration in the application tag of AndroidManifest.xml

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config" />
```

#### Server Configuration

```java
public class RequestServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.baidu.com/";
    }

    @NonNull
    @Override
    public IHttpPostBodyStrategy getBodyType() {
        // Parameters submitted in Json format (default is form)
        return RequestBodyType.JSON;
    }
}
```

#### Framework Initialization

* Need to configure request result handling, specific encapsulation can refer to [RequestHandler](app/src/main/java/com/hjq/easy/demo/http/model/RequestHandler.java)

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .build();

EasyConfig.with(okHttpClient)
        // Whether to print logs
        .setLogEnabled(BuildConfig.DEBUG)
        // Set server configuration (must be set)
        .setServer(server)
        // Set request processing strategy (must be set)
        .setHandler(new RequestHandler())
        // Set request cache implementation strategy (not required)
        //.setCacheStrategy(new HttpCacheStrategy())
        // Set request retry count
        .setRetryCount(3)
        // Add global request parameters
        //.addParam("token", "6666666")
        // Add global request headers
        //.addHeader("time", "20191030")
        // Enable configuration
        .into();
```

* This is for creating configuration, updating configuration can use

```java
EasyConfig.getInstance()
        .addParam("token", data.getData().getToken());
```

# Usage Documentation

#### Configure API

```java
public final class LoginApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "user/login";
    }

    /** Username */
    private String userName;

    /** Login password */
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

* You can add some annotations to the fields of this class

    * @HttpHeader: Mark this field as a request header parameter

    * @HttpIgnore: Mark this field will not be sent to the backend

    * @HttpRename: Redefine the parameter name or header name sent to the backend

* You can implement some interfaces in this class

    * implements IRequestHost: After implementing this interface, you can re-specify the main host address of this request

    * implements IRequestBodyType: After implementing this interface, you can re-specify the submission method of this request body

    * implements IRequestCacheConfig: After implementing this interface, you can re-specify the cache mode configuration of this request

    * implements IRequestHttpClient: After implementing this interface, you can re-specify the OkHttpClient object used for this request

* The field is the standard for measuring request parameters

    * Assuming the attribute value of a field is empty, this field will not be sent to the backend as a request parameter

    * Assuming a field type is String, its attribute value is an empty string, then this field will be sent as a request parameter, if it is an empty object, it will not

    * Assuming a field type is int, since basic data types do not have empty values, this field will definitely be sent as a request parameter, but it can be replaced with an Integer object to avoid, because the default value of Integer is null

* I'll give you an example: [https://www.baidu.com/api/user/getInfo](https://www.baidu.com/)，then the standard way to write is

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

#### Make a Request

* Need to configure request status and lifecycle handling, specific encapsulation can refer to [BaseActivity](app/src/main/java/com/hjq/easy/demo/BaseActivity.java)

```java
EasyHttp.post(this)
        .api(new LoginApi()
                .setUserName("Android 轮子哥")
                .setPassword("123456"))
        .request(new HttpCallbackProxy<HttpData<LoginBean>>(activity) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<LoginBean> data) {
                toast("Login successful");
            }
        });
```

* This shows the post method, EasyHttp also supports get, head, delete, put, patch requests, which are not demonstrated here

#### Upload Files

```java
public final class UpdateImageApi implements IRequestApi, IRequestBodyType {

    @NonNull
    @Override
    public String getApi() {
        return "upload/";
    }

    @NonNull
    @Override
    public IHttpPostBodyStrategy getBodyType() {
        // File upload needs to use the form method
        return RequestBodyType.FORM;
    }

    /** Local image */
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
                toast("Upload successful");
            }

            @Override
            public void onUpdateFail(@NonNull Throwable throwable) {
                toast("Upload failed");
            }

            @Override
            public void onUpdateEnd(@NonNull IRequestApi api) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
```

* **Note: If the uploaded file is too large or too many, it may cause a timeout, you can re-set the timeout for this request, the timeout is recommended to be based on the file size, please refer to the documentation for specific timeout settings, you can search directly on this page.**

* Of course, in addition to using `File` objects for upload, you can also use `FileContentResolver`, `InputStream`, `RequestBody`, `MultipartBody.Part` objects for upload, if you need to batch upload, please use `List<File>`, `List<FileContentResolver>`, `List<InputStream>`, `List<RequestBody>`, `List<MultipartBody.Part>` objects for batch upload.

#### Download Files

* Download cache strategy: When the md5 of the specified downloaded file or the backend returns md5, the download framework enables the download cache mode by default. If the file already exists in the phone and the md5 verifies the file integrity, the framework will not download it again, but will directly call the download listener. Reduce server pressure and user waiting time.

```java
EasyHttp.download(this)
        .method(HttpMethod.GET)
        .file(new File(Environment.getExternalStorageDirectory(), "微信.apk"))
        //.url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
        .url("http://dldir1.qq.com/weixin/android/weixin708android1540.apk")
        .md5("2E8BDD7686474A7BC4A51ADC3667CABF")
        // Set resumable transfer (default is not enabled)
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
                toast("Download complete: " + file.getPath());
                installApk(XxxActivity.this, file);
            }

            @Override
            public void onDownloadFail(@NonNull File file, @NonNull Throwable throwable) {
                toast("Download failed: " + throwable.getMessage());
                file.delete();
            }

            @Override
            public void onDownloadEnd(@NonNull File file) {
                mProgressBar.setVisibility(View.GONE);
            }

        }).start();
```

#### Scoped Storage Adaptation

* Before Android 10, when reading and writing external storage, we could directly use File objects to upload or download files, but if your project needs the feature of Android 10 scoped storage, then when reading and writing external storage files, we cannot directly use File objects, because `ContentResolver.insert` returns a `Uri` object, at this time, we need to use the `FileContentResolver` object provided by the framework (this object is a subclass of File), the specific usage example is as follows:

```java
File outputFile;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    ContentValues values = new ContentValues();
    .........
    // Generate a new uri path
    Uri outputUri = getContentResolver().insert(MediaStore.Xxx.Media.EXTERNAL_CONTENT_URI, values);
    // Adapt Android 10 scoped storage feature
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

* This is the upload example, the download is the same, here is no repetition.

#### Make Synchronous Requests

* Note: Synchronous requests are time-consuming operations, please ensure that this operation is executed in a sub-thread, do not execute it in the main thread.

```java
PostRequest postRequest = EasyHttp.post(MainActivity.this);
try {
    HttpData<SearchBean> data = postRequest
            .api(new SearchBlogsApi()
                    .setKeyword("搬砖不再有"))
            .execute(new ResponseClass<HttpData<SearchBean>>() {});
    toast("Request successful, please check the logs");
} catch (Throwable throwable) {
    toast(throwable.getMessage());
}
```

#### Set Request Cache

* Need to implement interfaces for reading and writing cache, specific encapsulation can refer to [HttpCacheStrategy](app/src/main/java/com/hjq/easy/demo/http/model/HttpCacheStrategy.java)

* Set cache strategy when initializing the framework

```java
public final class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        EasyConfig.with(okHttpClient)
                ......
                // Set request cache
                .setCacheStrategy(new HttpCacheStrategy())
                ......
                .into();
    }
}
```

* If you are using MMKV as the cache implementation for reading and writing, do not forget to initialize MMKV in the application startup

```java
public final class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        MMKV.initialize(this);
    }
}
```

* First, there are four ways to set the cache mode, all of which are in the `CacheMode` enumeration class

```java
public enum CacheMode {

    /**
     * Default (cache according to Http protocol)
     */
    DEFAULT,

    /**
     * Do not use cache (disable Http protocol cache)
     */
    NO_CACHE,

    /**
     * Only use cache
     *
     * If there is already a cache: read cache -> callback success
     * If there is no cache: request network -> write cache -> callback success
     */
    USE_CACHE_ONLY,

    /**
     * Prioritize cache
     *
     * If there is already a cache: read cache —> callback success —> request network —> refresh cache
     * If there is no cache: request network -> write cache -> callback success
     */
    USE_CACHE_FIRST,

    /**
     * Only read cache when network request fails
     */
    USE_CACHE_AFTER_FAILURE
}
```

* Set cache mode for a specific interface

```java
public final class XxxApi implements IRequestApi, IRequestCacheConfig {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/";
    }

    @NonNull
    @Override
    public CacheMode getCacheMode() {
        // Set to prioritize cache
        return CacheMode.USE_CACHE_FIRST;
    }
}
```

* Set global cache mode

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
        // Only read cache when request fails
        return CacheMode.USE_CACHE_AFTER_FAILURE;
    }
}
```

#### Use with Coroutines

* You can use synchronous requests with coroutines for processing, using the code as follows:

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
            // Refresh UI here
        }
    } catch (throwable: Throwable) {
        toast(throwable.message)
    }
}
```

* If you are not familiar with coroutines, I recommend you read [this article](https://www.jianshu.com/p/2e0746c7d4f3)

# FAQ

#### How to Set Cookies

* EasyHttp is based on OkHttp, and OkHttp itself supports setting cookies, so the usage is the same as OkHttp

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .cookieJar(new XxxCookieJar())
        .build();

EasyConfig.with(okHttpClient)
        .setXxx()
        .into();
```

#### How to Add or Remove Global Parameters

* Add global request parameters

```java
EasyConfig.getInstance().addParam("key", "value");
```

* Remove global request parameters

```java
EasyConfig.getInstance().removeParam("key");
```

* Add global request headers

```java
EasyConfig.getInstance().addHeader("key", "value");
```

* Remove global request headers

```java
EasyConfig.getInstance().removeHeader("key");
```

#### How to Dynamically Add Global Parameters or Headers

```java
EasyConfig.getInstance().setInterceptor(new IRequestInterceptor() {

    @Override
    public void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {
        // Add request header
        headers.put("key", "value");
        // Add parameter
        params.put("key", "value");
    }
});
```

#### How to Ignore a Global Parameter in a Request

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

#### How to Get Server Configuration

```java
IRequestServer server = EasyConfig.getInstance().getServer();
// Get the main host address of the current global server
String host = server.getHost();
```

#### How to Modify API Server Configuration

* First, define a server configuration

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }
}
```

* Then apply it to the global configuration

```java
EasyConfig.getInstance().setServer(new XxxServer());
```

* If you only want to configure a specific interface, you can do this

```java
public final class XxxApi extends XxxServer implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

* If you do not want to define a separate class, you can also write it like this

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

#### How to Configure Multiple Domains

* First, define a test server and official server configuration for a normal interface

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

* Then apply it to the global configuration

```java
IRequestServer server;
if (BuildConfig.DEBUG) {
    server = new TestServer();
} else {
    server = new ReleaseServer();
}
EasyConfig.getInstance().setServer(server);
```

* If you want to set a specific server configuration for an H5 business module, you can do this

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

* You can inherit H5Server when configuring the interface, and other H5 module configurations are similar

```java
public final class UserAgreementApi extends H5Server implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "user/agreement";
    }
}
```

#### How to Change Parameter Submission Method

* Submit parameters in form format (default)

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public IHttpPostBodyStrategy getBodyType() {
        return RequestBodyType.FORM;
    }
}
```

* Submit parameters in Json format

```java
public class XxxServer implements IRequestServer {

    @NonNull
    @Override
    public String getHost() {
        return "https://www.xxxxxxx.com/";
    }

    @NonNull
    @Override
    public IHttpPostBodyStrategy getBodyType() {
        return RequestBodyType.JSON;
    }
}
```

* Of course, you can also configure a specific interface separately

```java
public final class XxxApi implements IRequestApi, IRequestBodyType {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @NonNull
    @Override
    public IHttpPostBodyStrategy getBodyType() {
        return RequestBodyType.JSON;
    }
}
```

* Comparison of advantages and disadvantages of form and Json submission

|   Scenario  |  Form Method  |  Json Method |
| :----: | :------: |  :-----: |
|   Multi-level Parameters  |  Not Supported  |  Supported  |
|   File Upload  |  Supported  |  Not Supported  |

#### How to Encrypt or Decrypt API

* Regarding this issue, it can be implemented using the IRequestInterceptor interface provided by the framework, by overriding the corresponding methods to intercept and modify the content of the object to achieve encryption.

```java
public interface IRequestInterceptor {

    /**
     * Intercept parameters
     *
     * @param httpRequest    Interface object
     * @param params         Request parameters
     * @param headers        Request header parameters
     */
    default void interceptArguments(@NonNull HttpRequest<?> httpRequest, @NonNull HttpParams params, @NonNull HttpHeaders headers) {}

    /**
     * Intercept request header
     *
     * @param httpRequest    Interface object
     * @param request        Request header object
     * @return               Return new request header
     */
    @NonNull
    default Request interceptRequest(@NonNull HttpRequest<?> httpRequest, @NonNull Request request) {
        return request;
    }

    /**
     * Interceptor response header
     *
     * @param httpRequest    Interface object
     * @param response       Response header object
     * @return               Return new response header
     */
    @NonNull
    default Response interceptResponse(@NonNull HttpRequest<?> httpRequest, @NonNull Response response) {
        return response;
    }
}
```

```java
// Set interceptor when initializing the framework
EasyConfig.with(okHttpClient)
        // Set request parameter interceptor
        .setInterceptor(new XxxInterceptor())
        .into();
```

* If you only want to encrypt or decrypt a specific interface, you can let the Api class implement the IRequestInterceptor interface separately, so it will not follow the global configuration.

#### How to Ignore a Parameter

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

#### How to Pass Request Headers

* Add the `@HttpHeader` annotation to the field, then it means this field is a request header, if no annotation is added, the framework will default to using the field as a request parameter

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

#### How to Pass Dynamic Request Headers

* Pass a `HashMap` type field, and add the `@HTTPHeader` annotation to it

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

#### How to Rename Parameter or Header Name

* Add the `@HttpRename` annotation to the field, then you can modify the value of the parameter name, if no annotation is added, the framework will default to using the field name as the parameter name

```java
public final class XxxApi implements IRequestApi {

    @NonNull
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }

    @HttpRename("k")
    private String keyword;
}
```

#### How to Upload Files

* Upload using File object

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

* Upload using InputStream object

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

* Upload using RequestBody object

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

#### How to Upload a List of Files

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

#### How to Set Timeout Retry

```java
// Set request retry count
EasyConfig.getInstance().setRetryCount(3);
// Set request retry time
EasyConfig.getInstance().setRetryTime(1000);
```

#### How to Set Request Timeout

* Global configuration (applies to all interfaces)

```java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
builder.readTimeout(5000, TimeUnit.MILLISECONDS);
builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
builder.connectTimeout(5000, TimeUnit.MILLISECONDS);

EasyConfig.with(builder.build())
        .into();
```

* Local configuration (only applies to a specific interface)

```java
public final class XxxApi implements IRequestApi, IRequestHttpClient {

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

#### How to Disable Log Printing

```java
EasyConfig.getInstance().setLogEnabled(false);
```

#### How to Change Log Printing Strategy

* You can first define a class that implements [IHttpLogStrategy](library/src/main/java/com/hjq/http/config/IHttpLogStrategy.java) interface, then pass it in when initializing the framework

```java
EasyConfig.with(okHttpClient)
        .......
        // Set custom log printing strategy
        .setLogStrategy(new XxxStrategy())
        .into();
```

* Scenarios requiring modification of log printing strategy

    * Need to write request logs to local

    * Need to modify the format of printed request logs

#### How to Cancel an Ongoing Request

```java
// Cancel request task based on TAG
EasyHttp.cancelByTag(Object tag);
// Cancel request with specified Tag
EasyHttp.cancelByTag(Object tag);
// Cancel all requests
EasyHttp.cancelAll();
```

#### How to Delay a Request

```java
EasyHttp.post(MainActivity.this)
        .api(new XxxApi())
        // Request after 5 seconds
        .delay(5000)
        .request(new HttpCallbackProxy<HttpData<XxxBean>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<XxxBean> result) {

            }
        });
```

#### How to Dynamically Concatenate API Path

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

#### How to Dynamically Set the Entire Request URL

```java
EasyHttp.post(this)
        .api(new RequestUrl("https://xxxx.com/aaaa"))
        .request(new HttpCallbackProxy<Xxx>(this) {

            @Override
            public void onHttpSuccess(@NonNull Xxx result) {

            }
        });
```

#### How to Trust All Certificates for HTTPS

* Set this when initializing OkHttp

```java
HttpSslConfig sslConfig = HttpSslFactory.generateSslConfig();
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .sslSocketFactory(sslConfig.getsSLSocketFactory(), sslConfig.getTrustManager())
        .hostnameVerifier(HttpSslFactory.generateUnSafeHostnameVerifier())
        .build();
```
* However, this is not recommended, as this is insecure, meaning that no Https verification will be used for each request.

* Of course, the framework also provides some API for generating certificates, please refer to the classes under the com.hjq.http.ssl package.

#### What if I Don't Want to Write a Class for Each API

* First, define a URL management class, and configure the URL in this class

```java
public final class HttpUrls {

    /** Get user info */
    public static final String GET_USER_INFO =  "user/getUserInfo";
}
```

* Then introduce the interface path into EasyHttp

```java
EasyHttp.post(this)
        .api(HttpUrls.GET_USER_INFO)
        .request(new HttpCallbackProxy<HttpData<XxxBean>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<XxxBean> result) {

            }
        });
```

* However, this method can only be applied to interfaces without parameters, interfaces with parameters still need to write a class, because the framework only parses parameters in the Api class.

* Although EasyHttp opens this writing method, as the author, I do not recommend you to write it this way, because this writing method will result in poor extensibility, for example, subsequent addition of parameters, you still need to change it back, and cannot dynamically configure the interface.

#### What if the Framework Only Accepts LifecycleOwner

* Among them, `androidx.appcompat.app.AppCompatActivity` and `androidx.fragment.app.Fragment` are subclasses of LifecycleOwner, this is unquestionable, you can directly pass it to the framework as LifecycleOwner

* However, if you pass in an `android.app.Activity` object, which is not an `androidx.appcompat.app.AppCompatActivity` object, then you can write it like this

```java
EasyHttp.post(new ActivityLifecycle(this))
        .api(new XxxApi())
        .request(new HttpCallbackProxy<HttpData<XxxBean>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<XxxBean> result) {

            }
        });
```

* If you pass in an `android.app.Fragment` object, which is not an `androidx.fragment.app.Fragment` object, please directly inherit the LifecycleAppFragment class in the framework, or encapsulate a Fragment base class with Lifecycle in your project

* If you want to use EasyHttp in an `android.app.Service`, please directly inherit the LifecycleService class in the framework, or encapsulate a Service base class with Lifecycle in your project

* If none of the above conditions are met, but you still want to request the network in a certain place, then you can write it like this

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

* Note: Passing ApplicationLifecycle means that the framework cannot automatically control the lifecycle of the request, if you write it in Application, it is completely fine, but you cannot write it in Activity or Service, because this may lead to memory leaks.

* In addition to Application, if you use the writing method of ApplicationLifecycle in Activity or Service, in order to avoid memory leaks or crashes, you need to set the corresponding Tag when requesting, and manually cancel the request at the appropriate time (usually when Activity or Service is destroyed or exited).

```java
EasyHttp.cancelByTag("abc");
```

#### How to Use EasyHttp in ViewModel

* Step 1: Encapsulate a BaseViewModel, and incorporate the LifecycleOwner feature

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

* Step 2: Let the business ViewModel class inherit BaseViewModel, specific example as follows

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

#### How to Cancel the Loading Dialog When Cancelling a Request

* First, this loading dialog is not built-in by the framework, it can be modified or cancelled, there are two ways to choose from

* First way: Override HttpCallbackProxy callback methods

```java
EasyHttp.post(this)
        .api(new XxxApi())
        .request(new HttpCallbackProxy<Xxx>(this) {

            @Override
            public void onHttpStart(@NonNull IRequestApi api) {
                // Override method and comment out parent call
                //super.onHttpStart(call);
            }

            @Override
            public void onHttpEnd(@NonNull IRequestApi api) {
                // Override method and comment out parent call
                //super.onHttpEnd(call);
            }
        });
```

* Second way: Directly implement OnHttpListener interface

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

#### How to Upload a JSON Array as a Parameter

* Since the Api class will eventually be converted into a JsonObject string, if you need to upload a JsonArray string, please implement it in the following way

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

* However, I personally do not recommend using JsonArray as the root type of parameters, because the extensibility of such interfaces is extremely poor.

#### What if the Key of API Parameter is Dynamic

* The framework parses the fields in the Api class to be parameters through reflection, the field name as the parameter Key, the field value as the parameter Value, since Java cannot dynamically change the field name, so it cannot be modified through normal means, if you have this requirement, please implement it through the following means

```java
HashMap<String, Object> parameter = new HashMap<>();

// Add global parameters
HashMap<String, Object> globalParams = EasyConfig.getInstance().getParams();
Set<String> keySet = globalParams.keySet();
for (String key : keySet) {
    parameter.put(key, globalParams.get(key));
}

// Add custom parameters
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

#### How to Set a Custom UA Identifier

* First, UA stands for User Agent, when we do not set a custom UA identifier, then OkHttp will add a default UA identifier in the BridgeInterceptor, then how to set a custom UA identifier in EasyHttp? It's actually very simple, UA identifier is essentially a request header, add a request header with the name `User-Agent` to EasyHttp, and then how to add a request header, the previous documentation has already been introduced, here is no repetition.

#### How to Change the Thread for Request Callback

```java
EasyHttp.post(this)
        .api(new XxxApi())
        // Indicates that the callback is performed in a sub-thread
        .schedulers(ThreadSchedulers.IO)
        .request(new HttpCallbackProxy<HttpData<Xxx>>(this) {

            @Override
            public void onHttpSuccess(@NonNull HttpData<Xxx> result) {

            }
        });
```

#### How to Use a Custom RequestBody for Requests

* In some extreme cases, the framework cannot meet the needs, at which point a custom `RequestBody` is needed, then how to use a custom `RequestBody`? The framework actually has an open method, the specific usage example is as follows:

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

* Note: Since Post requests place parameters on the `RequestBody`, and a request can only set one `RequestBody`, if you set a custom `body(RequestBody body)`, then the framework will not parse the fields in the `XxxApi` class into parameters. In addition to Post requests, Put requests and Patch requests can also use this method to set, here is no repetition.

#### How to Customize ContentType in Request Header

* The specific writing example is as follows:

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

* Note: This function was added in framework **11.5** version, previous versions did not have this function.

#### How to Customize Key and Value in Get Request Parameters

* First, define a custom Api class, then use the `getApi` method to dynamically concatenate the parameters

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

* The outer layer can call it in the following way

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

* Note: This implementation method is only applicable in cases where the framework's design cannot meet the requirements, and the author does not recommend using this method, because this method is not convenient to manage the key of request parameters, it is recommended to use the method of defining fields on the class to achieve.

#### How to Define Get-like Parameters in Post Request

* Directly concatenate request parameters to the url, and ignore the value of a field (to avoid being parsed as Post parameters)

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

* Ps: In general, I do not recommend writing it this way, this request looks awkward, it looks like a Get request, but in fact it is a Post request.

# Use with RxJava

#### Preparation

* Add remote dependency

```groovy
dependencies {
    // RxJava: https://github.com/ReactiveX/RxJava
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
    implementation 'io.reactivex.rxjava2:rxjava:2.2.12'
}
```

* Note: RxJava needs to handle lifecycle yourself to avoid memory leaks

#### Multiple Requests in Series

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
// Let the observer execute in IO thread
.subscribeOn(Schedulers.io())
// Let the observer execute in the main thread
.observeOn(AndroidSchedulers.mainThread())
.subscribe(new Consumer<HttpData<SearchBean>>() {

    @Override
    public void accept(HttpData<SearchBean> data) throws Exception {
        Log.i("EasyHttp", "Final result: " + data.getMessage());
    }

}, new Consumer<Throwable>() {

    @Override
    public void accept(Throwable throwable) throws Exception {
        toast(throwable.getMessage());
    }
});
```

#### Polling Requests

* If the polling times are limited, you can consider using Http requests to implement it, but if the polling times are infinite, then it is not recommended to use Http requests to implement it, it should be done using WebSocket, or other long-link protocols.

```java
// Initiate polling request, initiate three requests, the first request triggers after 5 seconds, the remaining two trigger after 1 second and 2 seconds
Observable.intervalRange(1, 3, 5000, 1000, TimeUnit.MILLISECONDS)
        // Let the observer execute in IO thread
        .subscribeOn(Schedulers.io())
        // Let the observer execute in the main thread
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

#### Wrap Returned Data

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
// Let the observer execute in IO thread
.subscribeOn(Schedulers.io())
// Let the observer execute in the main thread
.observeOn(AndroidSchedulers.mainThread())
.subscribe(new Consumer<String>() {

    @Override
    public void accept(String s) throws Exception {
        Log.i("EasyHttp", "Current page position: " + s);
    }

}, new Consumer<Throwable>() {

    @Override
    public void accept(Throwable throwable) throws Exception {
        toast(throwable.getMessage());
    }
});
```

# Support Protobuf

#### Preparation

* Add the following configuration to the `build.gradle` file in the project root directory

```groovy
buildscript {

    ......

    dependencies {
        // Auto-generate Protobuf class plugin: https://github.com/google/protobuf-gradle-plugin
        classpath 'com.google.protobuf:protobuf-gradle-plugin:0.9.3'
    }
}
```

* Add remote dependency in the `build.gradle` file of the app module

```groovy
......

apply plugin: 'com.google.protobuf'

android {
    ......
    
    sourceSets {
        main {
            proto {
                // Specify Protobuf file path
                srcDir 'src/main/proto'
            }
        }
    }
}

protobuf {

    protoc {
        // You can also configure the local compiler path
        artifact = 'com.google.protobuf:protoc:3.23.0'
    }

    generateProtoTasks {
        all().each { task ->
            task.builtins {
                remove java
            }
            task.builtins {
                // Produce java source code
                java {}
            }
        }
    }
}

dependencies {

    ......

    // Protobuf: https://github.com/protocolbuffers/protobuf
    implementation 'com.google.protobuf:protobuf-java:3.23.1'
    implementation 'com.google.protobuf:protoc:3.23.0'
}
```

* Create a folder named `proto` under `app/src/main/`, used to store Protobuf related files, then create a file `Person.proto`, the specific content is as follows:

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

* Then `Rebuild Project`, you can see the plugin automatically generate the `PersonOuterClass` class, `PersonOuterClass` class also has a static inner class named `Person`

#### Parse Request Body as Protobuf

* Create a custom RequestBody class to parse Protocol objects into streams, it is recommended to be placed in the `com.xxx.xxx/http/model` package,

```java
public class ProtocolRequestBody extends RequestBody {

    /** MessageLite object */
    private final MessageLite mMessageLite;
    /** Byte array */
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
        // Note: This needs to be calculated using the length of the byte array
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
     * Get MessageLite object
     */
    @NonNull
    public MessageLite getMessageLite() {
        return mMessageLite;
    }
}
```

* Example of initiating a request

```java
// Pretend to generate a Protobuf object
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

#### Parse Response Body as Protobuf

* This support is very simple, just modify the implementation of the `IRequestHandler` interface, the specific code implementation is as follows:

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
                // Call static method
                result = parseFromMethod.invoke(null, (Object) text.getBytes());
            } else {
                result = GsonFactory.getSingletonGson().fromJson(text, type);
            }
        } catch (JsonSyntaxException e) {
            // Return result read exception
            throw new DataException(mApplication.getString(R.string.http_data_explain_error), e);
        }

        ......
        return result;
    }
}
```