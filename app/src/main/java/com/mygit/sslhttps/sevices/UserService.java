package com.mygit.sslhttps.sevices;

import android.content.Context;

import com.mygit.sslhttps.MyProjectApi;
import com.mygit.sslhttps.models.CodeModel;
import com.mygit.sslhttps.models.ModelBase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口
 * Created by admin on 2015/11/3.
 */
public class UserService {

    private static UserService sInstance;

    private UserService() {
    }

    public static UserService getInstance() {
        if (sInstance == null) {
            sInstance = new UserService();
        }
        return sInstance;
    }
    /**
     * 短信发送
     *
     * @param mobile   手机号（限制11位数字 1开头）
     *                 type     type=1 注册， type=2 找回密码……
     * @param callBack
     */
    public void smsSend(Context mContext, String mobile, ModelBase.OnResult callBack) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("type", "1");
        params.put("version", "版本号6.1.0");
        params.put("phoneType", "android");
        JSONObject json = new JSONObject(params);
        MyProjectApi.getInstance().buildJsonRequest("请求url", json, CodeModel.class, callBack);
    }

}


