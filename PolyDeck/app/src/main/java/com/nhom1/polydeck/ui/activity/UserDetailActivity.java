package com.nhom1.polydeck.ui.activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvAvatar, tvUserNameTitle;
    private EditText etFullName, etEmail, etLevel, etXP, etJoinDate;
    private Button btnSave;

    private APIService apiService;
    private String userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userId = getIntent().getStringExtra("user_id");

        initViews();
        setupAPI();
        loadUserDetail();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvAvatar = findViewById(R.id.tvAvatar);
        tvUserNameTitle = findViewById(R.id.tvUserNameTitle);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etLevel = findViewById(R.id.etLevel);
        etXP = findViewById(R.id.etXP);
        etJoinDate = findViewById(R.id.etJoinDate);
        btnSave = findViewById(R.id.btnSave);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void loadUserDetail() {
        apiService.getUserDetail(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    displayUserInfo();
                } else {
                    Toast.makeText(UserDetailActivity.this,
                            "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserDetailActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayUserInfo() {
        tvAvatar.setText(currentUser.getInitials());
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor("#7C3AED"));
        tvAvatar.setBackground(drawable);

        tvUserNameTitle.setText(currentUser.getHoTen());
        etFullName.setText(currentUser.getHoTen());
        etEmail.setText(currentUser.getEmail());
        etLevel.setText(String.valueOf(currentUser.getLevel()));
        etXP.setText(String.valueOf(currentUser.getXp()));
        etJoinDate.setText(currentUser.getNgayThamGia());
    }

    private void saveChanges() {
        String hoTen = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String levelStr = etLevel.getText().toString().trim();
        String xpStr = etXP.getText().toString().trim();

        if (hoTen.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            return;
        }

        int level = levelStr.isEmpty() ? 0 : Integer.parseInt(levelStr);
        int xp = xpStr.isEmpty() ? 0 : Integer.parseInt(xpStr);

        currentUser.setHoTen(hoTen);
        currentUser.setEmail(email);
        currentUser.setLevel(level);
        currentUser.setXp(xp);

        apiService.updateUser(userId, currentUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserDetailActivity.this,
                            "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UserDetailActivity.this,
                            "Không thể lưu thay đổi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserDetailActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}