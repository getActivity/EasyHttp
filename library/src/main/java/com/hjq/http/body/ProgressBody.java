package com.hjq.http.body;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnUpdateListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/08/15
 *    desc   : RequestBody 代理类（用于获取上传进度）
 */
public final class ProgressBody extends RequestBody {

    private final RequestBody mRequestBody;
    private final OnUpdateListener mListener;
    private final LifecycleOwner mLifecycleOwner;

    /** 总字节数 */
    private long mTotalByte;
    /** 已上传字节数 */
    private long mUpdateByte;
    /** 上传进度值 */
    private int mUpdateProgress;

    public ProgressBody(RequestBody body, LifecycleOwner lifecycleOwner, OnUpdateListener listener) {
        mRequestBody = body;
        mLifecycleOwner = lifecycleOwner;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mTotalByte = contentLength();
        mRequestBody.writeTo(sink = Okio.buffer(new ForwardingSink(sink) {

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                mUpdateByte += byteCount;
                EasyUtils.post(() -> {
                    if (mListener != null && HttpLifecycleManager.isLifecycleActive(mLifecycleOwner)) {
                        mListener.onByte(mTotalByte, mUpdateByte);
                    }
                    int progress = EasyUtils.getProgressProgress(mTotalByte, mUpdateByte);
                    // 只有上传进度发生改变的时候才回调此方法，避免引起不必要的 View 重绘
                    if (progress != mUpdateProgress) {
                        mUpdateProgress = progress;
                        if (mListener != null && HttpLifecycleManager.isLifecycleActive(mLifecycleOwner)) {
                            mListener.onProgress(progress);
                        }
                        EasyLog.print("正在进行上传" +
                                "，总字节：" + mTotalByte +
                                "，已上传：" + mUpdateByte +
                                "，进度：" + progress + "%");
                    }
                });
            }
        }));
        sink.flush();
    }
}