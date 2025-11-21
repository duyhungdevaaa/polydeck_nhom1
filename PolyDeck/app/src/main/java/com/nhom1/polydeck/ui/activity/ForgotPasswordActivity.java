package com.nhom1.polydeck.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nhom1.polydeck.R;
import com.google.gson.Gson;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.ApiResponse;
import com.nhom1.polydeck.data.model.ForgotPasswordRequest;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private MaterialButton sendLinkButton;
    private TextView backToLogin;
    private ProgressBar progressBar;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupAPI();
        setupClickListeners();
    }

    private void initViews() {
        inputEmail = findViewById(R.id.inputEmailForgot);
        sendLinkButton = findViewById(R.id.sendLinkButton);
        backToLogin = findViewById(R.id.backToLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupClickListeners() {
        // Gửi link reset password
        sendLinkButton.setOnClickListener(v -> handleSendResetLink());

        // Quay lại màn hình đăng nhập
        backToLogin.setOnClickListener(v -> finish());
    }

    private void handleSendResetLink() {
        String email = inputEmail.getText().toString().trim();

        // Validate email
        if (email.isEmpty()) {
            inputEmail.setError("Vui lòng nhập email");
            inputEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            inputEmail.setError("Email không hợp lệ");
            inputEmail.requestFocus();
            return;
        }

        // TODO: Call API to send reset password link
        sendResetLink(email);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendResetLink(String email) {
        showLoading(true);

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        Call<ApiResponse<Void>> call = apiService.forgotPassword(request);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        // Quay lại màn hình đăng nhập sau khi gửi thành công
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Parse error body để lấy message từ server
                    String errorMessage = "Không thể gửi yêu cầu";
                    if (response.errorBody() != null) {
                        try {
                            Gson gson = new Gson();
                            ApiResponse<?> errorResponse = gson.fromJson(
                                response.errorBody().string(), 
                                ApiResponse.class
                            );
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMessage = errorResponse.getMessage();
                            } else {
                                errorMessage = "Lỗi: " + response.code();
                            }
                        } catch (IOException e) {
                            errorMessage = "Lỗi: " + response.code();
                        } catch (Exception e) {
                            errorMessage = "Lỗi kết nối";
                        }
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                String errorMsg = "Lỗi kết nối";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("Failed to connect") || 
                        t.getMessage().contains("Unable to resolve host")) {
                        errorMsg = "Không thể kết nối đến server. Vui lòng kiểm tra:\n- Server đã chạy chưa?\n- IP/URL đúng chưa?";
                    } else {
                        errorMsg = "Lỗi: " + t.getMessage();
                    }
                }
                Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        sendLinkButton.setEnabled(!show);
    }
}