package com.hjq.http.demo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.hjq.http.EasyHttp;
import com.hjq.http.demo.http.model.HttpData;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;

import okhttp3.Call;

public class BaseActivity extends AppCompatActivity implements OnHttpListener {

    /** 加载对话框 */
    private ProgressDialog mDialog;
    /** 对话框数量 */
    private int mDialogTotal;

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        return mDialog != null && mDialog.isShowing();
    }

    /**
     * 显示加载对话框
     */
    public void showDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage(getResources().getString(R.string.http_loading));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        mDialogTotal++;
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        if (mDialogTotal == 1) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
        if (mDialogTotal > 0) {
            mDialogTotal--;
        }
    }

    @Override
    public void onStart(Call call) {
        showDialog();
    }

    @Override
    public void onSucceed(Object result) {
        if (result instanceof HttpData) {
            ToastUtils.show(((HttpData) result).getMessage());
        }
    }

    @Override
    public void onFail(Exception e) {
        ToastUtils.show(e.getMessage());
    }

    @Override
    public void onEnd(Call call) {
        hideDialog();
    }

    @Override
    protected void onDestroy() {
        EasyHttp.cancel(this);
        super.onDestroy();
    }
}