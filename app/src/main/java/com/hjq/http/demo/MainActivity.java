package com.hjq.http.demo;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import com.hjq.http.EasyHttp;
import com.hjq.http.demo.http.model.HttpData;
import com.hjq.http.demo.http.request.SearchAuthorApi;
import com.hjq.http.demo.http.request.SearchBlogsApi;
import com.hjq.http.demo.http.response.SearchBean;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.DownloadTask;
import com.hjq.http.model.HttpMethod;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.io.File;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener, OnPermission {

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.pb_main_progress);

        findViewById(R.id.btn_main_get).setOnClickListener(this);
        findViewById(R.id.btn_main_post).setOnClickListener(this);
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
    public void hasPermission(List<String> granted, boolean isAll) {

    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {
        if (quick) {
            ToastUtils.show("授权失败，请手动授予权限");
            XXPermissions.gotoPermissionSettings(this, true);
        } else {
            ToastUtils.show("请先授予权限");
            requestPermission();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (XXPermissions.isHasPermission(this, Permission.Group.STORAGE)) {
            hasPermission(null, true);
        }else {
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
                        .request(new OnHttpListener<HttpData<SearchBean>>() {

                            @Override
                            public void onSucceed(HttpData<SearchBean> data) {
                                ToastUtils.show("请求成功");
                            }

                            @Override
                            public void onFail(Exception e) {}
                        });
                break;
            case R.id.btn_main_post:
                EasyHttp.post(this)
                        .api(new SearchBlogsApi()
                                .setKeyword("搬砖不再有"))
                        .request(new OnHttpListener<HttpData<SearchBean>>() {

                            @Override
                            public void onSucceed(HttpData<SearchBean> data) {
                                ToastUtils.show("请求成功");
                            }

                            @Override
                            public void onFail(Exception e) {}
                        });
                break;
            case R.id.btn_main_download:
                EasyHttp.download(this)
                        .method(HttpMethod.GET)
                        .file(new File(Environment.getExternalStorageDirectory(), "手机QQ.apk"))
                        .url("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
                        .md5("47CBDF2A2940B7773DD1B63CBCFD86E1")
                        //.url("http://dldir1.qq.com/weixin/android/weixin708android1540.apk")
                        .listener(new OnDownloadListener() {

                            @Override
                            public void onDownloadStart(DownloadTask task) {
                                mProgressBar.setVisibility(View.VISIBLE);
                                ToastUtils.show("下载开始：" + task.getFile().getName());
                            }

                            @Override
                            public void onDownloadProgress(DownloadTask task) {
                                mProgressBar.setProgress(task.getProgress());
                            }

                            @Override
                            public void onDownloadComplete(DownloadTask task) {
                                mProgressBar.setVisibility(View.GONE);
                                ToastUtils.show("下载完成：" + task.getFile().getPath());
                            }

                            @Override
                            public void onDownloadError(DownloadTask task, Exception e) {
                                mProgressBar.setVisibility(View.GONE);
                                ToastUtils.show("下载出错：" + e.getMessage());
                            }

                        }).start();
                break;
            default:
                break;
        }
    }
}