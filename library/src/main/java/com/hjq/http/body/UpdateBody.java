package com.hjq.http.body;

import com.hjq.http.EasyUtils;
import com.hjq.http.model.ContentType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/14
 *    desc   : 上传文件流
 */
public class UpdateBody extends RequestBody {

    /** 上传源 */
    private final Source mSource;

    /** 内容类型 */
    private final MediaType mMediaType;

    /** 内容名称 */
    private final String mName;

    /** 内容大小 */
    private final long mLength;

    public UpdateBody(File file) throws FileNotFoundException {
        this(Okio.source(file), EasyUtils.guessMimeType(file.getName()), file.getName(), file.length());
    }

    public UpdateBody(InputStream inputStream, String name) throws IOException {
        this(Okio.source(inputStream), ContentType.STREAM, name, inputStream.available());
    }

    public UpdateBody(Source source, MediaType type, String name, long length) {
        mSource = source;
        mMediaType = type;
        mName = name;
        mLength = length;
    }

    @Override
    public MediaType contentType() {
        return mMediaType;
    }

    @Override
    public long contentLength() {
        return mLength;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try {
            sink.writeAll(mSource);
        } finally {
            EasyUtils.closeStream(mSource);
        }
    }

    public String getName() {
        return mName;
    }
}