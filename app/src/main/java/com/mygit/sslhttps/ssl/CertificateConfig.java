package com.mygit.sslhttps.ssl;

/**
 * Created by admin on 2017/7/27.
 */
public class CertificateConfig {
    //这里的bks文件和cer文件是公司给的证书，放到了assets里面，使用的时候，直接从assets取出来
    //因为是自己的项目，所以assets里面放置假的 a.weiyan.com.bks文件和a.weiyan.com.cer文件
    public static final String KEY_STORE_TYPE_BKS = "BKS";
    public static final String keyStoreFileName = "a.weiyan.com.bks";
    public static final String keyStorePassword = "123456";
    public static final String trustStoreFileName = "a.weiyan.com.cer";
    public static final String trustStorePassword = "123456";
}


