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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.api.APIService;
import com.nhom1.polydeck.data.api.RetrofitClient;
import com.nhom1.polydeck.data.model.BoTu;
import com.nhom1.polydeck.ui.adapter.DeckAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeckManagementActivity extends AppCompatActivity implements DeckAdapter.OnDeckClickListener {

    private ImageView btnBack, btnAdd;
    private EditText etSearch;
    private TextView tvTotalDecks, tvPublished;
    private RecyclerView rvDecks;
    private ProgressBar progressBar;

    private DeckAdapter deckAdapter;
    private List<BoTu> deckList = new ArrayList<>();
    private List<BoTu> filteredList = new ArrayList<>();

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_management);

        initViews();
        setupRecyclerView();
        setupAPI();
        loadDecks();
        setupSearchListener();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);
        etSearch = findViewById(R.id.etSearch);
        tvTotalDecks = findViewById(R.id.tvTotalDecks);
        tvPublished = findViewById(R.id.tvPublished);
        rvDecks = findViewById(R.id.rvDecks);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(DeckManagementActivity.this, CreateDeckActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        deckAdapter = new DeckAdapter(this, filteredList, this);
        rvDecks.setLayoutManager(new LinearLayoutManager(this));
        rvDecks.setAdapter(deckAdapter);
    }

    private void setupAPI() {
        apiService = RetrofitClient.getApiService();
    }

    private void loadDecks() {
        showLoading(true);

        apiService.getAllBoTu().enqueue(new Callback<List<BoTu>>() {
            @Override
            public void onResponse(Call<List<BoTu>> call, Response<List<BoTu>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    deckList.clear();
                    deckList.addAll(response.body());

                    filteredList.clear();
                    filteredList.addAll(deckList);

                    deckAdapter.notifyDataSetChanged();
                    updateStats();
                } else {
                    Toast.makeText(DeckManagementActivity.this,
                            "Không thể tải danh sách bộ từ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BoTu>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(DeckManagementActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStats() {
        int total = deckList.size();
        int published = 0;

        for (BoTu deck : deckList) {
            if ("Đã xuất bản".equals(deck.getTrangThai())) {
                published++;
            }
        }

        tvTotalDecks.setText(String.format("%,d", total));
        tvPublished.setText(String.format("%,d", published));
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDecks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterDecks(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(deckList);
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (BoTu deck : deckList) {
                if (deck.getTenBoTu().toLowerCase().contains(searchQuery)) {
                    filteredList.add(deck);
                }
            }
        }

        deckAdapter.notifyDataSetChanged();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvDecks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onViewClick(BoTu deck) {
        Intent intent = new Intent(this, AddVocabularyActivity.class);
        intent.putExtra("deck_id", deck.getId());
        intent.putExtra("deck_name", deck.getTenBoTu());
        intent.putExtra("view_only", true);
        startActivity(intent);
    }

    @Override
    public void onEditClick(BoTu deck) {
        Intent intent = new Intent(this, AddVocabularyActivity.class);
        intent.putExtra("deck_id", deck.getId());
        intent.putExtra("deck_name", deck.getTenBoTu());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(BoTu deck) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bộ từ \"" + deck.getTenBoTu() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteDeck(deck))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteDeck(BoTu deck) {
        apiService.deleteBoTu(deck.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DeckManagementActivity.this,
                            "Đã xóa bộ từ", Toast.LENGTH_SHORT).show();
                    loadDecks();
                } else {
                    Toast.makeText(DeckManagementActivity.this,
                            "Không thể xóa bộ từ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DeckManagementActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDecks();
    }
}