package com.nhom1.polydeck.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.model.TuVung;

import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabViewHolder> {

    private final List<TuVung> vocabList;

    public VocabularyAdapter(List<TuVung> vocabList) {
        this.vocabList = vocabList;
    }

    @NonNull
    @Override
    public VocabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabulary, parent, false);
        return new VocabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabViewHolder holder, int position) {
        TuVung vocab = vocabList.get(position);
        if (vocab == null) return;

        holder.tvEnglish.setText(vocab.getTuTiengAnh());
        holder.tvPronunciation.setText(vocab.getPhienAm());
        holder.tvVietnamese.setText(vocab.getNghiaTiengViet());
        TextView tvExample = holder.itemView.findViewById(R.id.tvVocabExample);
        if (tvExample != null) {
            String ex = vocab.getCauViDu();
            if (ex != null && !ex.trim().isEmpty()) {
                tvExample.setText("\"" + ex + "\"");
                tvExample.setVisibility(View.VISIBLE);
            } else {
                tvExample.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return vocabList != null ? vocabList.size() : 0;
    }

    // FIX: Added this method to update the adapter's data
    public void updateData(List<TuVung> newList) {
        vocabList.clear();
        if (newList != null) {
            vocabList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    static class VocabViewHolder extends RecyclerView.ViewHolder {
        TextView tvEnglish, tvPronunciation, tvVietnamese;

        public VocabViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnglish = itemView.findViewById(R.id.tvVocabEnglish);
            tvPronunciation = itemView.findViewById(R.id.tvVocabPronunciation);
            tvVietnamese = itemView.findViewById(R.id.tvVocabVietnamese);
        }
    }
}
