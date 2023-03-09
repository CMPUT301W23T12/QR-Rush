package com.example.qrrush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.function.Consumer;

public class Geo {
    private static boolean initialized = false;
    private static FusedLocationProviderClient fusedLocationClient;

    /**
     * Initializes the Geo geolocation API for use by the rest of the App.
     *
     * @param a The activity to ask geolocation in.
     */
    public static void initGeolocation(Activity a) {
        Geo.fusedLocationClient = LocationServices.getFusedLocationProviderClient(a);
    }

    /**
     * Runs the onComplete function provided with the actual location retrieved.
     *
     * @param onComplete The function to run with the location retrieved.
     */
    @SuppressLint("MissingPermission")
    public static void getCurrentLocation(Consumer<Location> onComplete) {
        Geo.fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener(onComplete::accept);
    }
}