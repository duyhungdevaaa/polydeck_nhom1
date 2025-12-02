package com.nhom1.polydeck.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.model.BaiQuiz;
import com.nhom1.polydeck.data.model.BoTu;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuizAdminAdapter extends RecyclerView.Adapter<QuizAdminAdapter.QuizAdminViewHolder> {

    private List<BaiQuiz> quizList;
    private Map<String, BoTu> deckMap; // To show deck names
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public QuizAdminAdapter(Context context, List<BaiQuiz> quizList, Map<String, BoTu> deckMap) {
        this.context = context;
        this.quizList = quizList;
        this.deckMap = deckMap;
    }

    @NonNull
    @Override
    public QuizAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_admin, parent, false);
        return new QuizAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAdminViewHolder holder, int position) {
        BaiQuiz quiz = quizList.get(position);
        if (quiz == null) return;

        BoTu deck = deckMap.get(quiz.getMaChuDe());
        String deckName = (deck != null) ? deck.getTenChuDe() : "Unknown Deck";
        holder.tvQuizDeckName.setText("Chủ đề: " + deckName);

        String info = String.format(Locale.getDefault(), "%d câu hỏi • Ngày tạo: %s",
                quiz.getQuestions().size(),
                quiz.getCreatedAt() != null ? sdf.format(quiz.getCreatedAt()) : "N/A");
        holder.tvQuizInfo.setText(info);

        holder.btnViewQuiz.setOnClickListener(v -> {
            // TODO: Implement navigation to an EditQuizActivity
            Toast.makeText(context, "Chức năng Xem/Sửa đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        holder.btnDeleteQuiz.setOnClickListener(v -> {
            // TODO: Implement delete logic with confirmation
            Toast.makeText(context, "Chức năng Xóa đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return quizList != null ? quizList.size() : 0;
    }

    public void updateData(List<BaiQuiz> newQuizList, Map<String, BoTu> newDeckMap) {
        this.quizList.clear();
        this.quizList.addAll(newQuizList);
        this.deckMap.clear();
        this.deckMap.putAll(newDeckMap);
        notifyDataSetChanged();
    }

    static class QuizAdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizDeckName, tvQuizInfo;
        Button btnViewQuiz, btnDeleteQuiz;

        public QuizAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizDeckName = itemView.findViewById(R.id.tvQuizDeckName);
            tvQuizInfo = itemView.findViewById(R.id.tvQuizInfo);
            btnViewQuiz = itemView.findViewById(R.id.btnViewQuiz);
            btnDeleteQuiz = itemView.findViewById(R.id.btnDeleteQuiz);
        }
    }
}
