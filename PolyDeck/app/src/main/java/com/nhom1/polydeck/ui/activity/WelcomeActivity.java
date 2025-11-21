package com.nhom1.polydeck.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.utils.SessionManager;

public class WelcomeActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView imgLogo = findViewById(R.id.imgLogo);

        imgLogo.setScaleX(0.5f);
        imgLogo.setScaleY(0.5f);

        imgLogo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator())
                .start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);
            
            // Kiểm tra đã đăng nhập chưa
            if (sessionManager.isLoggedIn()) {
                // Đã đăng nhập - chuyển đến màn hình chính
                String vaiTro = sessionManager.getVaiTro();
                Intent intent;
                if (vaiTro != null && vaiTro.equals("admin")) {
                    intent = new Intent(WelcomeActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                }
                startActivity(intent);
            } else {
                // Chưa đăng nhập - chuyển đến màn hình đăng nhập
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            finish();
        }, SPLASH_DELAY);
    }
}
