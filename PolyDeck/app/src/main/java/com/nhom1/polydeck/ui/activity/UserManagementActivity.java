package com.nhom1.polydeck.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.User;
import com.nhom1.polydeck.ui.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private ImageView btnBack;
    private EditText etSearch;
    private TextView tvTotalCount, tvActiveCount, tvBlockedCount;
    private RecyclerView rvUsers;
    private ProgressBar progressBar;

    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private List<User> filteredList = new ArrayList<>();

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        initViews();
        setupRecyclerView();
        setupAPI();
        loadUsers();
        setupSearchListener();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        tvBlockedCount = findViewById(R.id.tvBlockedCount);
        rvUsers = findViewById(R.id.rvUsers);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(this, filteredList, this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void loadUsers() {
        showLoading(true);

        apiService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());

                    filteredList.clear();
                    filteredList.addAll(userList);

                    userAdapter.notifyDataSetChanged();
                    updateStats();
                } else {
                    Toast.makeText(UserManagementActivity.this,
                            "Không thể tải danh sách người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(UserManagementActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStats() {
        int total = userList.size();
        int active = 0;
        int blocked = 0;

        for (User user : userList) {
            if (user.isActive()) {
                active++;
            } else {
                blocked++;
            }
        }

        tvTotalCount.setText(String.format("%,d", total));
        tvActiveCount.setText(String.format("%,d", active));
        tvBlockedCount.setText(String.format("%,d", blocked));
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterUsers(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (User user : userList) {
                if (user.getHoTen().toLowerCase().contains(searchQuery) ||
                        user.getEmail().toLowerCase().contains(searchQuery)) {
                    filteredList.add(user);
                }
            }
        }

        userAdapter.notifyDataSetChanged();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDetailClick(User user) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user_id", user.getId());
        startActivity(intent);
    }

    @Override
    public void onBlockClick(User user) {
        if (user.isActive()) {
            blockUser(user);
        } else {
            unblockUser(user);
        }
    }

    private void blockUser(User user) {
        apiService.blockUser(user.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserManagementActivity.this,
                            "Đã khóa tài khoản", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Reload danh sách
                } else {
                    Toast.makeText(UserManagementActivity.this,
                            "Không thể khóa tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unblockUser(User user) {
        apiService.unblockUser(user.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserManagementActivity.this,
                            "Đã mở khóa tài khoản", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Reload danh sách
                } else {
                    Toast.makeText(UserManagementActivity.this,
                            "Không thể mở khóa tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}