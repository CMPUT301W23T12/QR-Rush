package com.example.qrrush;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private boolean locationPermissionGranted;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MainFragment extends Fragment {
    User user;
    Button cameraButton;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //grab google maps fragment
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Request location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);
        } else {
            locationPermissionGranted = true;
        }

        // Obtain the SupportMapFragment object from the layout using getChildFragmentManager()
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);

        // Replace the Fragment with the SupportMapFragment
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //when map is loaded
        Log.d("MainFragment", "onMapReady method called");
        mMap = googleMap;
        if (locationPermissionGranted) {
            getDeviceLocation();
        } else {
            // Display interactive world map
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void getDeviceLocation() {
        Log.d("MainFragment", "current location: " + currentLocation);

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task location = fusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    if (currentLocation != null) {
                        LatLng deviceLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(deviceLocation));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceLocation, 15f));
                    } else {
                        getDeviceLocation();
                    }
                }
            }
        });
    }
}



