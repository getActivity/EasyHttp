package com.hjq.easy.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.easy.demo.http.api.SearchAuthorApi;
import com.hjq.easy.demo.http.api.SearchBlogsApi;
import com.hjq.easy.demo.http.api.UpdateImageApi;
import com.hjq.easy.demo.http.model.HttpData;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyUtils;
import com.hjq.http.listener.HttpCallback;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.FileContentResolver;
import com.hjq.http.model.HttpMethod;
import com.hjq.http.model.ResponseClass;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import okhttp3.Call;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求示例
 */
public final class MainActivity extends BaseActivity implements View.OnClickListener, OnPermissionCallback {

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

        TitleBar titleBar = findViewById(R.id.tb_main_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onTitleClick(TitleBar titleBar) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(titleBar.getTitle().toString()));
                startActivity(intent);
            }
        });

        requestPermission();
    }

    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
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
        if (XXPermissions.isGranted(this, Permission.Group.STORAGE)) {
            onGranted(null, true);
        } else {
            requestPermission();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_main_get) {

            EasyHttp.get(this)
                    .api(new SearchAuthorApi()
                            .setId(190000))
                    .request(new HttpCallback<HttpData<List<SearchAuthorApi.Bean>>>(this) {

                        @Override
                        public void onSucceed(HttpData<List<SearchAuthorApi.Bean>> result) {
                            ToastUtils.show("Get 请求成功，请看日志");
                        }
                    });

        } else if (viewId == R.id.btn_main_post) {

            EasyHttp.post(this)
                    .api(new SearchBlogsApi()
                            .setKeyword("搬砖不再有"))
                    .request(new HttpCallback<HttpData<SearchBlogsApi.Bean>>(this) {

                        @Override
                        public void onSucceed(HttpData<SearchBlogsApi.Bean> result) {
                            ToastUtils.show("Post 请求成功，请看日志");
                        }
                    });

        } else if (viewId == R.id.btn_main_exec) {

            // 在主线程中不能做耗时操作
            new Thread(() -> {
                runOnUiThread(this::showDialog);
                try {
                    HttpData<SearchBlogsApi.Bean> data = EasyHttp.post(MainActivity.this)
                            .api(new SearchBlogsApi()
                                    .setKeyword("搬砖不再有"))
                            .execute(new ResponseClass<HttpData<SearchBlogsApi.Bean>>() {});
                    ToastUtils.show("同步请求成功，请看日志");
                } catch (Exception e) {
                    ToastUtils.show(e.getMessage());
                }
                runOnUiThread(this::hideDialog);
            }).start();

        } else if (viewId == R.id.btn_main_update) {

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                ToastUtils.show("当前正在上传或者下载，请等待完成之后再进行操作");
                return;
            }

            // 如果是放到外部存储目录下则需要适配分区存储
//            String fileName = "EasyHttp.png";
//            File file;
//            Uri outputUri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // 适配 Android 10 分区存储特性
//                ContentValues values = new ContentValues();
//                // 设置显示的文件名
//                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
//                // 生成一个新的 uri 路径
//                outputUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                file = new FileContentResolver(getContentResolver(), outputUri, fileName);
//            } else {
//                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
//            }

            // 如果是放到外部存储的应用专属目录则不需要适配分区存储特性
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "EasyHttp.png");

            if (!file.exists()) {
                // 生成图片到本地
                try {
                    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_material);
                    OutputStream outputStream = EasyUtils.openFileOutputStream(file);
                    if (((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        outputStream.flush();
                    }
                    // 通知系统多媒体扫描该文件，否则会导致拍摄出来的图片或者视频没有及时显示到相册中，而需要通过重启手机才能看到
                    MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, null, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            EasyHttp.post(this)
                    .api(new UpdateImageApi(file))
                    .request(new OnUpdateListener<Void>() {

                        @Override
                        public void onStart(Call call) {
                            mProgressBar.setProgress(0);
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

        } else if (viewId == R.id.btn_main_download) {

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                ToastUtils.show("当前正在上传或者下载，请等待完成之后再进行操作");
                return;
            }

            // 如果是放到外部存储目录下则需要适配分区存储
//            String fileName = "微信 8.0.15.apk";
//
//            File file;
//            Uri outputUri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // 适配 Android 10 分区存储特性
//                ContentValues values = new ContentValues();
//                // 设置显示的文件名
//                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
//                // 生成一个新的 uri 路径
//                outputUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
//                file = new FileContentResolver(getContentResolver(), outputUri, fileName);
//            } else {
//                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
//            }

            // 如果是放到外部存储的应用专属目录则不需要适配分区存储特性
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "微信 8.0.15.apk");

            EasyHttp.download(this)
                    .method(HttpMethod.GET)
                    .file(file)
                    //.url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
                    .url("https://dldir1.qq.com/weixin/android/weixin8015android2020_arm64.apk")
                    .md5("b05b25d4738ea31091dd9f80f4416469")
                    .listener(new OnDownloadListener() {

                        @Override
                        public void onStart(File file) {
                            mProgressBar.setProgress(0);
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
                    })
                    .start();
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
                                if (file instanceof FileContentResolver) {
                                    uri = ((FileContentResolver) file).getContentUri();
                                } else {
                                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                                }
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
}