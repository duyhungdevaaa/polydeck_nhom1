package com.nhom1.polydeck.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nhom1.polydeck.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.ApiResponse;
import com.nhom1.polydeck.data.model.GoogleLoginRequest;
import com.nhom1.polydeck.data.model.LoginRequest;
import com.nhom1.polydeck.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton loginButton;
    private TextView registerNow, forgotPassword;
    private LinearLayout googleButton;
    private ProgressBar progressBar;
    private APIService apiService;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupAPI();
        setupGoogleSignIn();
        setupPasswordToggle();
        setupClickListeners();
    }

    private void initViews() {
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.loginButton);
        registerNow = findViewById(R.id.registerNow);
        forgotPassword = findViewById(R.id.forgotPasswordView);
        googleButton = findViewById(R.id.googleButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("458654847284-06v4cluss9i716rhg3o8e1vmgbhvout1.apps.googleusercontent.com") // Web Client ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupPasswordToggle() {
        inputPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (inputPassword.getRight() -
                        inputPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
            isPasswordVisible = false;
        } else {
            inputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            inputPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on, 0);
            isPasswordVisible = true;
        }
        inputPassword.setSelection(inputPassword.getText().length());
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        googleButton.setOnClickListener(v -> handleGoogleSignIn());
    }

    private void handleLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

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
        if (password.isEmpty()) {
            inputPassword.setError("Vui lòng nhập mật khẩu");
            inputPassword.requestFocus();
            return;
        }

        performLogin(email, password);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performLogin(String email, String password) {
        showLoading(true);

        LoginRequest request = new LoginRequest(email, password);

        Call<ApiResponse<LoginResponse>> call = apiService.login(request);
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        LoginResponse loginData = apiResponse.getData();
                        if (loginData != null) {
                            Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            Intent intent;
                            // FIX: Get role directly from LoginResponse
                            String vaiTro = loginData.getVaiTro();

                            if (vaiTro != null && vaiTro.equals("admin")) {
                                // Admin → Admin Panel
                                intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                            } else {
                                // Student/User → Main Activity
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                             Toast.makeText(LoginActivity.this, "Dữ liệu đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Đăng nhập thất bại. Mã lỗi: " + response.code();
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        loginButton.setEnabled(!show);
    }

    private void handleGoogleSignIn() {
        if (googleSignInClient == null) {
            Toast.makeText(this, "Google Sign-In chưa được cấu hình.", Toast.LENGTH_LONG).show();
            return;
        }
        
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    performGoogleLogin(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void performGoogleLogin(String idToken) {
        showLoading(true);

        GoogleLoginRequest request = new GoogleLoginRequest(idToken);

        Call<ApiResponse<LoginResponse>> call = apiService.googleLogin(request);
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        LoginResponse loginData = apiResponse.getData();
                        if (loginData != null) {
                            Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            
                            Intent intent;
                             // FIX: Get role directly from LoginResponse
                            String vaiTro = loginData.getVaiTro();

                            if (vaiTro != null && vaiTro.equals("admin")) {
                                // Admin → Admin Panel
                                intent = new Intent(LoginActivity.this, AdminPanelActivity.class);
                            } else {
                                // Student/User → Main Activity
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Dữ liệu đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Đăng nhập Google thất bại. Mã lỗi: " + response.code();
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (inputPassword != null) {
            inputPassword.setText("");
        }
    }
}
