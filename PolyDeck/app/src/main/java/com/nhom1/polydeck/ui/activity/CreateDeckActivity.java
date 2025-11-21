package com.nhom1.polydeck.ui.activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.BoTu;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateDeckActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etDeckName;
    private View color1, color2, color3, color4, color5, color6;
    private Button btnAddWords;

    private String selectedColor = "#7C3AED";
    private View selectedColorView;

    private APIService apiService;

    private static final String[] COLOR_VALUES = {
            "#7C3AED", "#10B981", "#FF6B9D",
            "#EF4444", "#3B82F6", "#8B5CF6"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);

        initViews();
        setupAPI();
        setupColorPicker();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etDeckName = findViewById(R.id.etDeckName);

        color1 = findViewById(R.id.color1);
        color2 = findViewById(R.id.color2);
        color3 = findViewById(R.id.color3);
        color4 = findViewById(R.id.color4);
        color5 = findViewById(R.id.color5);
        color6 = findViewById(R.id.color6);

        btnAddWords = findViewById(R.id.btnAddWords);

        btnBack.setOnClickListener(v -> finish());
        btnAddWords.setOnClickListener(v -> createDeck());

        // Chọn màu purple mặc định
        selectedColorView = color1;
        selectColor(color1, COLOR_VALUES[0]);
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void setupColorPicker() {
        View[] colorViews = {color1, color2, color3, color4, color5, color6};

        for (int i = 0; i < colorViews.length; i++) {
            final View colorView = colorViews[i];
            final String colorValue = COLOR_VALUES[i];

            colorView.setOnClickListener(v -> {
                if (selectedColorView != null) {
                    selectedColorView.setAlpha(1.0f);
                    selectedColorView.setScaleX(1.0f);
                    selectedColorView.setScaleY(1.0f);
                }

                selectColor(colorView, colorValue);
            });
        }
    }

    private void selectColor(View colorView, String colorValue) {
        selectedColorView = colorView;
        selectedColor = colorValue;

        colorView.setAlpha(0.8f);
        colorView.setScaleX(0.95f);
        colorView.setScaleY(0.95f);
    }

    private void createDeck() {
        String deckName = etDeckName.getText().toString().trim();

        if (deckName.isEmpty()) {
            etDeckName.setError("Vui lòng nhập tên bộ từ");
            return;
        }

        BoTu boTu = new BoTu();
        boTu.setTenChuDe(deckName);
        boTu.setLinkAnhIcon(selectedColor);

        apiService.createChuDe(boTu).enqueue(new Callback<BoTu>() {
            @Override
            public void onResponse(@NonNull Call<BoTu> call, @NonNull Response<BoTu> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateDeckActivity.this,
                            "Đã tạo bộ từ thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CreateDeckActivity.this, AddVocabularyActivity.class);
                    // FIX: Changed "deck_id" to "DECK_ID" to match the key expected by AddVocabularyActivity
                    intent.putExtra("DECK_ID", response.body().getId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateDeckActivity.this,
                            "Không thể tạo bộ từ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BoTu> call, @NonNull Throwable t) {
                Toast.makeText(CreateDeckActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
