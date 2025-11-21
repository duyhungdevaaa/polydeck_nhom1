package com.nhom1.polydeck.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.AdminStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalDecks, tvActiveUsers, tvTotalWords;
    private TextView tvUserGrowth, tvDeckGrowth, tvActiveGrowth, tvWordGrowth;

    private CardView cardUsers, cardDecks, cardQuiz, cardNotification, cardSupport;

    // Logout button
    private ImageView btnLogout;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupAPI();
        loadStats();
        setupClickListeners();
    }

    private void initViews() {
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalDecks = findViewById(R.id.tvTotalDecks);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvTotalWords = findViewById(R.id.tvTotalWords);

        tvUserGrowth = findViewById(R.id.tvUserGrowth);
        tvDeckGrowth = findViewById(R.id.tvDeckGrowth);
        tvActiveGrowth = findViewById(R.id.tvActiveGrowth);
        tvWordGrowth = findViewById(R.id.tvWordGrowth);

        cardUsers = findViewById(R.id.cardUsers);
        cardDecks = findViewById(R.id.cardDecks);
        cardQuiz = findViewById(R.id.cardQuiz);
        cardNotification = findViewById(R.id.cardNotification);
        cardSupport = findViewById(R.id.cardSupport);

        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void loadStats() {
        apiService.getAdminStats().enqueue(new Callback<AdminStats>() {
            @Override
            public void onResponse(Call<AdminStats> call, Response<AdminStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdminStats stats = response.body();
                    updateStatsUI(stats);
                } else {
                    Toast.makeText(AdminDashboardActivity.this,
                            "Không thể tải thống kê", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminStats> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatsUI(AdminStats stats) {
        tvTotalUsers.setText(String.format("%,d", stats.getTongNguoiDung()));
        tvTotalDecks.setText(String.format("%,d", stats.getTongBoTu()));
        tvActiveUsers.setText(String.format("%,d", stats.getNguoiHoatDong()));
        tvTotalWords.setText(String.format("%,d", stats.getTongTuVung()));

        tvUserGrowth.setText(stats.getTyLeNguoiDung());
        tvDeckGrowth.setText(stats.getTyLeBoTu());
        tvActiveGrowth.setText(stats.getTyLeHoatDong());
        tvWordGrowth.setText(stats.getTyLeTuVung());
    }

    private void setupClickListeners() {
        cardUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        cardDecks.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, DeckManagementActivity.class);
            startActivity(intent);
        });


        btnLogout.setOnClickListener(v -> {
            finish();
        });
    }
}