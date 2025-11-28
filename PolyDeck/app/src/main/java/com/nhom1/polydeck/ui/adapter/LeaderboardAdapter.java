package com.nhom1.polydeck.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.model.User;
import com.nhom1.polydeck.utils.SessionManager;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.VH> {
    private final List<User> items;
    private final String currentUserEmail;
    private final SessionManager sessionManager;

    public LeaderboardAdapter(List<User> items, SessionManager sessionManager) {
        this.items = items;
        this.sessionManager = sessionManager;
        this.currentUserEmail = sessionManager.getEmail();
    }

    public void update(List<User> users) {
        items.clear();
        items.addAll(users);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        User u = items.get(position);
        int rank = position + 4; // Start from rank 4 (top 3 shown separately)
        
        h.tvRank.setText("#" + rank);
        h.tvName.setText(u.getHoTen());
        h.tvScore.setText(String.valueOf(u.getXp()));
        h.tvAbbreviation.setText(u.getInitials());
        
        // Calculate days since joining
        int days = calculateDays(u.getNgayThamGia());
        h.tvDays.setText(days + " days");
        
        // Highlight current user
        boolean isCurrentUser = currentUserEmail != null && currentUserEmail.equalsIgnoreCase(u.getEmail());
        if (isCurrentUser) {
            h.tvName.setText(u.getHoTen() + " (Bạn)");
            // Show avatar for current user
            h.cvAvatar.setVisibility(View.VISIBLE);
            h.tvAbbreviation.setVisibility(View.GONE);
            h.tvAvatarText.setText(u.getInitials());
            h.tvAvatarText.setBackgroundColor(Color.parseColor("#5C6BC0")); // Purple
            // Change card background
            h.cardView.setCardBackgroundColor(Color.parseColor("#E3F2FD")); // Light blue
            h.tvScore.setTextColor(Color.parseColor("#1976D2")); // Blue
        } else {
            h.cvAvatar.setVisibility(View.GONE);
            h.tvAbbreviation.setVisibility(View.VISIBLE);
            h.cardView.setCardBackgroundColor(Color.WHITE);
            h.tvScore.setTextColor(Color.parseColor("#212121"));
        }
    }

    private int calculateDays(String ngayThamGia) {
        if (ngayThamGia == null || ngayThamGia.isEmpty()) {
            return 0;
        }
        try {
            // Parse ISO date format: "2025-11-25T03:58:53.863Z"
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date joinDate = sdf.parse(ngayThamGia);
            if (joinDate == null) {
                // Try alternative format without milliseconds
                sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                joinDate = sdf.parse(ngayThamGia);
            }
            if (joinDate != null) {
                long diff = System.currentTimeMillis() - joinDate.getTime();
                return (int) (diff / (1000 * 60 * 60 * 24)); // Convert to days
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvRank, tvName, tvScore, tvAbbreviation, tvDays, tvAvatarText;
        CardView cvAvatar;
        
        VH(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvAbbreviation = itemView.findViewById(R.id.tvAbbreviation);
            tvDays = itemView.findViewById(R.id.tvDays);
            cvAvatar = itemView.findViewById(R.id.cvAvatar);
            tvAvatarText = itemView.findViewById(R.id.tvAvatarText);
        }
    }
}
