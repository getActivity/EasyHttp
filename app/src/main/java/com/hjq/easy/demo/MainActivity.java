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
import androidx.annotation.NonNull;
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
import com.hjq.http.exception.FileMd5Exception;
import com.hjq.http.listener.HttpCallbackProxy;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.FileContentResolver;
import com.hjq.http.model.HttpMethod;
import com.hjq.http.model.ResponseClass;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.Toaster;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {}

    @Override
    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
        if (doNotAskAgain) {
            Toaster.show("授权失败，请手动授予存储权限");
            XXPermissions.startPermissionActivity(this, permissions);
        } else {
            Toaster.show("请先授予存储权限");
            requestPermission();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (XXPermissions.isGranted(this, Permission.Group.STORAGE)) {
            onGranted(new ArrayList<>(), true);
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
                    .request(new HttpCallbackProxy<HttpData<List<SearchAuthorApi.Bean>>>(this) {

                        @Override
                        public void onHttpSuccess(HttpData<List<SearchAuthorApi.Bean>> result) {
                            Toaster.show("Get 请求成功，请看日志");
                        }
                    });

        } else if (viewId == R.id.btn_main_post) {

            EasyHttp.post(this)
                    .api(new SearchBlogsApi()
                            .setKeyword("搬砖不再有"))
                    .request(new HttpCallbackProxy<HttpData<SearchBlogsApi.Bean>>(MainActivity.this) {

                        @Override
                        public void onHttpSuccess(HttpData<SearchBlogsApi.Bean> result) {
                            Toaster.show("Post 请求成功，请看日志");
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
                    Toaster.show("同步请求成功，请看日志");
                } catch (Throwable throwable) {
                    Toaster.show(throwable.getMessage());
                }
                runOnUiThread(this::hideDialog);
            }).start();

        } else if (viewId == R.id.btn_main_update) {

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                Toaster.show("当前正在上传或者下载，请等待完成之后再进行操作");
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
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "我是测试专用的图片.png");

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
                        public void onUpdateStart(Call call) {
                            mProgressBar.setProgress(0);
                            mProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onUpdateProgressChange(int progress) {
                            mProgressBar.setProgress(progress);
                        }

                        @Override
                        public void onUpdateSuccess(Void result) {
                            Toaster.show("上传成功");
                        }

                        @Override
                        public void onUpdateFail(Throwable throwable) {
                            Toaster.show("上传失败");
                        }

                        @Override
                        public void onUpdateEnd(Call call) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });

        } else if (viewId == R.id.btn_main_download) {

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                Toaster.show("当前正在上传或者下载，请等待完成之后再进行操作");
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
                        public void onDownloadStart(File file) {
                            mProgressBar.setProgress(0);
                            mProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onDownloadProgressChange(File file, int progress) {
                            mProgressBar.setProgress(progress);
                        }

                        @Override
                        public void onDownloadSuccess(File file) {
                            Toaster.show("下载成功：" + file.getPath());
                            installApk(MainActivity.this, file);
                        }

                        @Override
                        public void onDownloadFail(File file, Throwable throwable) {
                            Toaster.show("下载失败：" + throwable.getMessage());
                            if (throwable instanceof FileMd5Exception) {
                                // 如果是文件 md5 校验失败，则删除文件
                                file.delete();
                            }
                        }

                        @Override
                        public void onDownloadEnd(File file) {
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
                .permission(Permission.REQUEST_INSTALL_PACKAGES)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            return;
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (file instanceof FileContentResolver) {
                                uri = ((FileContentResolver) file).getContentUri();
                            } else {
                                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                            }
                        } else {
                            uri = Uri.fromFile(file);
                        }

                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // 对目标应用临时授权该 Uri 读写权限
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        Toaster.show("安装 apk 失败，请正确授予安装权限");
                    }
                });
    }
}