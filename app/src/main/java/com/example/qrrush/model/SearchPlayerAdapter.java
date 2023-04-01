package com.example.qrrush.model;

import android.content.Context;
import android.graphics.Color;
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

import java.util.List;

public class SearchPlayerAdapter extends ArrayAdapter<User> {

    private Context context;
    private int resource;
    private List<User> mUsers;

    public SearchPlayerAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        this.context = context;
        this.resource = resource;
        this.mUsers = users;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(resource, parent, false);
        }

        User user = mUsers.get(position);

        // Set the user data to the appropriate UI components
        TextView searchedName = view.findViewById(R.id.nameViewSocial);
        TextView searchedScore = view.findViewById(R.id.playerScoreView);
        ImageView searchedPlayerImageView = view.findViewById(R.id.searchedPlayerImageView);
        searchedName.setText(user.getUserName());
        searchedScore.setText(String.valueOf(user.getTotalScore()));

        if (user.hasProfilePicture()) {
            Glide.with(getContext())
                    .load(user.getProfilePicture())
                    .dontAnimate()
                    .into(searchedPlayerImageView);
        } else {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(user.getUserName());
            searchedPlayerImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(ResourcesCompat.getFont(getContext(), R.font.gatekept))
                    .toUpperCase()
                    .width(200)
                    .height(200)
                    .endConfig()
                    .buildRound(String.valueOf(user.getUserName().charAt(0)), color);
            searchedPlayerImageView.setImageDrawable(drawable);
        }

        return view;
    }

    public void setData(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

}
