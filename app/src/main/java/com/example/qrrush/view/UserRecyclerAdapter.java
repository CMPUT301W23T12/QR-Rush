package com.example.qrrush.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.qrrush.R;
import com.example.qrrush.model.User;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {
    ArrayList<User> users;
    Context context;

    public UserRecyclerAdapter(Context context, ArrayList<User> objects) {
        this.context = context;
        users = objects;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_content, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getUserName());
        holder.score.setText(String.valueOf(user.getTotalScore()));
        holder.image.setImageResource(R.drawable.discordpic);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView score;
        ImageView image;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.OtherUserNameView);
            score = itemView.findViewById(R.id.OtherUserScoreView);
            image = itemView.findViewById(R.id.imageView2);
        }
    }
}
