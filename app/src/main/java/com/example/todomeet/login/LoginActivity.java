package com.example.todomeet.login;

import static com.example.todomeet.api.NetworkClient.okHttpClient;

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
import com.example.todomeet.api.ApiService;
import com.example.todomeet.api.NetworkClient;
import com.example.todomeet.model.JwtResponse;
import com.example.todomeet.model.User;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_button);
        SharedPreferences sharedPreferences = getSharedPreferences("accessToken", MODE_PRIVATE);

        if (sharedPreferences != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        ImageButton kakaoLoginButton = findViewById(R.id.mainLoginButton);

        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {

            public void postLogin(String email, String nickname, String profileImage) {
                OkHttpClient okHttpClient = NetworkClient.getOkHttpClient(LoginActivity.this);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.api_server)+"/api/auth/")
//                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);

                User user = new User(email, nickname, profileImage);
                Call<JwtResponse> call = apiService.login(user);

                call.enqueue(new Callback<JwtResponse>() {
                    @Override
                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                        System.out.println("response: " + response);
                        if (response.isSuccessful() && response.body() != null) {
                            JwtResponse jwtResponse = response.body();

                            String at = jwtResponse.getAccessToken();
                            String rt = jwtResponse.getRefreshToken();

                            System.out.println("token: " + at);
                            System.out.println(("refresh: " + rt));
                            SharedPreferences sharedPreferences = getSharedPreferences("accessToken", MODE_PRIVATE);
                            SharedPreferences refreshToken = getSharedPreferences("refreshToken", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            SharedPreferences.Editor editor1 = refreshToken.edit();
                            editor.putString("accessToken", at);
                            editor1.putString("refreshToken", rt);
                            editor.apply();
                        }
                    }

                    @Override
                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                        System.out.println("JWT 토큰 얻기 실패: " + t.getMessage());
                    }
                });
            }

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

                        postLogin(user.getKakaoAccount().getEmail(),
                                user.getKakaoAccount().getProfile().getNickname(),
                                user.getKakaoAccount().getProfile().getProfileImageUrl());

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
