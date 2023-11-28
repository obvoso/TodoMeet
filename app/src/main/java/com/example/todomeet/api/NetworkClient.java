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

                            String token = context.getSharedPreferences("accessToken", Context.MODE_PRIVATE)
                                    .getString("accessToken", "");

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
