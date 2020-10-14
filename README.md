# 网络请求框架

> 码云地址：[Gitee](https://gitee.com/getActivity/EasyHttp)

> [点击此处下载Demo](EasyHttp.apk)

![](EasyHttp.jpg)

#### Gradle 集成

```groovy
android {
    // 支持 JDK 1.8
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'com.hjq:http:8.6'
    implementation 'com.squareup.okhttp3:okhttp:3.12.12'
    implementation 'com.google.code.gson:gson:2.8.5'
}
```
            
#### 具体用法[请点击这里查看](HelpDoc.md)
    
#### 不同网络请求框架之间的对比

|  功能  | [EasyHttp](https://github.com/getActivity/EasyHttp) | [Retrofit](https://github.com/square/retrofit) | [OkGo](https://github.com/jeasonlzy/okhttp-OkGo) |
| :----: | :------: |  :-----: |  :-----: |
|    动态 Host  |  支持  |  不支持  |   支持   |
|    全局参数   |  支持  |  不支持  |    支持   |
|    超时重试   |  支持  |  不支持  |    支持   |
|    极速下载   |  支持  |  不支持  |   不支持  |
|    下载校验   |  支持  |  不支持  |   不支持  |
|    注解数量   |  3 个  |  25 个  |   0 个  |
|    上传文件类型   | File / InputStream | RequestBody |  File  |
|    批量上传文件   |  支持  |   不支持   |    支持    |
|    上传进度监听   |  支持  |   不支持   |    支持    |
|    Json 参数提交  |  支持  |    支持   |   支持   |
|    请求生命周期  | 自动管控 |   需要封装  |   需要封装  |
|    参数传值方式  |  字段名 + 字段值  | 方法参数名 + 方法参数值 |  定义 key 和 value  |
|    参数灵活性  | 不强制传入 | 强制全部传入 |   不强制传入 |
|   框架维护状态 |  维护中  |   维护中   |   停止维护  |

* Retrofit 在我看来并不是那么好用，因为很多常用的功能实现起来比较麻烦，动态 Host 要写拦截器，日志打印要写拦截器，就连最常用的添加全局参数也要写拦截器，一个拦截器意味着要写很多代码，如果写得不够严谨还有可能出现 Bug，从而影响整个 OkHttp 请求流程，我经常在想这些功能能不能都用一句代码搞定，因为我觉得这些功能是框架设计的时候应该考虑的，这便是我做这个框架的初心。

* 本框架采用了 OOP 思想，一个请求代表一个对象，通过类的继承和实现的特性来实现接口的动态化，几乎涵盖接口开发中所有的功能，使用起来非常简单灵活。

* 有很多人觉得写一个接口类很麻烦，这个点确实有点麻烦，但是这块的付出是有收获的，从前期开发的效率考虑：OkGo > EasyHttp > Retrofit，但是从后期维护的效率考虑：EasyHttp > Retrofit > OkGo，之所以比较这三个框架，是因为框架的设计思想不同，但是我始终认为 EasyHttp 才是最好的设计，所以我创造了它。

* 前期开发和后期维护哪个更重要？我觉得都重要，但是如果两者之间有利益冲突，我会毫不犹豫选择后期维护，因为前期开发占据的是小头，后期的持续维护才是大头。

#### 混淆规则

```groovy
# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# 不混淆这个包下的字段名
-keepclassmembernames class com.hjq.http.demo.http.** {
    <fields>;
}
```

#### 作者的其他开源项目

* 安卓架构：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 日志框架：[Logcat](https://github.com/getActivity/Logcat)

* 吐司框架：[ToastUtils](https://github.com/getActivity/ToastUtils)

* 权限框架：[XXPermissions](https://github.com/getActivity/XXPermissions)

* 标题栏框架：[TitleBar](https://github.com/getActivity/TitleBar)

* 国际化框架：[MultiLanguages](https://github.com/getActivity/MultiLanguages)

* 悬浮窗框架：[XToast](https://github.com/getActivity/XToast)

#### Android技术讨论Q群：78797078

#### 如果您觉得我的开源库帮你节省了大量的开发时间，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png) ![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

#### [点击查看捐赠列表](https://github.com/getActivity/Donate)

#### 特别感谢

[张鸿洋](https://github.com/hongyangAndroid)

[WanAndroid](https://www.wanandroid.com/)

## License

```text
Copyright 2019 Huang JinQun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.