package com.mygit.sslhttps;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mygit.sslhttps.models.ModelBase;
import com.mygit.sslhttps.models.TokenPersist;
import com.mygit.sslhttps.ssl.SelfSSLSocketFactory;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Entry point for all requests to **My Project** API.
 * Uses Retrofit library to abstract the actual REST API into a service.
 */
public class MyProjectApi {

    private static final String TAG = "MyProjectApi";
    int DEFAULT_TIMEOUT_MS = 12000;
    private static MyProjectApi instance;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    /**
     * Returns the instance of this singleton.
     */
    public static MyProjectApi getInstance() {
        if (instance == null) {
            instance = new MyProjectApi(MyApplication.getContext());
        }
        return instance;
    }

    /**
     * Private singleton constructor.
     */
    private MyProjectApi(Context context) {
        /*普通网络请求-----创建队列对象*/
//        if (mRequestQueue == null) {
////            mRequestQueue = Volley.newRequestQueue(context);
//        }

        /*HTTPS协议  SSL加密---创建队列对象*/
        if (mRequestQueue == null) {
            SSLSocketFactory sslSocketFactory = SelfSSLSocketFactory.getSSLSocketFactory(context);
            Network network = new BasicNetwork(new HurlStack(null, sslSocketFactory));
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // 当URL的主机名和服务器的标识主机名不匹配默认返回true
                    return true;
                }
            });
        }
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, BitMapCache.getInstance());
        }
    }

    public void deleteDiskCache() {
        mRequestQueue.getCache().clear();
    }

    /**
     * Creates JsonObjectRequest  by setting custom HttpClient.
     */
    public void buildJsonRequest(final String urlSuffix, final JSONObject json, final Type classType, final ModelBase.OnResult callBack) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, urlSuffix, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ModelBase model = null;
                        try {
                            model = gson.fromJson(response.toString(), classType);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if (model != null) {
                            if (model.isLogout()) {
                                /*
                                * 需要登出时，清理本地账户存储，跳转到首页
                                * */
//                                UserPersist.saveNewUser(null);
//                                TokenPersist.storeToken(null);
//                                LocalData.put(KeyConstants.KEY_ACTIVITY_CALLING_FOR_LOGIN, MyActivityManager.getInstance().getCurrentActivity());
//                                NaviJump.gotoLoginActivity(MyActivityManager.getInstance().getCurrentActivity());

                                Gson gsonkk = new Gson();
                                String errorResponse = "{'resultMsg':'登录过期，请重新登录','resultCode':'-1'}";
                                ModelBase modelkk = null;
                                try {
                                    modelkk = gsonkk.fromJson(errorResponse, classType);
                                    if (model != null) {
                                        callBack.OnListener(modelkk);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (callBack != null) {
                                    callBack.OnListener(model);
                                }
                            }
                        } else {
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                /**
                 Volley的异常列表：
                 AuthFailureError：如果在做一个HTTP的身份验证，可能会发生这个错误。
                 NetworkError：Socket关闭，服务器宕机，DNS错误都会产生这个错误。
                 NoConnectionError：和NetworkError类似，这个是客户端没有网络连接。
                 ParseError：在使用JsonObjectRequest或JsonArrayRequest时，如果接收到的JSON是畸形，会产生异常。
                 SERVERERROR：服务器的响应的一个错误，最有可能的4xx或5xx HTTP状态代码。
                 TimeoutError：Socket超时，服务器太忙或网络延迟会产生这个异常。默认情况下，Volley的超时时间为2.5秒。如果得到这个错误可以使用RetryPolicy。
                 */
                Log.d(TAG, "Error: " + error.getMessage());
                if (callBack != null) {
                    Gson gson = new Gson();
                    String errorResponse = "{'resultMsg':'','resultCode':'-1'}";
                    if (error instanceof TimeoutError) {
                        errorResponse = "{'resultMsg':'网络繁忙，请稍后再试','resultCode':'-1'}";
                    } else if (error instanceof NoConnectionError) {   //NoConnectionError 是NetworkError子类
                        errorResponse = "{'resultMsg':'网络未连接','resultCode':'-1'}";
                    } else if (error instanceof NetworkError) {
                        errorResponse = "{'resultMsg':'网络繁忙，请稍后再试...','resultCode':'-1'}";
                    }
                    ModelBase model = null;
                    try {
                        model = gson.fromJson(errorResponse, classType);
                        if (model != null) {
                            callBack.OnListener(model);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }) {
            //添加请求头（token和imei）
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                TelephonyManager tm = (TelephonyManager) mContext.
                        getSystemService(Context.TELEPHONY_SERVICE);
                headers.put("imei", tm.getDeviceId());
                headers.put("qpToken", TokenPersist.getToken());
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonObjReq);

    }


}
