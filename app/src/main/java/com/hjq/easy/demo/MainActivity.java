package com.hjq.easy.demo;

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

import com.hjq.easy.demo.http.model.HttpData;
import com.hjq.easy.demo.http.request.SearchAuthorApi;
import com.hjq.easy.demo.http.request.SearchBlogsApi;
import com.hjq.easy.demo.http.request.UpdateImageApi;
import com.hjq.easy.demo.http.response.SearchBean;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.HttpMethod;
import com.hjq.http.model.ResponseClass;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求示例
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, OnPermissionCallback {

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.pb_main_progress);

        findViewById(R.id.btn_main_get).setOnClickListener(this);
        findViewById(R.id.btn_main_post).setOnClickListener(this);
        findViewById(R.id.btn_main_exec).setOnClickListener(this);
        findViewById(R.id.btn_main_update).setOnClickListener(this);
        findViewById(R.id.btn_main_download).setOnClickListener(this);
        requestPermission();
    }

    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(this);
    }

    /**
     * {@link OnPermissionCallback}
     */

    @Override
    public void onGranted(List<String> permissions, boolean all) {

    }

    @Override
    public void onDenied(List<String> permissions, boolean never) {
        if (never) {
            ToastUtils.show("授权失败，请手动授予存储权限");
            XXPermissions.startPermissionActivity(this, permissions);
        } else {
            ToastUtils.show("请先授予存储权限");
            requestPermission();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (XXPermissions.isGrantedPermission(this, Permission.MANAGE_EXTERNAL_STORAGE)) {
            onGranted(null, true);
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
                                ToastUtils.show("Get 请求成功，请看日志");
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
                                ToastUtils.show("Post 请求成功，请看日志");
                            }
                        });
                break;
            case R.id.btn_main_exec:
                // 在主线程中不能做耗时操作
                new Thread(() -> {
                    runOnUiThread(this::showDialog);
                    try {
                        HttpData<SearchBean> data = EasyHttp.post(MainActivity.this)
                                .api(new SearchBlogsApi()
                                        .setKeyword("搬砖不再有"))
                                .execute(new ResponseClass<HttpData<SearchBean>>() {});
                        ToastUtils.show("同步请求成功，请看日志");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show(e.getMessage());
                    }
                    runOnUiThread(this::hideDialog);
                }).start();
                break;
            case R.id.btn_main_update:
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    ToastUtils.show("当前正在上传或者下载，请等待完成之后再进行操作");
                    return;
                }

                File file = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + ".png");
                // 生成图片到本地
                drawableToFile(ContextCompat.getDrawable(this, R.drawable.bg_material), file);

                EasyHttp.post(this)
                        .api(new UpdateImageApi(file))
                        .request(new OnUpdateListener<Void>() {

                            @Override
                            public void onStart(Call call) {
                                mProgressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onProgress(int progress) {
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
                break;
            case R.id.btn_main_download:
                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    ToastUtils.show("当前正在上传或者下载，请等待完成之后再进行操作");
                    return;
                }
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
                            public void onProgress(File file, int progress) {
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
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
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
                    public void onDenied(List<String> permissions, boolean never) {

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
            e.printStackTrace();
        }
    }
}