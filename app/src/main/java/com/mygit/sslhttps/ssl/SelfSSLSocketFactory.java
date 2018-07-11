package com.mygit.sslhttps.ssl;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by admin on 2017/7/27.
 */
public class SelfSSLSocketFactory {

    /**
     * 获取SSLSocketFactory
     *
     * @param context
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            return setCertificates(context, context.getAssets().open(CertificateConfig.trustStoreFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 产生SSLSocketFactory
     *
     * @param context
     * @param certificates
     * @return
     */
    private static SSLSocketFactory setCertificates(Context context, InputStream... certificates) {
        try {
            /*
            *
                1）首先，在做项目时为加强安全性，需要读取 x.crt证书文件获取公钥；此时使用普通的IO流读取文件是行不通的，
                通过学习，发现用下面的方法可以完美解决。
                2）在使用的过程中，遇到了一个bug：
                java.security.cert.CertificateException: com.android.org.conscrypt.OpenSSLX509……
                bug信息太长，后面的省略了，总结异常关键点：
                CertificateException - OpenSSLX509CertificateFactory$ParsingException；
                后来发现方法中需要多传一个"BC"，就可以解决问题：
                eg：CertificateFactory.getInstance("X.509","BC");
            */

            //读取证书CertificateFactory是读取证书的对象，certificateFactory.generateCertificate(certificate)读取证书的方法
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            //创建证书库并将证书导入证书库
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*SSL是Netscape开发的专门用户保护Web通讯的，目前版本为3.0。
            最新版本的TLS 1.0是IETF(工程任务组)制定的一种新的协议，
            它建立在SSL 3.0协议规范之上，是SSL 3.0的后续版本。两者差别极小，
            可以理解为SSL 3.1，它是写入了RFC的。 */

            //取得SSL的SSLContext实例
            SSLContext sslContext = SSLContext.getInstance("TLS");
            ////取得KeyManagerFactory和TrustManagerFactory的X509密钥管理器实例
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());


            trustManagerFactory.init(keyStore);


            //初始化keystore  //取得BKS密库实例  //加客户端载证书和私钥,通过读取资源文件的方式读取密钥和信任证书
            KeyStore clientKeyStore = KeyStore.getInstance(CertificateConfig.KEY_STORE_TYPE_BKS);
            clientKeyStore.load(context.getAssets().open(CertificateConfig.keyStoreFileName), CertificateConfig.keyStorePassword.toCharArray());

            ////KeyManager选择证书证明自己的身份 //TrustManager决定是否信任对方的证书
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            //初始化密钥管理器
            keyManagerFactory.init(clientKeyStore, CertificateConfig.trustStorePassword.toCharArray());
            //初始化SSLContext
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            //生成SSLSocket
            return sslContext.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
