package com.hjq.http.body;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2022/09/17
 *    desc   : 支持自定义 Content-Type 的 RequestBody
 */
public class CustomTypeBody extends WrapperRequestBody {

   /** 内容类型 */
   private MediaType mContentType;

   public CustomTypeBody(RequestBody body) {
      super(body);
   }

   @Override
   public MediaType contentType() {
      if (mContentType != null) {
         return mContentType;
      }
      return super.contentType();
   }

   /**
    * 设置内容的类型
    */
   public void setContentType(MediaType type) {
      mContentType = type;
   }
}