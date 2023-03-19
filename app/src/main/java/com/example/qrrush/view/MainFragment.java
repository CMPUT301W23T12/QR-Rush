package com.example.qrrush.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qrrush.R;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

/**
 * The MainFragment class represents the main fragment(home page) of the QR Rush app.
 * Displays the user's total score,
 * the device's current location on a map, and providing a button to access the camera feature.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback {
    private User user;
    private Button cameraButton;
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
        cameraButton = view.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener((v) -> {
            FragmentTransaction t = requireActivity().getSupportFragmentManager().beginTransaction();
            t.replace(R.id.main_view, new CameraFragment(user));
            t.addToBackStack(null);
            t.commit();
        });

        TextView scoreView = view.findViewById(R.id.scoreView);
        // sets users total score on the main page
        scoreView.setText(String.valueOf(user.getTotalScore()));

        // Obtain the SupportMapFragment object from the layout using getChildFragmentManager()
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);

        // Replace the Fragment with the SupportMapFragment
        mapFragment.getMapAsync(this);
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
            LatLng deviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("permission", deviceLocation.toString());
            googleMap.addMarker(new MarkerOptions().position(deviceLocation));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 15f));

            // Fetch QRCode locations from Firebase
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("qrcodes").addSnapshotListener((querySnapshot, error) -> {
                if (error != null) {
                    Log.w("MainFragment", "Listen failed.", error);
                    return;
                }

                if (querySnapshot != null) {
                    googleMap.clear(); // Clear previous markers from the map
                    // Add the device location marker again after clearing the map
                    googleMap.addMarker(new MarkerOptions().position(deviceLocation));

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        GeoPoint geoPoint = document.getGeoPoint("location");
                        if (geoPoint != null) {
                            LatLng qrCodeLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(qrCodeLatLng).title(document.getId()));
                        }
                    }
                } else {
                    Log.d("MainFragment", "Current data: null");
                }
            });
        });
    }
}




