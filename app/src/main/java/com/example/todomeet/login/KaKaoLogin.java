package com.example.todomeet.login;

import android.app.Application;

import com.example.todomeet.R;
import com.kakao.sdk.common.KakaoSdk;

public class KaKaoLogin extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key));
    }
}
