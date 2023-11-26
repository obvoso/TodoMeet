package com.example.todomeet.api;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkClient {
    public static OkHttpClient okHttpClient;

    public static synchronized OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();

                            // SharedPreferences에서 토큰을 가져옵니다.
                            String token = context.getSharedPreferences("accessToken", Context.MODE_PRIVATE)
                                    .getString("accessToken", "");

                            // 토큰이 있는 경우에만 헤더에 토큰을 첨부합니다.
                            if (token != null && !token.isEmpty()) {
                                Request newRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + token)
                                        .build();

                                return chain.proceed(newRequest);
                            } else {
                                return chain.proceed(originalRequest);
                            }
                        }
                    })
                    .build();
        }

        return okHttpClient;
    }
}
