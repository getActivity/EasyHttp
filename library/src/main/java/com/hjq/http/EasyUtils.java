package com.hjq.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.hjq.http.annotation.HttpIgnore;
import com.hjq.http.annotation.HttpRename;
import com.hjq.http.body.UpdateBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/11/17
 *    desc   : 请求工具类
 */
public final class EasyUtils {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 在主线程中执行
     */
    public static boolean post(Runnable r) {
        return HANDLER.post(r);
    }

    /**
     * 延迟一段时间执行
     */
    public static boolean postDelayed(Runnable r, long delayMillis) {
        return HANDLER.postDelayed(r, delayMillis);
    }

    /**
     * 关闭流
     */
    public static void closeStream(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            EasyLog.print(e);
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createFolder(File targetFolder) {
        if (targetFolder.exists()) {
            if (targetFolder.isDirectory()) {
                return true;
            }
            // noinspection ResultOfMethodCallIgnored
            targetFolder.delete();
        }
        return targetFolder.mkdirs();
    }

    /**
     * 获取文件的 md5
     */
    public static String getFileMd5(File file) {
        if (file == null) {
            return null;
        }
        DigestInputStream inputStream = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            inputStream = new DigestInputStream(fis, messageDigest);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(inputStream.read(buffer) > 0)) {
                    break;
                }
            }
            messageDigest = inputStream.getMessageDigest();
            byte[] md5 = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            EasyLog.print(e);
        } finally {
            EasyUtils.closeStream(inputStream);
        }
        return null;
    }

    /**
     * 判断对象是否为 Bean 类
     */
    public static boolean isBeanType(Object object) {
        // Number：Long、Integer、Short、Double、Float、Byte
        // CharSequence：String、StringBuilder、StringBuilder
        return !(object instanceof Number || object instanceof CharSequence ||
                object instanceof Boolean || object instanceof File ||
                object instanceof InputStream || object instanceof RequestBody ||
                object instanceof Character || object instanceof JSONObject ||
                object instanceof JSONArray);
    }

    /**
     * 判断是否包含存在流参数
     */
    public static boolean isMultipart(Field[] fields) {
        for (Field field : fields) {
            // 允许访问私有字段
            field.setAccessible(true);

            // 获取对象的类型
            Class<?> clazz = field.getType();

            // 获取对象上面实现的接口
            Class<?>[] interfaces = clazz.getInterfaces();
            for (int i = 0; i <= interfaces.length; i++) {
                Class temp;
                if (i == interfaces.length) {
                    temp = clazz;
                } else {
                    temp = interfaces[i];
                }

                // 判断类型是否是 List<File>
                if (List.class.equals(temp)) {
                    Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                    if (actualTypeArguments.length == 1 && File.class.equals(actualTypeArguments[0])) {
                        return true;
                    }
                }
            }

            do {
                if (File.class.equals(clazz) || InputStream.class.equals(clazz) || RequestBody.class.equals(clazz)) {
                    return true;
                }
                // 获取对象的父类类型
                clazz = clazz.getSuperclass();
            } while (clazz != null && !Object.class.equals(clazz));
        }
        return false;
    }

    /**
     * 判断一下这个集合装载的类型是不是 File
     */
    public static boolean isFileList(List list) {
        if (list != null && !list.isEmpty()) {
            for (Object object : list) {
                if (!(object instanceof File)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 判断对象是否为空
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof List && ((List) object).isEmpty()) {
            return true;
        } else if (object instanceof Map && ((Map) object).isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * List 转 JsonArray
     */
    public static JSONArray listToJsonArray(List<?> list) {
        JSONArray jsonArray = new JSONArray();
        if (list != null && !list.isEmpty()) {
            for (Object item : list) {
                if (item instanceof List) {
                    jsonArray.put(listToJsonArray(((List) item)));
                } else if (item instanceof Map) {
                    jsonArray.put(mapToJsonObject(((Map) item)));
                } else if (isBeanType(item)) {
                    jsonArray.put(mapToJsonObject(beanToHashMap(item)));
                } else {
                    jsonArray.put(item);
                }
            }
        }
        return jsonArray;
    }

    /**
     * Map 转 JsonObject
     */
    public static JSONObject mapToJsonObject(Map<?, ?> map) {
        JSONObject jsonObject = new JSONObject();
        if (map != null && !map.isEmpty()) {
            Set<?> keySet = map.keySet();
            for (Object key : keySet) {
                Object value = map.get(key);
                try {
                    if (value instanceof List) {
                        jsonObject.put(String.valueOf(key), listToJsonArray(((List) value)));
                    } else if (value instanceof Map) {
                        jsonObject.put(String.valueOf(key), mapToJsonObject(((Map) value)));
                    } else if (isBeanType(value)) {
                        jsonObject.put(String.valueOf(key), mapToJsonObject(beanToHashMap(value)));
                    } else {
                        jsonObject.put(String.valueOf(key), value);
                    }
                } catch (JSONException e) {
                    EasyLog.print(e);
                }
            }
        }
        return jsonObject;
    }

    /**
     * Bean 类转 HashMap
     */
    public static HashMap<String, Object> beanToHashMap(Object object) {
        HashMap<String, Object> data = null;

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 允许访问私有字段
            field.setAccessible(true);

            try {

                // 获取字段的对象
                Object value = field.get(object);

                // 前提是这个字段值不能为空（基本数据类型有默认的值，而对象默认的值为 null）
                // 又或者这个字段需要忽略，则进行忽略
                if (isEmpty(value) || field.isAnnotationPresent(HttpIgnore.class)) {
                    continue;
                }

                // 获取字段的名称
                String key;
                if (field.isAnnotationPresent(HttpRename.class)) {
                    key = field.getAnnotation(HttpRename.class).value();
                } else {
                    key = field.getName();
                    // 如果是内部类则会出现一个字段名为 this$0 的外部类对象，会导致无限递归，这里要忽略掉，如果使用静态内部类则不会出现这个问题
                    // 和规避 Kotlin 自动生成的伴生对象：https://github.com/getActivity/EasyHttp/issues/15
                    if (key.matches("this\\$\\d+") || "Companion".equals(key)) {
                        continue;
                    }
                }

                if (data == null) {
                    data = new HashMap<>(fields.length);
                }

                if (value instanceof List) {
                    data.put(key, listToJsonArray(((List) value)));
                } else if (value instanceof Map) {
                    data.put(key, mapToJsonObject(((Map) value)));
                } else if (isBeanType(value)) {
                    data.put(key, beanToHashMap(value));
                } else {
                    data.put(key, value);
                }

            } catch (IllegalAccessException e) {
                EasyLog.print(e);
            }
        }

        return data;
    }

    /**
     * 获取对象反射类型
     */
    public static Type getReflectType(Object object) {
        if (object == null) {
            return Void.class;
        }
        Type type;
        Type[] types = object.getClass().getGenericInterfaces();
        if (types.length > 0) {
            // 如果这个监听对象是直接实现了接口
            type = ((ParameterizedType) types[0]).getActualTypeArguments()[0];
        } else {
            // 如果这个监听对象是通过类继承
            type = ((ParameterizedType) object.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return type;
    }

    /**
     * 获取进度百分比
     */
    public static int getProgressProgress(long totalByte, long currentByte) {
        // 计算百分比，这里踩了两个坑
        // 当文件很大的时候：字节数 * 100 会超过 int 最大值，计算结果会变成负数
        // 还有需要注意的是，long 除以 long 等于 long，这里的字节数除以总字节数应该要 double 类型的
        return (int) (((double) currentByte / totalByte) * 100);
    }

    /**
     * 字符串编码
     */
    public static String encodeString(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return URLEncoder.encode(text);
    }

    /**
     * 根据 File 对象创建一个流媒体
     */
    public static MultipartBody.Part createPart(String key, File file) {
        try {
            // 文件名必须不能带中文，所以这里要编码
            return MultipartBody.Part.createFormData(key, EasyUtils.encodeString(file.getName()), new UpdateBody(file));
        } catch (FileNotFoundException e) {
            EasyLog.print("文件不存在，将被忽略上传：" + key + " = " + file.getPath());
            return null;
        }
    }

    /**
     * 根据 InputStream 对象创建一个流媒体
     */
    public static MultipartBody.Part createPart(String key, InputStream inputStream) {
        try {
            return MultipartBody.Part.createFormData(key, null, new UpdateBody(inputStream, key));
        } catch (IOException e) {
            EasyLog.print(e);
            return null;
        }
    }
}