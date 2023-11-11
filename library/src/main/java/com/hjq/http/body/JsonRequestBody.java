package com.hjq.http.body;

import androidx.annotation.NonNull;
import com.hjq.http.EasyUtils;
import com.hjq.http.model.ContentType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/12/28
 *    desc   : Json 参数提交
 */
public class JsonRequestBody extends RequestBody {

    /** Json 数据 */
    private final String mJson;
    /** 字节数组 */
    private final byte[] mBytes;

    public JsonRequestBody(Map<?, ?> map) {
        this(new JSONObject(map));
    }

    public JsonRequestBody(List<?> list) {
        this(new JSONArray(list));
    }

    public JsonRequestBody(JSONObject jsonObject) {
        mJson = EasyUtils.unescapeJson(jsonObject.toString());
        mBytes = mJson.getBytes();
    }

    public JsonRequestBody(JSONArray jsonArray) {
        mJson = EasyUtils.unescapeJson(jsonArray.toString());
        mBytes = mJson.getBytes();
    }

    public JsonRequestBody(String json) {
        mJson = json;
        mBytes = mJson.getBytes();
    }

    @Override
    public MediaType contentType() {
        return ContentType.JSON;
    }

    @Override
    public long contentLength() {
        // 需要注意：这里需要用字节数组的长度来计算
        return mBytes.length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.write(mBytes, 0, mBytes.length);
    }

    @NonNull
    @Override
    public String toString() {
        return mJson;
    }

    /**
     * 获取 Json 字符串
     */
    @NonNull
    public String getJson() {
        return mJson;
    }
}