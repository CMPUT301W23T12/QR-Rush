package com.example.qrrush;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
// Custom Adapter for each item in the listview
public class ProfileAdapter extends ArrayAdapter<QRcode> {
    ArrayList<QRcode> qRcodes;

    public ProfileAdapter(Context context, ArrayList<QRcode> objects) {
        super(context, 0, objects);
        qRcodes = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.profile_content, parent, false);
        } else {
            view = convertView;
        }
        QRcode qRcode = super.getItem(position);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView pointView = view.findViewById(R.id.pointView);
        TextView locationView = view.findViewById(R.id.locationView);
        ImageView imageView = view.findViewById(R.id.imageView);
        nameView.setText(qRcode.getName());
        pointView.setText(String.valueOf(qRcode.getValue()));
        locationView.setText(qRcode.getLocation());
        Button d = view.findViewById(R.id.deleteButton);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qRcodes.isEmpty()) {
                    return;
                }
                qRcodes.remove(position);
                Log.e("QR Rush", "this is a test");
                notifyDataSetChanged();
            }
        });
        // Image will be fit into the size of the image view
        Picasso
                .get()
                .load(qRcode.getImageURL())
                .fit()
                .into(imageView);
        return view;
    }
}
