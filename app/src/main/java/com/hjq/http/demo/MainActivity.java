package com.hjq.http.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.demo.http.model.HttpData;
import com.hjq.http.demo.http.request.SearchAuthorApi;
import com.hjq.http.demo.http.request.SearchBlogsApi;
import com.hjq.http.demo.http.request.UpdateImageApi;
import com.hjq.http.demo.http.response.SearchBean;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.DownloadInfo;
import com.hjq.http.model.HttpMethod;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求示例
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, OnPermission {

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.pb_main_progress);

        findViewById(R.id.btn_main_get).setOnClickListener(this);
        findViewById(R.id.btn_main_post).setOnClickListener(this);
        findViewById(R.id.btn_main_update).setOnClickListener(this);
        findViewById(R.id.btn_main_download).setOnClickListener(this);
        requestPermission();
    }

    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .request(this);
    }

    /**
     * {@link OnPermission}
     */

    @Override
    public void hasPermission(List<String> granted, boolean all) {

    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {
        if (quick) {
            ToastUtils.show("授权失败，请手动授予权限");
            XXPermissions.startPermissionActivity(this, true);
        } else {
            ToastUtils.show("请先授予权限");
            requestPermission();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (XXPermissions.hasPermission(this, Permission.Group.STORAGE)) {
            hasPermission(null, true);
        } else {
            requestPermission();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_get:
                EasyHttp.get(this)
                        .api(new SearchAuthorApi()
                                .setAuthor("鸿洋"))
                        .request(new HttpCallback<HttpData<SearchBean>>(this) {

                            @Override
                            public void onSucceed(HttpData<SearchBean> result) {
                                ToastUtils.show("请求成功，请看日志");
                            }
                        });
                break;
            case R.id.btn_main_post:
                EasyHttp.post(this)
                        .api(new SearchBlogsApi()
                                .setKeyword("搬砖不再有"))
                        .request(new HttpCallback<HttpData<SearchBean>>(this) {

                            @Override
                            public void onSucceed(HttpData<SearchBean> result) {
                                ToastUtils.show("请求成功，请看日志");
                            }
                        });
                break;
            case R.id.btn_main_update:
                File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + ".png");
                // 生成图片到本地
                drawableToFile(ContextCompat.getDrawable(this, R.mipmap.ic_launcher), file);

                EasyHttp.post(this)
                        .api(new UpdateImageApi(file))
                        .request(new HttpCallback<String>(this) {

                            @Override
                            public void onSucceed(String result) {
                                ToastUtils.show("上传成功");
                            }
                        });
                break;
            case R.id.btn_main_download:
                EasyHttp.download(this)
                        .method(HttpMethod.GET)
                        .file(new File(Environment.getExternalStorageDirectory(), "微信.apk"))
                        //.url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
                        .url("http://dldir1.qq.com/weixin/android/weixin708android1540.apk")
                        .md5("2E8BDD7686474A7BC4A51ADC3667CABF")
                        .listener(new OnDownloadListener() {

                            @Override
                            public void onStart(Call call) {
                                mProgressBar.setVisibility(View.VISIBLE);
                                ToastUtils.show("下载开始");
                            }

                            @Override
                            public void onProgress(DownloadInfo info) {
                                mProgressBar.setProgress(info.getDownloadProgress());
                            }

                            @Override
                            public void onComplete(DownloadInfo info) {
                                ToastUtils.show("下载完成：" + info.getFile().getPath());
                                installApk(MainActivity.this, info.getFile());
                            }

                            @Override
                            public void onError(DownloadInfo info, Exception e) {
                                ToastUtils.show("下载出错：" + e.getMessage());
                            }

                            @Override
                            public void onEnd(Call call) {
                                mProgressBar.setVisibility(View.GONE);
                                ToastUtils.show("下载结束");
                            }

                        }).start();
                break;
            default:
                break;
        }
    }

    /**
     * 安装 Apk
     */
    private void installApk(final Context context, final File file) {
        XXPermissions.with(MainActivity.this)
                // 安装包权限
                .permission(Permission.REQUEST_INSTALL_PACKAGES)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            } else {
                                uri = Uri.fromFile(file);
                            }

                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
    }

    /**
     * 将 Drawable 写入到文件中
     */
    private void drawableToFile(Drawable drawable, File file) {
        if (drawable == null) {
            return;
        }

        try {
            if (file.exists()) {
                file.delete();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream out;
            out = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (IOException e) {
            EasyLog.print(e);
        }
    }
}