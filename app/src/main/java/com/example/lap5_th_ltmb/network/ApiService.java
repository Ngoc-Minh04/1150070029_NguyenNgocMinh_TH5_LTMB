package com.example.lap5_th_ltmb.network;

import com.example.lap5_th_ltmb.network.ApiModels.ApiResponse;
import com.example.lap5_th_ltmb.network.ApiModels.LoginRequest;
import com.example.lap5_th_ltmb.network.ApiModels.RegisterRequest;
import com.example.lap5_th_ltmb.network.ApiModels.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/Users/register")
    Call<ApiResponse<UserResponse>> register(@Body RegisterRequest request);

    @POST("api/Users/login")
    Call<ApiResponse<UserResponse>> login(@Body LoginRequest request);
}


