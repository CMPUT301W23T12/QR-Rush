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

    private void getlocation(Optional<Location> location, Consumer<String> locationCallback) {
        if (!location.isPresent()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Location l = location.get();
                double latitude = l.getLatitude(); // Example latitude
                double longitude = l.getLongitude(); // Example longitude
                String apiKey = "AIzaSyABteFQy07SDCCQb_1FDyYtYF-ez6rbhKA"; // Replace with your API key

                try {
                    // Send a request to the Reverse Geocoding API
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                            latitude + "," + longitude + "&key=" + apiKey;
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");

                    // Get the response
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse the JSON response and get the city and province
                    JSONObject jsonObj = new JSONObject(response.toString());
                    JSONArray resultsArr = jsonObj.getJSONArray("results");
                    JSONObject firstResult = resultsArr.getJSONObject(0);
                    JSONArray addressComponentsArr = firstResult.getJSONArray("address_components");
                    String city = "";
                    String province = "";
                    for (int i = 0; i < addressComponentsArr.length(); i++) {
                        JSONObject component = addressComponentsArr.getJSONObject(i);
                        JSONArray typesArr = component.getJSONArray("types");
                        for (int j = 0; j < typesArr.length(); j++) {
                            String type = typesArr.getString(j);
                            if (type.equals("locality")) {
                                city = component.getString("long_name");
                            }
                            if (type.equals("administrative_area_level_1")) {
                                province = component.getString("short_name");
                            }
                        }
                    }

                    locationCallback.accept(String.format(Locale.ENGLISH, "%s, %s", city, province));
                } catch (Exception e) {
                    Log.e("City", e.toString());
                }
            }
        }).start();

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

        String location = "no location available";
        if (l.isPresent()) {
            Location loc = l.get();
            location = String.format(
                    Locale.ENGLISH,
                    "%.6f, %.6f",
                    loc.getLongitude(),
                    loc.getLatitude());
        }q
        locationView.setText(location);
        getlocation(qrCode.getLocation(), locationString -> {
            locationView.setVisibility(View.VISIBLE);
            locationView.setText(locationString);
        });

        pointView.setText(String.valueOf(qrCode.getScore()));

        // Image will be fit into the size of the image view
        Bitmap b = Bitmap.createScaledBitmap(qrCode.getImage(), 100, 100, false);
        imageView.setImageBitmap(b);
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
