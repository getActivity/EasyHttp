package com.hjq.http.test;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.TypeAdapters;
import com.hjq.http.demo.http.json.BooleanTypeAdapter;
import com.hjq.http.demo.http.json.DoubleTypeAdapter;
import com.hjq.http.demo.http.json.FloatTypeAdapter;
import com.hjq.http.demo.http.json.IntegerTypeAdapter;
import com.hjq.http.demo.http.json.ListTypeAdapter;
import com.hjq.http.demo.http.json.LongTypeAdapter;
import com.hjq.http.demo.http.json.StringTypeAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JsonUnitTest {

    private Gson mGson;

    @Before
    public void onTestBefore() {
        mGson = new GsonBuilder()
                .registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, new StringTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(boolean.class, Boolean.class, new BooleanTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(int.class, Integer.class, new IntegerTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(long.class, Long.class, new LongTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(float.class, Float.class, new FloatTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(double.class, Double.class, new DoubleTypeAdapter()))
                .registerTypeHierarchyAdapter(List.class, new ListTypeAdapter())
                .create();
    }

    @Test
    public void onSpecification() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        String json = getAssetsString(context, "Specification.json");
        mGson.fromJson(json, JsonBean.class);
    }

    @Test
    public void onNoSpecification() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        String json = getAssetsString(context, "NoSpecification.json");
        mGson.fromJson(json, JsonBean.class);
    }

    @After
    public void onTestAfter() {
        mGson = null;
    }

    /**
     * 获取资产目录下面文件的字符串
     */
    private static String getAssetsString(Context context, String file) {
        try {
            InputStream inputStream = context.getAssets().open(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            inputStream.close();
            return outStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}