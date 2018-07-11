package com.mygit.sslhttps.models;

import android.content.SharedPreferences;

import com.mygit.sslhttps.MyApplication;


/**
 * Created by admin on 2016/9/6.
 */
public class TokenPersist {

    private static final String TOKEN_STORE_FILE = "Token";

    private static SharedPreferences getSp() {
        return MyApplication.getContext().getSharedPreferences(TOKEN_STORE_FILE, 0);
    }

    /**
     * 保存token
     */
    public static void storeToken(String Id) {
        getSp().edit().putString("token_id", Id).commit();
    }

    /**
     * 得到token
     */
    public static String getToken() {
        return getSp().getString("token_id", "");
    }


    /**
     * 删除token
     */
    public static void delToken() {
        getSp().edit().clear().commit();
    }
}
