package com.nhom1.polydeck.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
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
import com.nhom1.polydeck.data.model.RegisterRequest;
import com.nhom1.polydeck.data.model.RegisterResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputFullName, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton registerButton;
    private TextView loginNow;
    private ProgressBar progressBar;
    private APIService apiService;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupAPI();
        setupPasswordToggles();
        setupClickListeners();
    }

    private void initViews() {
        inputFullName = findViewById(R.id.inputFullName);
        inputEmail = findViewById(R.id.inputEmailRegister);
        inputPassword = findViewById(R.id.inputPasswordRegister);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        registerButton = findViewById(R.id.registerButton);
        loginNow = findViewById(R.id.loginNow);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupPasswordToggles() {
        // Toggle cho mật khẩu
        inputPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (inputPassword.getRight() -
                        inputPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    togglePasswordVisibility(inputPassword, true);
                    return true;
                }
            }
            return false;
        });

        // Toggle cho xác nhận mật khẩu
        inputConfirmPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (inputConfirmPassword.getRight() -
                        inputConfirmPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    togglePasswordVisibility(inputConfirmPassword, false);
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility(EditText editText, boolean isPassword) {
        if (isPassword) {
            if (isPasswordVisible) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                isPasswordVisible = false;
            } else {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on, 0);
                isPasswordVisible = true;
            }
        } else {
            if (isConfirmPasswordVisible) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                isConfirmPasswordVisible = false;
            } else {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on, 0);
                isConfirmPasswordVisible = true;
            }
        }
        editText.setSelection(editText.getText().length());
    }

    private void setupClickListeners() {
        // Đăng ký
        registerButton.setOnClickListener(v -> handleRegister());

        // Chuyển đến màn hình đăng nhập
        loginNow.setOnClickListener(v -> {
            finish(); // Quay lại LoginActivity
        });
    }

    private void handleRegister() {
        String fullName = inputFullName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // Validate họ tên
        if (fullName.isEmpty()) {
            inputFullName.setError("Vui lòng nhập họ và tên");
            inputFullName.requestFocus();
            return;
        }

        if (fullName.length() < 2) {
            inputFullName.setError("Họ tên phải có ít nhất 2 ký tự");
            inputFullName.requestFocus();
            return;
        }

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

        // Validate mật khẩu
        if (password.isEmpty()) {
            inputPassword.setError("Vui lòng nhập mật khẩu");
            inputPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            inputPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            inputPassword.requestFocus();
            return;
        }

        // Validate xác nhận mật khẩu
        if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            inputConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            inputConfirmPassword.requestFocus();
            return;
        }

        // TODO: Implement actual registration logic with API
        performRegister(fullName, email, password);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performRegister(String fullName, String email, String password) {
        showLoading(true);

        RegisterRequest request = new RegisterRequest(fullName, email, password);

        Call<ApiResponse<RegisterResponse>> call = apiService.register(request);
        call.enqueue(new Callback<ApiResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RegisterResponse>> call, Response<ApiResponse<RegisterResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<RegisterResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Hiển thị thông báo dài hơn vì có hướng dẫn kích hoạt email
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        // Navigate back to login
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Parse error body để lấy message từ server
                    String errorMessage = "Đăng ký thất bại";
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
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RegisterResponse>> call, Throwable t) {
                showLoading(false);
                String errorMsg = "Lỗi kết nối";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("Failed to connect") || t.getMessage().contains("Unable to resolve host")) {
                        errorMsg = "Không thể kết nối đến server. Vui lòng kiểm tra:\n- Server đã chạy chưa?\n- IP/URL đúng chưa?";
                    } else {
                        errorMsg = "Lỗi: " + t.getMessage();
                    }
                }
                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        registerButton.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear sensitive data
        if (inputPassword != null) {
            inputPassword.setText("");
        }
        if (inputConfirmPassword != null) {
            inputConfirmPassword.setText("");
        }
    }
}