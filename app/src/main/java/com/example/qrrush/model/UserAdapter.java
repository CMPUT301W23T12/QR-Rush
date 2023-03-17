package com.example.qrrush.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrrush.R;

import java.util.ArrayList;

/**
 * Custom adapter that will display the high scores of all players
 */
public class UserAdapter extends ArrayAdapter<User> {
    ArrayList<User> users;

    /**
     * Creates a LeaderboardAdapter, unsure of datatype for now.
     *
     * @param context The context object to pass to the super constructor.
     */
    public UserAdapter(Context context, ArrayList<User> objects) {
        super(context, 0, objects);
        users = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.leaderboard_content, parent, false);
        }
        User user = getItem(position);

        TextView name = view.findViewById(R.id.OtherUserNameView);
        TextView score = view.findViewById(R.id.OtherUserScoreView);
        ImageView image = view.findViewById(R.id.imageView2);
        name.setText(user.getUserName());
        score.setText(String.valueOf(user.getTotalScore()));
        image.setImageResource(R.drawable.discordpic);
        return view;
    }
}
