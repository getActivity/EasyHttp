#### 如何添加全局参数？

```
// 添加全局请求参数
EasyConfig.getInstance().addHeader("token", "abc");
// 添加全局请求头
EasyConfig.getInstance().addParam("token", "abc");
```

#### 如何在请求中忽略某个全局参数？

```
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

```
IRequestServer server = EasyConfig.getInstance().getServer();
// 获取当前全局的服务器主机地址
String host = server.getHost();
// 获取当前全局的服务器路径地址
String path = server.getPath();
```

#### 如何修改服务器配置？

* 先定义一个服务器配置

```
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

```
EasyConfig.getInstance().setServer(new XxxServer());
```

* 如果只是针对某个接口可以这样配置

```
public final class XxxApi extends XxxServer implements IRequestApi {

    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
}
```

* 如果不想单独定义一个类，也可以这样写

```
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

```
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

```
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

* 当然也支持对某个接口进行单独配置，和上个问题雷同，这里略过

* 表单和 Json 方式提交的优缺点对比

|  场景  | 表单方式  | Json 方式 |
| :----: | :------: |  :-----: |
|    参数嵌套   | 不支持 | 支持 |
|    文件上传   | 支持 | 不支持 |

#### 如何忽略某个参数？

```
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

```
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

```
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

```
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private File file;
}
```

* 使用 InputStream 对象上传

```
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private InputStream inputStream;
}
```

* 使用 RequestBody 对象上传

```
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private RequestBody requestBody;
}
```

#### 如何上传文件列表？

```
public final class XxxApi implements IRequestApi {
    
    @Override
    public String getApi() {
        return "xxx/xxxx";
    }
    
    private List<File> files;
}
```

#### 如何设置超时重试？

```
EasyConfig.getInstance().setRetryCount(3);
```

#### 如何设置不打印日志？

```
EasyConfig.getInstance().setLogEnabled(false);
```