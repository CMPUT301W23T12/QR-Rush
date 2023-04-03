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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * ArrayAdapter which displays QR Codes from the QRCode class for the leaderboard fragment.
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
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.leaderboardqrcodeadapter, parent, false);
        }

        QRCode qrCode = getItem(position);
        TextView nameView = view.findViewById(R.id.nameViewQR1);
        nameView.setTextColor(Color.BLACK);
        TextView pointView = view.findViewById(R.id.pointView1);
        pointView.setTextColor(Color.BLACK);
        TextView locationView = view.findViewById(R.id.locationView1);
        locationView.setTextColor(Color.BLACK);
        ImageView imageView = view.findViewById(R.id.imageView1);
        nameView.setText(qrCode.getName());
        nameView.setTextColor(qrCode.getColor());

        Optional<Location> l = qrCode.getLocation();
        // gets location
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

        pointView.setText(String.valueOf(qrCode.getScore()));

        // Image will be fit into the size of the image view
        Bitmap b = Bitmap.createScaledBitmap(qrCode.getImage(), 100, 100, false);
        imageView.setImageBitmap(b);
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
