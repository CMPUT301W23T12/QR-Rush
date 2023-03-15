package com.example.qrrush.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


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
        TextView searchedRank = view.findViewById(R.id.rankView);

        searchedName.setText(user.getUserName());
        searchedRank.setText(String.valueOf(user.getRank()));

        return view;
    }

    public void setData(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

}
