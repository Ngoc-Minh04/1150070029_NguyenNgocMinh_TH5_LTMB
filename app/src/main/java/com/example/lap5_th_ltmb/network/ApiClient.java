package com.example.lap5_th_ltmb.network;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // TODO: chỉnh BASE_URL cho đúng với môi trường của bạn
    // Nếu chạy emulator Android Studio và API chạy trên http://localhost:5174
    // thì dùng: "http://10.0.2.2:5174/"
    // Nếu dùng điện thoại thật cùng mạng LAN, đổi 10.0.2.2 thành IP máy tính, ví dụ: "http://192.168.1.10:5174/"
    private static final String BASE_URL = "http://10.0.2.2:5174/";

    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d("API_LOG", message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}


