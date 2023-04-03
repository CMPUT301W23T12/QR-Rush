package com.example.qrrush.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrrush.R;
import com.example.qrrush.view.ProfileDialogFragment;

import java.util.ArrayList;
import java.util.Locale;
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

        LinearLayout container = view.findViewById(R.id.qrCodeAdapterContainer);
        container.setBackgroundColor(Color.parseColor("#cbc3e3"));

        QRCode qrCode = getItem(position);
        TextView nameView = view.findViewById(R.id.nameViewQR);
        nameView.setTextColor(Color.BLACK);
        TextView pointView = view.findViewById(R.id.pointView);
        pointView.setTextColor(Color.BLACK);
        TextView locationView = view.findViewById(R.id.locationView);
        locationView.setTextColor(Color.BLACK);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView commentEditText = view.findViewById(R.id.commentEditText);
        ImageView locationImage = view.findViewById(R.id.locationImageQRCode);
        locationImage.setVisibility(View.GONE);
        commentEditText.setVisibility(View.GONE);
        nameView.setText(qrCode.getName());

        Optional<Location> l = qrCode.getLocation();

        String location = "no location available";
        if (l.isPresent()) {
            Location loc = l.get();
            location = String.format(
                    Locale.ENGLISH,
                    "%.6f, %.6f",
                    loc.getLongitude(),
                    loc.getLatitude());
        }
        locationView.setText(location);

        pointView.setText("Score: " + qrCode.getScore());

        ImageButton deleteButton = view.findViewById(R.id.deleteButton);

        deleteButton.setVisibility(View.GONE);

        // Image will be fit into the size of the image view
        Bitmap b = Bitmap.createScaledBitmap(qrCode.getImage(), 100, 100, false);
        imageView.setImageBitmap(b);

        ImageButton commentButton = view.findViewById(R.id.commentButton);

        commentButton.setVisibility(View.GONE);
        // Bug Report: If the user deletes the QR code, or changes their name it should
        // update the qrcodes collection on firebase
        // and update the username/remove it. If someone scans a top QR code here and
        // changes their name, it won't update and when u try to click on their profile
        // it will crash the app.
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display other users who have scanned the same QR code as an AlertDialog
                FirebaseWrapper.getScannedQRCodeDataLeader(qrCode.getHash(), (scannedByList) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Scanned by...");

                    if (scannedByList.isEmpty()) {
                        builder.setMessage("No other user has scanned this QR code yet.");
                    } else {
                        builder.setItems(scannedByList.toArray(new String[scannedByList.size()]),

                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int pos) {
                                        // position is tracked by "pos" so now we pass the clickable profile
                                        // We need to create a user object with that so we gotta use getUserData
                                        FirebaseWrapper.getUserData(scannedByList.get(pos), user -> {
                                            // scannedByList.get(pos) returns the name -> STRING
                                            // send the user object to the profile fragment
                                            new ProfileDialogFragment(user.get()).show(
                                                    ((AppCompatActivity) context).getSupportFragmentManager(),
                                                    "");
                                        });

                                    }
                                });
                    }
                    builder.setPositiveButton("OK", null);
                    builder.show();
                });

            }
        });
        return view;
    }
}
