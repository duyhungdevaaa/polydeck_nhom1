package com.nhom1.polydeck.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.nhom1.polydeck.R;
import com.nhom1.polydeck.data.model.User;

import java.util.List;
import java.util.Random;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserClickListener listener;

    private static final String[] AVATAR_COLORS = {
            "#7C3AED", "#3B82F6", "#10B981", "#F59E0B", "#EF4444", "#8B5CF6"
    };

    public interface OnUserClickListener {
        void onDetailClick(User user);

        void onBlockClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvAvatar.setText(user.getInitials());
        setAvatarColor(holder.tvAvatar, position);

        holder.tvUserName.setText(user.getHoTen());
        holder.tvEmail.setText(user.getEmail());
        holder.tvUserInfo.setText(String.format("Level %d • %d XP • %s",
                user.getLevel(), user.getXp(), user.getNgayThamGia()));

        if (user.isActive()) {
            holder.tvStatus.setText("Hoạt động");
            holder.tvStatus.setTextColor(Color.parseColor("#10B981"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            holder.btnBlock.setText("Khóa");
            holder.btnBlock.setBackgroundResource(R.drawable.bg_button_outline_red);
            holder.btnBlock.setTextColor(Color.parseColor("#EF4444"));
        } else {
            holder.tvStatus.setText("Bị khóa");
            holder.tvStatus.setTextColor(Color.parseColor("#EF4444"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_blocked);
            holder.btnBlock.setText("Mở khóa");
            holder.btnBlock.setBackgroundResource(R.drawable.bg_button_outline_green);
            holder.btnBlock.setTextColor(Color.parseColor("#10B981"));
        }

        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(user);
            }
        });

        holder.btnBlock.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBlockClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void setAvatarColor(TextView textView, int position) {
        String color = AVATAR_COLORS[position % AVATAR_COLORS.length];
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(color));
        textView.setBackground(drawable);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvUserName, tvEmail, tvUserInfo, tvStatus;
        Button btnDetail, btnBlock;
        ImageView btnMore;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvUserInfo = itemView.findViewById(R.id.tvUserInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnBlock = itemView.findViewById(R.id.btnBlock);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}