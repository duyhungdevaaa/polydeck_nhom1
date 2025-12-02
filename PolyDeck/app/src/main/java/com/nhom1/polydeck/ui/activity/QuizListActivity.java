package com.nhom1.polydeck.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.BaiQuiz;
import com.nhom1.polydeck.data.model.BoTu;
import com.nhom1.polydeck.ui.adapter.QuizAdminAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvQuizList;
    private QuizAdminAdapter adapter;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        apiService = RetrofitClient.getApiService();

        initViews();
        setupToolbar();
        setupRecyclerView();

        fetchData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_quiz_list);
        rvQuizList = findViewById(R.id.rvQuizList);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        rvQuizList.setLayoutManager(new LinearLayoutManager(this));
        // Initially empty
        adapter = new QuizAdminAdapter(this, new ArrayList<>(), new HashMap<>());
        rvQuizList.setAdapter(adapter);
    }

    private void fetchData() {
        // We need to fetch both all quizzes and all decks (to map deck ID to deck name)
        Call<List<BaiQuiz>> quizzesCall = apiService.getAllQuizzes(); // This method needs to be created
        Call<List<BoTu>> decksCall = apiService.getAllChuDe();

        // Fetch all decks first
        decksCall.enqueue(new Callback<List<BoTu>>() {
            @Override
            public void onResponse(Call<List<BoTu>> call, Response<List<BoTu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, BoTu> deckMap = response.body().stream()
                            .collect(Collectors.toMap(BoTu::getId, deck -> deck));

                    // Now fetch all quizzes
                    quizzesCall.enqueue(new Callback<List<BaiQuiz>>() {
                        @Override
                        public void onResponse(Call<List<BaiQuiz>> call, Response<List<BaiQuiz>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                adapter.updateData(response.body(), deckMap);
                            } else {
                                Toast.makeText(QuizListActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<BaiQuiz>> call, Throwable t) {
                            Toast.makeText(QuizListActivity.this, "Error loading quizzes: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                     Toast.makeText(QuizListActivity.this, "Failed to load decks for quiz mapping", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BoTu>> call, Throwable t) {
                 Toast.makeText(QuizListActivity.this, "Error loading decks: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
