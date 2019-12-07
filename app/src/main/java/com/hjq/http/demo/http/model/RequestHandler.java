package com.hjq.http.demo.http.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.demo.R;
import com.hjq.http.exception.CancelException;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.HttpException;
import com.hjq.http.exception.NetworkException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.exception.ResultException;
import com.hjq.http.exception.ServerException;
import com.hjq.http.exception.TimeoutException;
import com.hjq.http.exception.TokenException;
import com.hjq.http.exception.UnknownException;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 请求处理类
 */
public final class RequestHandler implements IRequestHandler {

    private static final Gson GSON = new Gson();

    private HashMap<Call, ProgressDialog> mDialogs = new HashMap<>();

    @Override
    public void requestStart(Context context, final Call call) {
        if (context == null) {
            return;
        }

        // 显示进度对话框
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.http_loading));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });
        mDialogs.put(call, dialog);
        dialog.show();
    }

    @Override
    public void requestEnd(Context context, Call call) {
        ProgressDialog dialog = mDialogs.get(call);
        if (dialog != null) {
            dialog.dismiss();
            mDialogs.remove(call);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object requestSucceed(Context context, Call call, Response response, OnHttpListener listener) throws Exception {
        try {

            if (response.code() == 200) {

                ResponseBody body = response.body();
                if (body == null) {
                    return null;
                }

                Class clazz = null;
                Type type = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

                if (type instanceof Class) {
                    clazz = (Class) type;
                }

                if (Response.class.equals(clazz)) {
                    return response;
                } else if (Bitmap.class.equals(clazz)) {
                    // 如果这是一个 Bitmap 对象
                    return BitmapFactory.decodeStream(body.byteStream());
                } else {

                    String string = body.string();

                    if (EasyLog.isEnable()) {
                        // 打印这个 Json
                        EasyLog.json(string);
                    }

                    final Object result;
                    if (String.class.equals(clazz)) {
                        // 如果这是一个 String 对象
                        result = string;
                    } else if (JSONObject.class.equals(clazz)) {
                        // 如果这是一个 JSONObject 对象
                        result = new JSONObject(string);
                    } else if (JSONArray.class.equals(clazz)) {
                        // 如果这是一个 JSONArray 对象
                        result = new JSONArray(string);
                    } else {
                        result = GSON.fromJson(string, getSuperclassTypeParameter(listener.getClass()));
                        if (result instanceof HttpData) {
                            HttpData model = (HttpData) result;
                            if (model.getCode() == 0) {
                                // 代表执行成功
                                return result;
                            } else if (model.getCode() == 1001) {
                                // 代表登录失效，需要重新登录
                                throw new TokenException(context.getString(R.string.http_account_error));
                            } else {
                                // 代表执行失败
                                throw new ResultException(model.getMessage(), model);
                            }
                        }
                    }
                    return result;
                }
            }

            // 返回结果读取异常
            throw new ResponseException(context.getString(R.string.http_server_error), response);

        } catch (IOException e) {
            // 返回结果读取异常
            throw new DataException(context.getString(R.string.http_data_explain_error), e);
        } catch (JSONException e) {
            // Json 解析异常
            throw new DataException(context.getString(R.string.http_data_explain_error), e);
        } catch (JsonSyntaxException e) {
            // Gson 解析异常
            throw new DataException(context.getString(R.string.http_data_explain_error), e);
        } catch (Exception e) {
            // 其他未知异常
            throw new UnknownException(context.getString(R.string.http_unknown_error), e);
        } finally {
            // 请求完毕，关闭响应
            response.close();
        }
    }

    @Override
    public Exception requestFail(Context context, Call call, Exception e, OnHttpListener listener) {
        // 判断这个异常是不是自己抛的
        if (e instanceof HttpException) {
            if (e instanceof TokenException) {
                // 登录信息失效，跳转到登录页

            }
        } else {
            if (e instanceof SocketTimeoutException) {
                e = new TimeoutException(context.getString(R.string.http_server_out_time), e);
            } else if (e instanceof UnknownHostException) {
                NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                // 判断网络是否连接
                if (info != null && info.isConnected()) {
                    // 有连接就是服务器的问题
                    e = new ServerException(context.getString(R.string.http_server_error), e);
                } else {
                    // 没有连接就是网络异常
                    e = new NetworkException(context.getString(R.string.http_network_error), e);
                }
            } else if (e instanceof IOException) {
                e = new CancelException(context.getString(R.string.http_request_cancel), e);
            }else {
                e = new HttpException(e.getMessage(), e);
            }
        }

        if (EasyLog.isEnable()) {
            // 打印错误信息
            e.printStackTrace();
        }

        String message = e.getMessage();
        if (message != null && !"".equals(message)) {
            // 弹出错误提示
            ToastUtils.show(message);
        }
        return e;
    }

    private Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericInterfaces()[0];
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }
}