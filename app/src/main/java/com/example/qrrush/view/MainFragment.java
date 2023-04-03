package com.example.qrrush.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.MapQRCodeAdapter;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Quest;
import com.example.qrrush.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The MainFragment class represents the main fragment(home page) of the QR Rush
 * app.
 * Displays the user's total score,
 * the device's current location on a map, and providing a button to access the
 * camera feature.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback {
    private User user;
    private ImageButton cameraButton;
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
        mMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
        setupLocationUpdates();
        checkLocationPermission();
        mMap.getUiSettings().setCompassEnabled(true);
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
                    if (userLocationMarker != null) {
                        userLocationMarker.setPosition(userLocation);
                        return;
                    }

                    userLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))); // Change
                    // the color of the marker to azure blue
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
                }
            }
        };
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
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

            if (querySnapshot == null) {
                Log.e("Location Listener", "querySnapshot was null");
                return;
            }

            mMap.clear(); // Clear previous markers from the map
            // Add the device location marker again after clearing the map
            mMap.addMarker(new MarkerOptions().position(deviceLocation));

            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
            for (int i = 0; i < querySnapshot.getDocuments().size(); i += 1) {
                GeoPoint geoPoint = documents.get(i).getGeoPoint("location");
                if (geoPoint == null) {
                    Log.e("Geo", "location was null");
                    continue;
                }

                LatLng qrCodeLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                mMap.addMarker(new MarkerOptions().position(qrCodeLatLng).title(documents.get(i).getId()));

                // Add marker click listener to show alert dialog
                final int finalI = i;
                mMap.setOnMarkerClickListener(marker -> {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(finalI);
                    Log.e("Bruh", document.getId());
                    // Create and show alert dialog
                    FirebaseWrapper.getScannedQRCodeData(document.getId(), user.getUserName(), (scannedByList) -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogStyle);
                        LayoutInflater inflater = requireActivity().getLayoutInflater();
                        View customView = inflater.inflate(R.layout.map_qrcode, null);
                        builder.setView(customView);

                        TextView hashText = customView.findViewById(R.id.hash_text);
                        ListView usersList = customView.findViewById(R.id.users_list);
                        ImageButton customPositiveButton = customView.findViewById(R.id.custom_positive_button);
                        ImageView hash_image = customView.findViewById(R.id.hash_image);

                        // create qrCode object
                        QRCode qrCode = new QRCode(document.getId());
                        // Set the hash text
                        hashText.setText(qrCode.getName());

                        hash_image.setImageBitmap(
                                Bitmap.createScaledBitmap(qrCode.getImage(), 100, 100, false));

                        Log.e("Testing", scannedByList.toString());

                        if (scannedByList.isEmpty()) {
                            builder.setMessage("No other user has scanned this QR code yet.");
                        } else {
                            // Set the adapter for the ListView
                            MapQRCodeAdapter adapter = new MapQRCodeAdapter(getContext(), scannedByList);
                            usersList.setAdapter(adapter);

                            // Set the click listener for the ListView items
                            usersList.setOnItemClickListener((parent, view, position, id) -> {
                                // Use getUserData to create a user object

                                // Send the user object to the profile fragment
                                new ProfileDialogFragment(user).show(
                                        requireActivity().getSupportFragmentManager(),
                                        "");
                            });
                        }

                        // Set the click listener for the custom positive button
                        customPositiveButton.setOnClickListener(view -> {
                            AlertDialog dialog = (AlertDialog) view.getTag();
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        });

                        // Create the AlertDialog
                        AlertDialog alertDialog = builder.create();
                        // Set the tag for customPositiveButton
                        customPositiveButton.setTag(alertDialog);
                        // Show the custom alert dialog
                        alertDialog.show();

                    });
                    return true; // Return true to indicate that we've handled the marker click event
                });

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
