package com.mygit.sslhttps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mygit.sslhttps.models.ModelBase;
import com.mygit.sslhttps.sevices.UserService;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //请求网络接口，根据实际情况编写
        UserService.getInstance().smsSend(this, "电话号码", smsCodeCallback);
    }

    private ModelBase.OnResult smsCodeCallback = new ModelBase.OnResult() {
        @Override
        public void OnListener(ModelBase model) {

        }
    };
}
