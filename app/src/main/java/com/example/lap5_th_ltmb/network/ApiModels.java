package com.example.lap5_th_ltmb.network;

import com.google.gson.annotations.SerializedName;

// DTOs dùng để map với API .NET

public class ApiModels {

    public static class RegisterRequest {
        @SerializedName("email")
        public String email;

        @SerializedName("password")
        public String password;

        @SerializedName("fullName")
        public String fullName;

        public RegisterRequest(String email, String password, String fullName) {
            this.email = email;
            this.password = password;
            this.fullName = fullName;
        }
    }

    public static class LoginRequest {
        @SerializedName("email")
        public String email;

        @SerializedName("password")
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class UserResponse {
        @SerializedName("id")
        public int id;

        @SerializedName("email")
        public String email;

        @SerializedName("fullName")
        public String fullName;

        @SerializedName("createdAt")
        public String createdAt;
    }

    // Generic response giống ApiResponse<T> bên .NET
    public static class ApiResponse<T> {
        @SerializedName("success")
        public boolean success;

        @SerializedName("message")
        public String message;

        @SerializedName("data")
        public T data;
    }
}


