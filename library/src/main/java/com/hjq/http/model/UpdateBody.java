package com.hjq.http.model;

import com.hjq.http.EasyLog;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/14
 *    desc   : 上传请求
 */
public final class UpdateBody extends RequestBody {

    private File mFile;

    public UpdateBody(File file) {
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
        Source source = Okio.source(mFile);
        Buffer buffer = new Buffer();
        long totalLength = contentLength();
        long updateLength = 0;
        long readCount;
        while ((readCount = source.read(buffer, 2048)) != -1) {
            sink.write(buffer, readCount);
            updateLength += readCount;

            EasyLog.print(mFile.getPath() + " 正在上传" +
                    "，文件总字节：" + totalLength +
                    "，已上传字节：" + updateLength);
        }
    }
}