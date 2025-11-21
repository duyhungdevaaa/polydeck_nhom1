package com.nhom1.polydeck.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailActivity";
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";

    private Toolbar toolbar;
    private CircleImageView ivDetailAvatar;
    private TextView tvDetailUserName;
    private EditText etDetailFullName, etDetailEmail, etDetailLevel, etDetailXp, etDetailJoinDate;
    private Button btnSaveChanges;

    private APIService apiService;
    private String userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userId = getIntent().getStringExtra(EXTRA_USER_ID);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();
        initViews();
        setupToolbar();
        fetchUserDetails();

        btnSaveChanges.setOnClickListener(v -> saveUserChanges());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_user_detail);
        ivDetailAvatar = findViewById(R.id.ivDetailAvatar);
        tvDetailUserName = findViewById(R.id.tvDetailUserName);
        etDetailFullName = findViewById(R.id.etDetailFullName);
        etDetailEmail = findViewById(R.id.etDetailEmail);
        etDetailLevel = findViewById(R.id.etDetailLevel);
        etDetailXp = findViewById(R.id.etDetailXp);
        etDetailJoinDate = findViewById(R.id.etDetailJoinDate);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void fetchUserDetails() {
        apiService.getUserDetail(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    populateUserData(currentUser);
                } else {
                    Toast.makeText(UserDetailActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "API Error: " + t.getMessage());
                Toast.makeText(UserDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUserData(User user) {
        Glide.with(this)
                .load(user.getLinkAnhDaiDien())
                .error(R.drawable.circle_purple) // Fallback image
                .into(ivDetailAvatar);

        tvDetailUserName.setText(user.getHoTen());
        etDetailFullName.setText(user.getHoTen());
        etDetailEmail.setText(user.getEmail());
        etDetailLevel.setText(String.valueOf(user.getLevel()));
        etDetailXp.setText(String.valueOf(user.getXp()));
        etDetailJoinDate.setText(user.getNgayThamGia());
    }

    private void saveUserChanges() {
        if (currentUser == null) return;

        // Update user object from EditText fields
        currentUser.setHoTen(etDetailFullName.getText().toString());
        currentUser.setEmail(etDetailEmail.getText().toString());
        currentUser.setLevel(Integer.parseInt(etDetailLevel.getText().toString()));
        currentUser.setXp(Integer.parseInt(etDetailXp.getText().toString()));

        apiService.updateUser(userId, currentUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserDetailActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the list
                } else {
                    Toast.makeText(UserDetailActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "API Error: " + t.getMessage());
                Toast.makeText(UserDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
