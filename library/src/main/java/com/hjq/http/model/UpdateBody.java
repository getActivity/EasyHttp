package com.hjq.http.model;

import com.hjq.http.EasyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/14
 *    desc   : 上传文件请求
 */
public final class UpdateBody extends RequestBody {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/octet-stream");

    /**
     * 上传源
     */
    private final Source mSource;
    /**
     * 内容类型
     */
    private final MediaType mMediaType;
    /**
     * 内容名称
     */
    private final String mName;
    /**
     * 内容大小
     */
    private final long mLength;

    public UpdateBody(File file) throws FileNotFoundException {
        this(Okio.source(file), guessMimeType(file.getName()), file.getPath(), file.length());
    }

    public UpdateBody(InputStream inputStream, String name) throws IOException {
        this(Okio.source(inputStream), MEDIA_TYPE, name, inputStream.available());
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
        Buffer buffer = new Buffer();
        long totalLength = contentLength();
        long updateLength = 0;
        long readCount;
        while ((readCount = mSource.read(buffer, 2048)) != -1) {
            sink.write(buffer, readCount);
            updateLength += readCount;

            EasyLog.print(mName + " 正在上传" +
                    "，文件总字节：" + totalLength +
                    "，已上传字节：" + updateLength);
        }
    }

    /**
     * 根据文件名获取 MIME 类型
     */
    public static MediaType guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        // 解决文件名中含有#号异常的问题
        fileName = fileName.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return MEDIA_TYPE;
        }
        MediaType type = MediaType.parse(contentType);
        if (type == null) {
            type = MEDIA_TYPE;
        }
        return type;
    }


    /**
     * 根据 File 对象创建一个流媒体
     */
    public static MultipartBody.Part createPart(String key, File file) {
        if (file.exists() && file.isFile()) {
            try {
                // 文件名必须不能带中文，所以这里要编码
                return MultipartBody.Part.createFormData(key, URLEncoder.encode(file.getName()), new UpdateBody(file));
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