package com.hjq.easy.demo.other;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2024/01/20
 *    desc   : ContentResolver uri 存储器
 */
public class ContentResolverUriStore {

    private static final String CACHE_PREFERENCES_NAME = "content_resolver_cache_store_preferences";

    public static @Nullable Uri insert(Context context, @RequiresPermission.Write @NonNull Uri url, @Nullable ContentValues values) {
        ContentResolver contentResolver = context.getContentResolver();
        SharedPreferences sharedPreferences = context.getSharedPreferences(CACHE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 使用字符串作为缓存键
        String cacheKey = "ContentResolver insert: " + "Uri = " + url + ", ContentValues = " + convertContentValuesToString(values);
        String oldUriString = sharedPreferences.getString(cacheKey, "");
        if (oldUriString != null && !"".equals(oldUriString)) {
            Cursor cursor = null;
            try {
                Uri oldUri = Uri.parse(oldUriString);
                // 查询旧的 uri 是否真实并且可用，如果是的话，再进行复用
                cursor = contentResolver.query(oldUri, null, null, null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    return oldUri;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Uri newUri = contentResolver.insert(url, values);
        // 存储数据到 SharedPreferences
        editor.putString(cacheKey, String.valueOf(newUri));
        editor.apply();
        return newUri;
    }

    /**
     * 将 ContentValues 转换为字符串
     */
    private static String convertContentValuesToString(ContentValues contentValues) {
        if (contentValues == null) {
            return "";
        }
        return contentValues.toString();
    }
}