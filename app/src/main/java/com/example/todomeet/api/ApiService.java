package com.example.todomeet.api;

import com.example.todomeet.model.JwtResponse;
import com.example.todomeet.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("sign-up")
    Call<JwtResponse> login(@Body User user);

    @DELETE("api")
    Call<Void> logout(@Query("userEmail") String userEmail);
}
