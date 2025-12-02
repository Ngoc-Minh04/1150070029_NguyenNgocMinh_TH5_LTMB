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

public class M001RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText edtEmail, edtPass, edtRepass, edtFullName;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.m001_frg_register, container, false);
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
        edtRepass = v.findViewById(R.id.edt_re_pass);
        // Nếu layout có ô nhập fullname, map ở đây (nếu không có có thể bỏ dòng này)
        int fullNameId = getResources().getIdentifier("edt_full_name", "id", requireContext().getPackageName());
        if (fullNameId != 0) {
            edtFullName = v.findViewById(fullNameId);
        }

        v.findViewById(R.id.tv_register).setOnClickListener(this);
        v.findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(mContext,
                androidx.appcompat.R.anim.abc_fade_in));

        if (v.getId() == R.id.iv_back) {
            gotoLoginScreen();

        } else if (v.getId() == R.id.tv_register) {
            String fullName = edtFullName != null ? edtFullName.getText().toString() : "";
            register(
                    edtEmail.getText().toString(),
                    edtPass.getText().toString(),
                    edtRepass.getText().toString(),
                    fullName
            );
        }
    }

    private void gotoLoginScreen() {
        ((MainActivity) mContext).gotoLoginScreen();
    }

    private void register(String mail, String pass, String repass, String fullName) {
        if (mail.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
            Toast.makeText(mContext, "Empty value", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(repass)) {
            Toast.makeText(mContext, "Password is not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService service = ApiClient.getApiService();
        ApiModels.RegisterRequest request = new ApiModels.RegisterRequest(mail, pass, fullName);

        service.register(request).enqueue(new Callback<ApiModels.ApiResponse<ApiModels.UserResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.UserResponse>> call, Response<ApiModels.ApiResponse<ApiModels.UserResponse>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ApiModels.ApiResponse<ApiModels.UserResponse> body = response.body();
                    Toast.makeText(mContext, body.message != null ? body.message : "Register success", Toast.LENGTH_SHORT).show();
                    if (body.success) {
                        gotoLoginScreen();
                    }
                } else {
                    Toast.makeText(mContext, "Register failed: " + response.code(), Toast.LENGTH_SHORT).show();
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