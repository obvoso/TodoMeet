package com.example.todomeet.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.todomeet.MainActivity;
import com.example.todomeet.R;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_button);

        ImageButton kakaoLoginButton = findViewById(R.id.mainLoginButton);

        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {

            public void getUserInfo() {
                String TAG = "getUserInfo()";
                UserApiClient.getInstance().me((user, meError) -> {
                    if (meError != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", meError);
                    } else {
                        SharedPreferences sharedPref = getSharedPreferences("sharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putLong("userId", user.getId());
                        editor.putString("userName", user.getKakaoAccount().getProfile().getNickname());
                        editor.putString("userEmail", user.getKakaoAccount().getEmail());
                        editor.putString("profileImageUrl", user.getKakaoAccount().getProfile().getProfileImageUrl());
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    return null;
                });
            }

            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this,
                            (token, error) -> {
                                if (token != null && error == null) {
                                    getUserInfo();
                                }
                                return null;
                            });
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this,
                            (token, error) -> {
                                if (token != null && error == null) {
                                    getUserInfo();
                                }
                                return null;
                            });
                }
            }
        });
    }
}
