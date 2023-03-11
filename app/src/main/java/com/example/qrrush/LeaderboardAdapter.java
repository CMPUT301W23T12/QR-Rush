package com.example.qrrush;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LeaderboardAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> names;

    public LeaderboardAdapter(Context context, List<String> names) {
        super(context, R.layout.leaderboard_content, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.leaderboard_content, parent, false);
        TextView nameView =  ((Activity) getContext()).findViewById(R.id.list_name);
        String name = names.get(position);
        nameView.setText(name);
        return rowView;
    }
}
