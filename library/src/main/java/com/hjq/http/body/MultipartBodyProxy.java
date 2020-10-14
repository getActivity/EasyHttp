package com.hjq.http.body;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.lifecycle.HttpLifecycleControl;
import com.hjq.http.listener.OnUpdateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/08/15
 *    desc   : MultipartBody 代理类
 */
public final class MultipartBodyProxy extends RequestBody {

    private final MultipartBody mMultipartBody;
    private final OnUpdateListener mListener;
    private final LifecycleOwner mLifecycleOwner;

    public MultipartBodyProxy(MultipartBody body, LifecycleOwner lifecycleOwner, OnUpdateListener listener) {
        mMultipartBody = body;
        mLifecycleOwner = lifecycleOwner;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mMultipartBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mMultipartBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        final long totalByte = contentLength();
        mMultipartBody.writeTo(sink = Okio.buffer(new ForwardingSink(sink) {

            private long updateByte;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                updateByte += byteCount;
                int progress = EasyUtils.getProgressPercent(totalByte, updateByte);
                EasyLog.print("正在进行上传" +
                        "，总字节：" + totalByte +
                        "，已上传：" + updateByte +
                        "，进度：" + progress + "%");
                EasyUtils.post(() -> {
                    if (mListener != null && HttpLifecycleControl.isLifecycleActive(mLifecycleOwner)) {
                        mListener.onUpdate(totalByte, updateByte, progress);
                    }
                });
            }
        }));
        sink.flush();
    }

    public static final class Builder {

        private final MultipartBody.Builder mBuilder;
        private OnUpdateListener mListener;
        private LifecycleOwner mLifecycleOwner;

        public Builder() {
            mBuilder = new MultipartBody.Builder();
        }

        public Builder setType(MediaType type) {
            mBuilder.setType(type);
            return this;
        }

        public Builder addPart(MultipartBody.Part part) {
            mBuilder.addPart(part);
            return this;
        }

        public Builder addFormDataPart(String name, String value) {
            mBuilder.addFormDataPart(name, value);
            return this;
        }

        public Builder addFormDataPart(String name, @Nullable String filename, RequestBody body) {
            mBuilder.addFormDataPart(name, filename, body);
            return this;
        }

        public Builder setOnUpdateListener(OnUpdateListener listener) {
            mListener = listener;
            return this;
        }

        public Builder setLifecycleOwner(LifecycleOwner lifecycleOwner)  {
            mLifecycleOwner = lifecycleOwner;
            return this;
        }

        public MultipartBodyProxy build() {
            return new MultipartBodyProxy(mBuilder.build(), mLifecycleOwner, mListener);
        }
    }

    /**
     * 根据 File 对象创建一个流媒体
     */
    public static MultipartBody.Part createPart(String key, File file) {
        if (file.exists() && file.isFile()) {
            try {
                // 文件名必须不能带中文，所以这里要编码
                return MultipartBody.Part.createFormData(key, EasyUtils.encodeString(file.getName()), new UpdateBody(file));
            } catch (FileNotFoundException e) {
                EasyLog.print(e);
            }
        } else {
            EasyLog.print("文件不存在，将被忽略上传：" + key + " = " + file.getPath());
        }
        return null;
    }

    /**
     * 根据 InputStream 对象创建一个流媒体
     */
    public static MultipartBody.Part createPart(String key, InputStream inputStream) {
        try {
            return MultipartBody.Part.createFormData(key, null, new UpdateBody(inputStream, key));
        } catch (IOException e) {
            EasyLog.print(e);
        }
        return null;
    }
}