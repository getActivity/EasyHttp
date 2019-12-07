package com.hjq.http.demo;

import android.support.v7.app.AppCompatActivity;

import com.hjq.http.EasyHttp;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        EasyHttp.cancel(this);
        super.onDestroy();
    }
}