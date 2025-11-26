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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.Answer;
import com.nhom1.polydeck.data.model.BoTu;
import com.nhom1.polydeck.data.model.Question;
import com.nhom1.polydeck.data.model.Quiz;

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
            public void onResponse(@NonNull Call<List<BoTu>> call, @NonNull Response<List<BoTu>> response) {
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
            public void onFailure(@NonNull Call<List<BoTu>> call, @NonNull Throwable t) {
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

        List<Question> questions = new ArrayList<>();

        for (View questionView : questionViews) {
            EditText etQuestionText = questionView.findViewById(R.id.etQuestionText);
            RadioGroup rgAnswers = questionView.findViewById(R.id.rgAnswers);
            EditText etAnswer1 = questionView.findViewById(R.id.etAnswer1);
            EditText etAnswer2 = questionView.findViewById(R.id.etAnswer2);
            EditText etAnswer3 = questionView.findViewById(R.id.etAnswer3);
            EditText etAnswer4 = questionView.findViewById(R.id.etAnswer4);

            String questionText = etQuestionText.getText().toString().trim();
            if (questionText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ nội dung câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Answer> answers = new ArrayList<>();
            int checkedRadioButtonId = rgAnswers.getCheckedRadioButtonId();
            if (checkedRadioButtonId == -1) {
                Toast.makeText(this, "Vui lòng chọn một đáp án đúng cho mỗi câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }

            answers.add(new Answer(etAnswer1.getText().toString(), R.id.rbAnswer1 == checkedRadioButtonId));
            answers.add(new Answer(etAnswer2.getText().toString(), R.id.rbAnswer2 == checkedRadioButtonId));
            answers.add(new Answer(etAnswer3.getText().toString(), R.id.rbAnswer3 == checkedRadioButtonId));
            answers.add(new Answer(etAnswer4.getText().toString(), R.id.rbAnswer4 == checkedRadioButtonId));

            questions.add(new Question(questionText, answers));
        }

        if (questions.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất một câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        Quiz quiz = new Quiz(selectedDeck.getId(), questions);

        apiService.createQuiz(quiz).enqueue(new Callback<Quiz>() {
            @Override
            public void onResponse(@NonNull Call<Quiz> call, @NonNull Response<Quiz> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateQuizActivity.this, "Tạo quiz thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateQuizActivity.this, "Tạo quiz thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Quiz> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(CreateQuizActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
