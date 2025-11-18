package com.nhom1.polydeck.ui.adapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.model.BoTu;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private Context context;
    private List<BoTu> deckList;
    private OnDeckClickListener listener;

    public interface OnDeckClickListener {
        void onViewClick(BoTu deck);
        void onEditClick(BoTu deck);
        void onDeleteClick(BoTu deck);
    }

    public DeckAdapter(Context context, List<BoTu> deckList, OnDeckClickListener listener) {
        this.context = context;
        this.deckList = deckList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        BoTu deck = deckList.get(position);

        setDeckColor(holder.flIcon, deck.getMauSac());

        holder.tvDeckName.setText(deck.getTenBoTu());
        holder.tvDeckInfo.setText(String.format("%d từ • %d người dùng • %s",
                deck.getSoTu(), deck.getSoNguoiDung(), deck.getNgayTao()));

        if ("Đã xuất bản".equals(deck.getTrangThai())) {
            holder.tvStatus.setText("Đã xuất bản");
            holder.tvStatus.setTextColor(Color.parseColor("#10B981"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active);
        } else {
            holder.tvStatus.setText("Nháp");
            holder.tvStatus.setTextColor(Color.parseColor("#6B7280"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_edittext);
        }

        holder.btnView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewClick(deck);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(deck);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(deck);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    private void setDeckColor(FrameLayout frameLayout, String colorHex) {
        try {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Color.parseColor(colorHex));
            drawable.setCornerRadius(12 * context.getResources().getDisplayMetrics().density);
            frameLayout.setBackground(drawable);
        } catch (Exception e) {
            frameLayout.setBackgroundResource(R.drawable.rounded_bg);
        }
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIcon;
        TextView tvDeckName, tvDeckInfo, tvStatus;
        Button btnView, btnEdit, btnDelete;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            flIcon = itemView.findViewById(R.id.flIcon);
            tvDeckName = itemView.findViewById(R.id.tvDeckName);
            tvDeckInfo = itemView.findViewById(R.id.tvDeckInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}