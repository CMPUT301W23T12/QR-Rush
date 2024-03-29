package com.example.qrrush.model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
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

        TextView rank = view.findViewById(R.id.rankTextView); // add TextView for rank

        TextView name = view.findViewById(R.id.OtherUserNameView);
        TextView score = view.findViewById(R.id.OtherUserScoreView);
        ImageView image = view.findViewById(R.id.imageView2);

        int index = position + 4; // calculate the rank of the user
        rank.setText(String.valueOf(index)); // set the rank TextView

        name.setText(user.getUserName());
        score.setText(String.valueOf(user.getTotalScore()));
        Log.e("score",String.valueOf(user.getTotalScore()));

        if (user.hasProfilePicture()) {
            Glide.with(getContext())
                    .load(user.getProfilePicture())
                    .dontAnimate()
                    .into(image);
        } else {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(user.getUserName());
            image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(ResourcesCompat.getFont(getContext(), R.font.gatekept))
                    .toUpperCase()
                    .width(200)
                    .height(200)
                    .endConfig()
                    .buildRound(String.valueOf(user.getUserName().charAt(0)), color);
            image.setImageDrawable(drawable);
        }
        return view;
    }
}
