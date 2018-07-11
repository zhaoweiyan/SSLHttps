package com.mygit.sslhttps.models;

/**
 * Created by admin on 2015/11/3.
 */
public class ModelBase {

    public String resultMsg;
    public String resultCode;

    public boolean isSuccess() {
        if ("0".equals(resultCode)) {
            return true;
        }
        return false;
    }

    public boolean isLogout() {
        if ("9999999999".equals(resultCode)) {
            return true;
        }
        return false;
    }

    public interface OnResult {
        public void OnListener(ModelBase model);
//        public void OnErrorListener();
    }
}
