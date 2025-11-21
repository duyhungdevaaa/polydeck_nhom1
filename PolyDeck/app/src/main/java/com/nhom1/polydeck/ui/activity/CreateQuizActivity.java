package com.nhom1.polydeck.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.BoTu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateQuizActivity extends AppCompatActivity {

    private static final String TAG = "CreateQuizActivity";

    private Toolbar toolbar;
    private Spinner spinnerDecks;
    private LinearLayout containerQuestions;
    private Button btnAddQuestion, btnSaveQuiz, btnCancelQuiz;

    private APIService apiService;
    private List<BoTu> deckList = new ArrayList<>();
    private List<View> questionViews = new ArrayList<>();
    private BoTu selectedDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        apiService = RetrofitClient.getApiService();

        initViews();
        setupToolbar();
        setupListeners();

        fetchDecksForSpinner();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_create_quiz);
        spinnerDecks = findViewById(R.id.spinnerDecks);
        containerQuestions = findViewById(R.id.containerQuestions);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnSaveQuiz = findViewById(R.id.btnSaveQuiz);
        btnCancelQuiz = findViewById(R.id.btnCancelQuiz);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        btnAddQuestion.setOnClickListener(v -> addQuestionView());
        btnCancelQuiz.setOnClickListener(v -> finish());
        btnSaveQuiz.setOnClickListener(v -> saveQuiz());
    }

    private void fetchDecksForSpinner() {
        apiService.getAllChuDe().enqueue(new Callback<List<BoTu>>() {
            @Override
            public void onResponse(Call<List<BoTu>> call, Response<List<BoTu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deckList = response.body();
                    List<String> deckNames = deckList.stream().map(BoTu::getTenChuDe).collect(Collectors.toList());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateQuizActivity.this, android.R.layout.simple_spinner_item, deckNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDecks.setAdapter(adapter);
                    spinnerDecks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDeck = deckList.get(position);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedDeck = null;
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<BoTu>> call, Throwable t) {
                Toast.makeText(CreateQuizActivity.this, "Không thể tải danh sách bộ từ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addQuestionView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View questionView = inflater.inflate(R.layout.item_quiz_question, containerQuestions, false);

        TextView tvQuestionNumber = questionView.findViewById(R.id.tvQuestionNumber);
        tvQuestionNumber.setText("Câu " + (questionViews.size() + 1));

        View btnDeleteQuestion = questionView.findViewById(R.id.btnDeleteQuestion);
        btnDeleteQuestion.setOnClickListener(v -> removeQuestionView(questionView));
        
        questionViews.add(questionView);
        containerQuestions.addView(questionView);
    }

    private void removeQuestionView(View questionView) {
        containerQuestions.removeView(questionView);
        questionViews.remove(questionView);
        // Update question numbers
        for (int i = 0; i < questionViews.size(); i++) {
            View view = questionViews.get(i);
            TextView tvQuestionNumber = view.findViewById(R.id.tvQuestionNumber);
            tvQuestionNumber.setText("Câu " + (i + 1));
        }
    }

    private void saveQuiz() {
        if (selectedDeck == null) {
            Toast.makeText(this, "Vui lòng chọn một bộ từ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (questionViews.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất một câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(this, "Chức năng Lưu Quiz đang được phát triển.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Selected Deck ID: " + selectedDeck.getId());
    }
}
