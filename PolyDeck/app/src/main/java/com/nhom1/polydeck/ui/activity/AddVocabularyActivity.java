package com.nhom1.polydeck.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.TuVung;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVocabularyActivity extends AppCompatActivity {

    private static final String TAG = "AddVocabularyActivity";
    private static final int PICK_EXCEL_FILE_REQUEST = 2;

    private Toolbar toolbar;
    private LinearLayout btnImportExcel;
    private TextInputEditText etEnglishWord, etPronunciation, etVietnameseMeaning, etExample;
    private Button btnAddVocabulary;
    private ImageView btnSaveAndExit;
    private ProgressBar importProgressBar;

    private APIService apiService;
    private String deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vocabulary);

        apiService = RetrofitClient.getApiService();

        deckId = getIntent().getStringExtra("DECK_ID");
        if (deckId == null || deckId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID bộ từ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_add_vocab);
        btnImportExcel = findViewById(R.id.btnImportExcel);
        etEnglishWord = findViewById(R.id.etEnglishWord);
        etPronunciation = findViewById(R.id.etPronunciation);
        etVietnameseMeaning = findViewById(R.id.etVietnameseMeaning);
        etExample = findViewById(R.id.etExample);
        btnAddVocabulary = findViewById(R.id.btnAddVocabulary);
        btnSaveAndExit = findViewById(R.id.btnSaveAndExit);
        // You need to add a ProgressBar to your activity_add_vocabulary.xml
        // importProgressBar = findViewById(R.id.importProgressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        btnAddVocabulary.setOnClickListener(v -> addVocabularyManually(false));
        btnImportExcel.setOnClickListener(v -> openFileChooser());
        btnSaveAndExit.setOnClickListener(v -> addVocabularyManually(true));
    }

    private void addVocabularyManually(boolean shouldExit) {
        // ... (existing manual add code, no changes needed)
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // .xlsx
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Chọn tệp Excel"), PICK_EXCEL_FILE_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Vui lòng cài đặt một trình quản lý tệp.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EXCEL_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            readExcelFile(uri);
        }
    }

    private void readExcelFile(Uri uri) {
        // showLoading(true);
        new Thread(() -> {
            List<TuVung> vocabList = new ArrayList<>();
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

                XSSFSheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                // Skip header row if it exists
                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Cell cell1 = row.getCell(0); // English word
                    Cell cell2 = row.getCell(1); // Vietnamese meaning
                    Cell cell3 = row.getCell(2); // Pronunciation (optional)

                    if (cell1 != null && cell2 != null) {
                        String english = cell1.getStringCellValue();
                        String vietnamese = cell2.getStringCellValue();
                        String pronunciation = (cell3 != null) ? cell3.getStringCellValue() : "";

                        if (!english.trim().isEmpty() && !vietnamese.trim().isEmpty()) {
                            TuVung vocab = new TuVung();
                            vocab.setTuTiengAnh(english);
                            vocab.setNghiaTiengViet(vietnamese);
                            vocab.setPhienAm(pronunciation);
                            vocabList.add(vocab);
                        }
                    }
                }

                if (!vocabList.isEmpty()) {
                    runOnUiThread(() -> uploadVocabList(vocabList));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy dữ liệu hợp lệ trong tệp Excel.", Toast.LENGTH_LONG).show();
                        // showLoading(false);
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error reading Excel file", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi đọc tệp Excel", Toast.LENGTH_SHORT).show();
                    // showLoading(false);
                });
            }
        }).start();
    }

    private void uploadVocabList(List<TuVung> vocabList) {
        apiService.importVocab(deckId, vocabList).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(AddVocabularyActivity.this, "Đã import thành công " + vocabList.size() + " từ vựng!", Toast.LENGTH_LONG).show();
                    finish(); // Close activity after successful import
                } else {
                    Toast.makeText(AddVocabularyActivity.this, "Import thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // showLoading(false);
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(AddVocabularyActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
