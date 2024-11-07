# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK\Studio\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# EasyHttp
-keep class com.hjq.http.** {*;}
# 必须要加上此规则，否则会导致泛型解析失败
-keep class * implements com.hjq.http.listener.OnHttpListener {
    *;
}

-keep class * extends com.hjq.http.model.ResponseClass {
    *;
}

# 不混淆这个包下的类
-keep class com.hjq.easy.demo.http.** {
    <fields>;
}