package com.hjq.http.body;

import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2022/09/17
 *    desc   : RequestBody 包装类
 */
public class WrapperRequestBody extends RequestBody {

   private final RequestBody mRequestBody;

   public WrapperRequestBody(RequestBody body) {
      mRequestBody = body;
   }

   @Override
   public long contentLength() throws IOException {
      return mRequestBody.contentLength();
   }

   @Override
   public MediaType contentType() {
      return mRequestBody.contentType();
   }

   @Override
   public void writeTo(@NonNull BufferedSink sink) throws IOException {
      mRequestBody.writeTo(sink);
   }

   /**
    * 获取当前的 RequestBody
    */
   public RequestBody getRequestBody() {
      return mRequestBody;
   }
}