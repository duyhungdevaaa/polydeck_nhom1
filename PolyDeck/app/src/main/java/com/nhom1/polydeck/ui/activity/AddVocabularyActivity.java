package com.nhom1.polydeck.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.TuVung;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVocabularyActivity extends AppCompatActivity {

    private static final int PICK_EXCEL_FILE = 1001;

    private ImageView btnBack;
    private CardView cardImportExcel;
    private EditText etWord, etPhonetic, etMeaning, etExample;
    private Button btnAddWord;

    private String deckId;
    private String deckName;
    private boolean viewOnly;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vocabulary);

        deckId = getIntent().getStringExtra("deck_id");
        deckName = getIntent().getStringExtra("deck_name");
        viewOnly = getIntent().getBooleanExtra("view_only", false);

        initViews();
        setupAPI();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardImportExcel = findViewById(R.id.cardImportExcel);
        etWord = findViewById(R.id.etWord);
        etPhonetic = findViewById(R.id.etPhonetic);
        etMeaning = findViewById(R.id.etMeaning);
        etExample = findViewById(R.id.etExample);
        btnAddWord = findViewById(R.id.btnAddWord);

        btnBack.setOnClickListener(v -> finish());

        cardImportExcel.setOnClickListener(v -> openFilePicker());

        btnAddWord.setOnClickListener(v -> addWordManually());

        if (viewOnly) {
            disableInputs();
        }
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Chọn file Excel"),
                    PICK_EXCEL_FILE
            );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Vui lòng cài đặt File Manager", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_EXCEL_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                importExcelFile(fileUri);
            }
        }
    }

    private void importExcelFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);


            Toast.makeText(this, "Đang import từ vựng...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addWordManually() {
        String word = etWord.getText().toString().trim();
        String phonetic = etPhonetic.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();
        String example = etExample.getText().toString().trim();

        if (word.isEmpty()) {
            etWord.setError("Vui lòng nhập từ vựng");
            return;
        }

        if (meaning.isEmpty()) {
            etMeaning.setError("Vui lòng nhập nghĩa");
            return;
        }

        TuVung tuVung = new TuVung();
        tuVung.setTuVung(word);
        tuVung.setPhienAm(phonetic);
        tuVung.setNghia(meaning);
        tuVung.setViDu(example);
        tuVung.setBoTuId(deckId);

        apiService.addTuVung(tuVung).enqueue(new Callback<TuVung>() {
            @Override
            public void onResponse(Call<TuVung> call, Response<TuVung> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddVocabularyActivity.this,
                            "Đã thêm từ vựng", Toast.LENGTH_SHORT).show();
                    clearInputs();
                } else {
                    Toast.makeText(AddVocabularyActivity.this,
                            "Không thể thêm từ vựng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TuVung> call, Throwable t) {
                Toast.makeText(AddVocabularyActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        etWord.setText("");
        etPhonetic.setText("");
        etMeaning.setText("");
        etExample.setText("");
        etWord.requestFocus();
    }

    private void disableInputs() {
        etWord.setEnabled(false);
        etPhonetic.setEnabled(false);
        etMeaning.setEnabled(false);
        etExample.setEnabled(false);
        btnAddWord.setEnabled(false);
        cardImportExcel.setEnabled(false);
    }
}