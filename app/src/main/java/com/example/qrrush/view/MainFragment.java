package com.example.qrrush.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.Quest;
import com.example.qrrush.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

/**
 * The MainFragment class represents the main fragment(home page) of the QR Rush
 * app.
 * Displays the user's total score,
 * the device's current location on a map, and providing a button to access the
 * camera feature.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback {
    private User user;

    private MediaPlayer mediaPlayer;
    private ImageButton cameraButton;
    TextView loadingText;

    /**
     * Grabs the User object from the main activity
     *
     * @param user
     */
    public MainFragment(User user) {
        this.user = user;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraButton = (ImageButton) view.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener((v) -> {
            new CameraFragment(user, () -> {
                refreshStats(view);
            }).show(
                    requireActivity().getSupportFragmentManager(),
                    "Scan a QR code");
        });

        refreshStats(view);

        // Obtain the SupportMapFragment object from the layout using
        // getChildFragmentManager()
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);

        // Replace the Fragment with the SupportMapFragment
        mapFragment.getMapAsync(this);

        loadingText = view.findViewById(R.id.map_loading_text);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStats(requireView());
    }

    private void refreshStats(View v) {
        // sets users total score on the main page
        TextView scoreView = v.findViewById(R.id.scoreView);
        scoreView.setText(String.valueOf(user.getTotalScore()));

        ArrayList<Quest> quests = Quest.getCurrentQuests();
        CheckBox quest1box = v.findViewById(R.id.quest1box);
        quest1box.setText(quests.get(0).getDescription());
        quest1box.setChecked(user.isQuestCompleted(0));

        CheckBox quest2box = v.findViewById(R.id.quest2box);
        quest2box.setText(quests.get(1).getDescription());
        quest2box.setChecked(user.isQuestCompleted(1));

        CheckBox quest3box = v.findViewById(R.id.quest3box);
        quest3box.setText(quests.get(2).getDescription());
        quest3box.setChecked(user.isQuestCompleted(2));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    /**
     * Runs when the map is ready to display.
     *
     * @param googleMap The GoogleMap object which is ready to display.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // When map is loaded
        Geo.getCurrentLocation(location -> {
            loadingText.setVisibility(View.GONE);

            LatLng deviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("permission", deviceLocation.toString());
            googleMap.addMarker(new MarkerOptions().position(deviceLocation));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 15f));
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));

            // Fetch QRCode locations from Firebase
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("qrcodes").addSnapshotListener((querySnapshot, error) -> {
                if (error != null) {
                    Log.w("MainFragment", "Listen failed.", error);
                    return;
                }

                if (querySnapshot == null) {
                    Log.w("MainFragment", "QuerySnapshot is null!.", error);
                    return;
                }

                googleMap.clear(); // Clear previous markers from the map
                // Add the device location marker again after clearing the map
                googleMap.addMarker(new MarkerOptions().position(deviceLocation));

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    GeoPoint geoPoint = document.getGeoPoint("location");
                    if (geoPoint == null) {
                        Log.d("MainFragment", "Current data: null");
                        continue;
                    }

                    LatLng qrCodeLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(qrCodeLatLng).title(document.getId()));

                    // Add marker click listener to show alert dialog
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            // Create and show alert dialog
                            FirebaseWrapper.getScannedQRCodeData(document.getId(), user.getUserName(),
                                    (scannedByList) -> {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("QR CODE\nHash:" + document.getId());
                                        if (scannedByList.isEmpty()) {
                                            builder.setMessage("No other user has scanned this QR code yet.");
                                            return;
                                        }
                                        builder.setItems(scannedByList.toArray(new String[scannedByList.size()]),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int pos) {
                                                        // position is tracked by "pos" so now we pass the clickable
                                                        // profile
                                                        // We need to create a user object with that so we gotta use
                                                        // getUserData
                                                        FirebaseWrapper.getUserData(scannedByList.get(pos), user -> {
                                                            // scannedByList.get(pos) returns the name -> STRING
                                                            // send the user object to the profile fragment
                                                            requireActivity().getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .replace(R.id.tabLayout,
                                                                            new ProfileFragment(user.get(), false))
                                                                    .commit();

                                                        });
                                                    }
                                                });
                                        builder.setPositiveButton("OK", null);
                                        builder.show();
                                    });
                            return true; // Return true to indicate that we've handled the marker click event
                        }
                    });
                }
            });
        });
    }
}
