package com.hjq.http.request;

import android.content.Context;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.callback.CommonCallback;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : Post 请求
 */
public final class PostRequest extends BaseRequest<PostRequest> {

    public PostRequest(Context context) {
        super(context);
    }

    @Override
    protected Request create(String url, String tag, HttpParams params, HttpHeaders headers) {
        Request.Builder request = new Request.Builder();
        request.url(url);
        if (tag != null) {
            request.tag(tag);
        }

        // 添加请求头
        if (!headers.isEmpty()) {
            for (String key : headers.getNames()) {
                request.addHeader(key, headers.get(key));
            }
        }

        MultipartBody.Builder body = new MultipartBody.Builder();
        body.setType(MultipartBody.FORM);
        // 添加参数
        if (!params.isEmpty()) {
            for (String key : params.getNames()) {
                String value = params.get(key);
                File file = new File(value);
                if (file.exists() && file.isFile()) {
                    // 如果这是一个文件
                    body.addFormDataPart(key, file.getName(), new UpdateRequestBody(file));
                } else {
                    // 如果这是一个参数
                    body.addFormDataPart(key, value);
                }
            }
        }
        request.post(body.build());
        return request.build();
    }

    /**
     * 执行请求
     */
    public void request(OnHttpListener listener) {
        Call call = create();
        EasyConfig.getInstance().getHandler().requestStart(getContext(), call);
        call.enqueue(new CommonCallback(getContext(), listener));
    }

    private class UpdateRequestBody extends RequestBody {

        private File mFile;

        private UpdateRequestBody(File file) {
            mFile = file;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/octet-stream");
        }

        @Override
        public long contentLength() {
            return mFile.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;
            try {
                source = Okio.source(mFile);
                Buffer buffer = new Buffer();
                // 文件大小
                long totalSize = contentLength();
                // 当前进度
                long currentSize = 0;
                long readCount;
                while ((readCount = source.read(buffer, 2048)) != -1) {
                    sink.write(buffer, readCount);
                    currentSize += readCount;
                    if (EasyLog.isEnable()) {
                        EasyLog.print(mFile.getName() + "文件总字节：" + totalSize + "，已上传字节：" + currentSize);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (source != null) {
                    source.close();
                }
            }
        }
    }
}