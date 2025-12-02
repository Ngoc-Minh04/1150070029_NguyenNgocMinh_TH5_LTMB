package com.example.lap5_th_ltmb;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.lap5_th_ltmb.network.ApiClient;
import com.example.lap5_th_ltmb.network.ApiModels;
import com.example.lap5_th_ltmb.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class M000LoginFragment extends Fragment implements View.OnClickListener {

    private EditText edtEmail, edtPass;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.m000_frg_login, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initView(View v) {
        edtEmail = v.findViewById(R.id.edt_email);
        edtPass = v.findViewById(R.id.edt_pass);

        v.findViewById(R.id.tv_login).setOnClickListener(this);
        v.findViewById(R.id.tv_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(
                mContext,
                androidx.appcompat.R.anim.abc_fade_in
        ));

        if (v.getId() == R.id.tv_login) {
            login(
                    edtEmail.getText().toString(),
                    edtPass.getText().toString()
            );

        } else if (v.getId() == R.id.tv_register) {
            gotoRegisterScreen();
        }
    }

    private void gotoRegisterScreen() {
        ((MainActivity) mContext).gotoRegisterScreen();
    }

    private void login(String mail, String pass) {
        if (mail.isEmpty() || pass.isEmpty()) {
            Toast.makeText(mContext, "Empty value", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService service = ApiClient.getApiService();
        ApiModels.LoginRequest request = new ApiModels.LoginRequest(mail, pass);

        service.login(request).enqueue(new Callback<ApiModels.ApiResponse<ApiModels.UserResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.UserResponse>> call, Response<ApiModels.ApiResponse<ApiModels.UserResponse>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ApiModels.ApiResponse<ApiModels.UserResponse> body = response.body();
                    Toast.makeText(mContext, body.message != null ? body.message : "Login success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.UserResponse>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(mContext, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}