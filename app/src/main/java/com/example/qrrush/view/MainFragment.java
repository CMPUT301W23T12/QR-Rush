package com.example.qrrush.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker userLocationMarker;
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
        cameraButton = view.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener((v) -> {
            new CameraFragment(user).show(
                    requireActivity().getSupportFragmentManager(),
                    "Scan a QR code"
            );
        });

        TextView scoreView = view.findViewById(R.id.scoreView);
        // sets users total score on the main page
        scoreView.setText(String.valueOf(user.getTotalScore()));

        // Obtain the SupportMapFragment object from the layout using getChildFragmentManager()
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);


        // Replace the Fragment with the SupportMapFragment
        mapFragment.getMapAsync(this);

        loadingText = view.findViewById(R.id.map_loading_text);
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
        mMap = googleMap;
        setupLocationUpdates();
        checkLocationPermission();
        Geo.getCurrentLocation(location -> {
            handleQrCodeMarkers(location);
        });
    }

    private void setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500); // Update interval in milliseconds
        locationRequest.setFastestInterval(100); // Fastest update interval in milliseconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (userLocationMarker == null) {
                        userLocationMarker = mMap.addMarker(new MarkerOptions()
                                .position(userLocation)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))); // Change the color of the marker to azure blue
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
                    } else {
                        userLocationMarker.setPosition(userLocation);
                    }
                }
            }
        };
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
    private void handleQrCodeMarkers(Location location) {
        loadingText.setVisibility(View.GONE);

        LatLng deviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Log.e("permission", deviceLocation.toString());
        mMap.addMarker(new MarkerOptions().position(deviceLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 15f));

        // Fetch QRCode locations from Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("qrcodes").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.w("MainFragment", "Listen failed.", error);
                return;
            }

            if (querySnapshot != null) {
                mMap.clear(); // Clear previous markers from the map
                // Add the device location marker again after clearing the map
                mMap.addMarker(new MarkerOptions().position(deviceLocation));

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    GeoPoint geoPoint = document.getGeoPoint("location");
                    if (geoPoint != null) {
                        LatLng qrCodeLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(qrCodeLatLng).title(document.getId()));

                        // Add marker click listener to show alert dialog
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                // Create and show alert dialog
                                FirebaseWrapper.getScannedQRCodeData(document.getId(), user.getUserName(), (scannedByList) -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("QR CODE\nHash:" + document.getId());
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
                                                            requireActivity().getSupportFragmentManager().beginTransaction()
                                                                    .replace(R.id.tabLayout, new ProfileFragment(user.get(), false)).commit();

                                                        });
                                                    }
                                                });
                                    }
                                    builder.setPositiveButton("OK", null);
                                    builder.show();
                                });
                                return true; // Return true to indicate that we've handled the marker click event
                            }
                        });
                    }
                }
            } else {
                Log.d("MainFragment", "Current data: null");
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}




