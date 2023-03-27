package com.example.qrrush.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrrush.R;

import java.util.ArrayList;
import java.util.Optional;

/**
 * ArrayAdapter which displays QR Codes from the QRCode class.
 */
public class LeaderboardQRCodeAdapter extends ArrayAdapter<QRCode> {
    ArrayList<QRCode> qrCodes;

    Context context;

    /**
     * Creates a QRCodeAdapter given a list of QR Codes and a user.
     *
     * @param context The context object to pass to the super constructor.
     * @param objects The QR codes to display.

     */
    public LeaderboardQRCodeAdapter(Context context, ArrayList<QRCode> objects) {
        super(context, 0, objects);
        qrCodes = objects;

        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.profile_content, parent, false);
        }

        QRCode qrCode = getItem(position);
        TextView nameView = view.findViewById(R.id.nameViewQR);
        TextView pointView = view.findViewById(R.id.pointView);
        TextView locationView = view.findViewById(R.id.locationView);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView commentEditText = view.findViewById(R.id.commentEditText);
        commentEditText.setVisibility(View.GONE);
        nameView.setText(qrCode.getName());

        Optional<Location> l = qrCode.getLocation();


        String location = "no location available";
        if (l.isPresent()) {
            Location loc = l.get();
            location = loc.getLongitude() + ", " + loc.getLatitude();
        }
        locationView.setText(location);

        pointView.setText("Score: " + qrCode.getScore());

        Button deleteButton = view.findViewById(R.id.deleteButton);

        deleteButton.setVisibility(View.GONE);

        // Image will be fit into the size of the image view
        Bitmap b = Bitmap.createScaledBitmap(qrCode.getImage(), 100, 100, false);
        imageView.setImageBitmap(b);

        Button commentButton = view.findViewById(R.id.commentButton);

        commentButton.setVisibility(View.GONE);

        return view;
    }
}
