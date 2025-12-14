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
import com.hjq.http.config.IRequestApi;
import com.hjq.http.listener.HttpCallbackProxy;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnUpdateListener;
import com.hjq.http.model.FileContentResolver;
import com.hjq.http.model.HttpMethod;
import com.hjq.http.model.ResponseClass;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.toast.Toaster;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 网络请求示例
 */
public final class MainActivity extends BaseActivity implements View.OnClickListener {

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
            .permission(PermissionLists.getManageExternalStoragePermission())
            .request((grantedList, deniedList) -> {
                boolean allGranted = deniedList.isEmpty();
                if (!allGranted) {
                    // 判断请求失败的权限是否被用户勾选了不再询问的选项
                    boolean doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(MainActivity.this, deniedList);
                    if (doNotAskAgain) {
                        Toaster.show(getString(R.string.toast_permission_storage));
                        XXPermissions.startPermissionActivity(MainActivity.this, deniedList);
                    } else {
                        Toaster.show(getString(R.string.toast_permission_request));
                        requestPermission();
                    }
                }
            });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!XXPermissions.isGrantedPermission(this, PermissionLists.getManageExternalStoragePermission())) {
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
                        public void onHttpSuccess(@NonNull HttpData<List<SearchAuthorApi.Bean>> result) {
                            Toaster.show(getString(R.string.toast_get_success));
                        }
                    });

        } else if (viewId == R.id.btn_main_post) {

            EasyHttp.post(this)
                .api(new SearchBlogsApi()
                    .setKeyword("搬砖不再有"))
                    .request(new HttpCallbackProxy<HttpData<SearchBlogsApi.Bean>>(MainActivity.this) {

                        @Override
                        public void onHttpSuccess(@NonNull HttpData<SearchBlogsApi.Bean> result) {
                            Toaster.show(getString(R.string.toast_post_success));
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
                    Toaster.show(getString(R.string.toast_sync_success));
                } catch (Throwable throwable) {
                    Toaster.show(throwable.getMessage());
                }
                runOnUiThread(this::hideDialog);
            }).start();

        } else if (viewId == R.id.btn_main_update) {

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                Toaster.show(getString(R.string.toast_upload_progress));
                return;
            }

            /*
            // 如果是放到外部存储目录下则需要适配分区存储
            String fileName = "EasyHttp.png";
            File file;
            Uri outputUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 适配 Android 10 分区存储特性
                ContentValues values = new ContentValues();
                // 设置显示的文件名
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                // 生成一个新的 uri 路径
                outputUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                file = new FileContentResolver(getContentResolver(), outputUri, fileName);
            } else {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
            }
            */

            // 如果是放到外部存储的应用专属目录则不需要适配分区存储特性
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "test_image.png");

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
                        public void onUpdateStart(@NonNull IRequestApi api) {
                            mProgressBar.setProgress(0);
                            mProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onUpdateProgressChange(int progress) {
                            mProgressBar.setProgress(progress);
                        }

                        @Override
                        public void onUpdateSuccess(@NonNull Void result) {
                            Toaster.show(getString(R.string.toast_upload_success));
                        }

                        @Override
                        public void onUpdateFail(@NonNull Throwable throwable) {
                            Toaster.show(getString(R.string.toast_upload_fail));
                        }

                        @Override
                        public void onUpdateEnd(@NonNull IRequestApi api) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });

        } else if (viewId == R.id.btn_main_download) {

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                Toaster.show("当前正在上传或者下载，请等待完成之后再进行操作");
                return;
            }

            /*
            // 如果是放到外部存储目录下则需要适配分区存储
            String fileName = "微信 8.0.15.apk";

            File file;
            Uri outputUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 适配 Android 10 分区存储特性
                ContentValues values = new ContentValues();
                // 设置显示的文件名
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                // 生成一个新的 uri 路径
                // 注意这里使用 ContentResolver 插入的时候都会生成新的 Uri
                // 解决方式将 ContentValues 和 Uri 作为 key 和 value 进行持久化关联
                // outputUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                outputUri = ContentResolverUriStore.insert(this, Downloads.EXTERNAL_CONTENT_URI, values);
                file = new FileContentResolver(getContentResolver(), outputUri, fileName);
            } else {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            }
            */

            // 如果是放到外部存储的应用专属目录则不需要适配分区存储特性
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "wechat_8.0.15.apk");

            EasyHttp.download(this)
                    .method(HttpMethod.GET)
                    .file(file)
                    //.url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
                    .url("https://dldir1.qq.com/weixin/android/weixin8015android2020_arm64.apk")
                    .md5("b05b25d4738ea31091dd9f80f4416469")
                    // 设置断点续传（默认不开启）
                    .resumableTransfer(true)
                    .listener(new OnDownloadListener() {

                        @Override
                        public void onDownloadStart(@NonNull File file) {
                            mProgressBar.setProgress(0);
                            mProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onDownloadProgressChange(@NonNull File file, int progress) {
                            mProgressBar.setProgress(progress);
                        }

                        @Override
                        public void onDownloadSuccess(@NonNull File file) {
                            Toaster.show(getString(R.string.toast_download_success, file.getPath()));
                            installApk(MainActivity.this, file);
                        }

                        @Override
                        public void onDownloadFail(@NonNull File file, @NonNull Throwable throwable) {
                            Toaster.show(getString(R.string.toast_download_fail, throwable.getMessage()));
                            file.delete();
                        }

                        @Override
                        public void onDownloadEnd(@NonNull File file) {
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
        XXPermissions.with(this)
            .permission(PermissionLists.getRequestInstallPackagesPermission())
            .request((grantedList, deniedList) -> {
                boolean allGranted = deniedList.isEmpty();
                if (!allGranted) {
                    Toaster.show(getString(R.string.toast_install_fail));
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
            });
    }
}