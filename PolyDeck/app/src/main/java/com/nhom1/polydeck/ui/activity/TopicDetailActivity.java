package com.nhom1.polydeck.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.TuVung;
import com.nhom1.polydeck.ui.adapter.VocabularyAdapter;
import com.nhom1.polydeck.utils.LearningStatusManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DECK_ID = "EXTRA_DECK_ID";
    public static final String EXTRA_DECK_NAME = "EXTRA_DECK_NAME";

    private APIService apiService;
    private VocabularyAdapter vocabAdapter;
    private TextView tvProgressPercent, tvCounts, tvUnknownCount;
    private ProgressBar progressXp;
    private LearningStatusManager learningStatusManager;

    private String deckId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        deckId = getIntent().getStringExtra(EXTRA_DECK_ID);
        String deckName = getIntent().getStringExtra(EXTRA_DECK_NAME);
        if (deckId == null) deckId = "";
        if (deckName == null) deckName = "";

        apiService = RetrofitClient.getApiService();
        learningStatusManager = new LearningStatusManager(this);

        TextView tvTitle = findViewById(R.id.tv_title);
        tvCounts = findViewById(R.id.tv_counts);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvUnknownCount = findViewById(R.id.tv_unknown_count);
        progressXp = findViewById(R.id.progress_xp);
        ImageButton btnBack = findViewById(R.id.btn_back);
        tvTitle.setText(deckName);
        btnBack.setOnClickListener(v -> onBackPressed());

        View btnFlashcard = findViewById(R.id.btn_flashcard);
        View btnQuiz = findViewById(R.id.btn_quiz);
        TextView btnManageStatus = findViewById(R.id.btn_manage_status);
        RecyclerView rv = findViewById(R.id.rv_preview_vocab);
        rv.setLayoutManager(new LinearLayoutManager(this));
        vocabAdapter = new VocabularyAdapter(new ArrayList<>(), this);
        rv.setAdapter(vocabAdapter);

        String finalDeckId = deckId;
        String finalDeckName = deckName;
        btnFlashcard.setOnClickListener(v -> {
            showFlashcardModeDialog(finalDeckId, finalDeckName);
        });

        btnQuiz.setOnClickListener(v -> {
            Intent i = new Intent(this, QuizActivity.class);
            i.putExtra(QuizActivity.EXTRA_DECK_ID, finalDeckId);
            i.putExtra(QuizActivity.EXTRA_DECK_NAME, finalDeckName);
            startActivity(i);
        });

        btnManageStatus.setOnClickListener(v -> {
            Intent i = new Intent(this, LearningStatusActivity.class);
            i.putExtra(LearningStatusActivity.EXTRA_DECK_ID, finalDeckId);
            i.putExtra(LearningStatusActivity.EXTRA_DECK_NAME, finalDeckName);
            startActivity(i);
        });

        loadPreview(deckId);
        updateUnknownCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh vocabulary list when returning from AddVocabularyActivity
        if (deckId != null && !deckId.isEmpty()) {
            loadPreview(deckId);
            updateUnknownCount();
        }
    }
    
    private void updateUnknownCount() {
        if (deckId == null || deckId.isEmpty() || tvUnknownCount == null) return;
        int unknownCount = learningStatusManager.getUnknownCount(deckId);
        if (unknownCount > 0) {
            tvUnknownCount.setText(unknownCount + " từ chưa nhớ");
            tvUnknownCount.setVisibility(View.VISIBLE);
        } else {
            tvUnknownCount.setVisibility(View.GONE);
        }
    }

    private void loadPreview(String deckId) {
        apiService.getTuVungByBoTu(deckId).enqueue(new Callback<List<TuVung>>() {
            @Override
            public void onResponse(@NonNull Call<List<TuVung>> call, @NonNull Response<List<TuVung>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TuVung> all = response.body();
                    List<TuVung> preview = all.size() > 5 ? all.subList(0, 5) : all;
                    vocabAdapter.updateData(preview);

                    // Update counts/progress roughly based on loaded data (placeholder logic)
                    int total = all.size();
                    int learned = Math.max(0, Math.min(total, total / 2));
                    if (tvCounts != null) {
                        tvCounts.setText(total + " từ • " + learned + " đã học");
                    }
                    int percent = total == 0 ? 0 : Math.round(learned * 100f / total);
                    if (tvProgressPercent != null) tvProgressPercent.setText(percent + "%");
                    if (progressXp != null) progressXp.setProgress(percent);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<TuVung>> call, @NonNull Throwable t) { }
        });
    }

    private void showFlashcardModeDialog(String deckId, String deckName) {
        int unknownCount = learningStatusManager.getUnknownCount(deckId);
        
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_flashcard_mode, null);
        
        CardView cardAll = dialogView.findViewById(R.id.card_all);
        CardView cardReview = dialogView.findViewById(R.id.card_review);
        TextView tvReviewTitle = dialogView.findViewById(R.id.tv_review_title);
        TextView tvReviewSubtitle = dialogView.findViewById(R.id.tv_review_subtitle);
        
        // Hiển thị card review nếu có từ chưa nhớ
        if (unknownCount > 0) {
            cardReview.setVisibility(View.VISIBLE);
            tvReviewTitle.setText("Học lại từ chưa nhớ (" + unknownCount + " từ)");
            tvReviewSubtitle.setText("Chỉ học lại các từ bạn chưa nhớ");
        } else {
            cardReview.setVisibility(View.GONE);
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);
        
        AlertDialog dialog = builder.create();
        
        // Click vào "Học tất cả"
        cardAll.setOnClickListener(v -> {
            Intent i = new Intent(this, FlashcardActivity.class);
            i.putExtra(FlashcardActivity.EXTRA_DECK_ID, deckId);
            i.putExtra(FlashcardActivity.EXTRA_DECK_NAME, deckName);
            i.putExtra(FlashcardActivity.EXTRA_REVIEW_UNKNOWN_ONLY, false);
            startActivity(i);
            dialog.dismiss();
        });
        
        // Click vào "Học lại từ chưa nhớ"
        cardReview.setOnClickListener(v -> {
            if (unknownCount > 0) {
                Intent i = new Intent(this, FlashcardActivity.class);
                i.putExtra(FlashcardActivity.EXTRA_DECK_ID, deckId);
                i.putExtra(FlashcardActivity.EXTRA_DECK_NAME, deckName);
                i.putExtra(FlashcardActivity.EXTRA_REVIEW_UNKNOWN_ONLY, true);
                startActivity(i);
                dialog.dismiss();
            }
        });
        
        dialog.show();
        
        // Làm tròn góc cho dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}




