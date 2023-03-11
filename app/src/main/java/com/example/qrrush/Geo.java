package com.example.qrrush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.function.Consumer;

/**
 * The Geolocation API used in QR Rush.
 */
public class Geo {
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
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        Geo.fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    onComplete.accept(location);
                    Log.e("LocationRequest", "Location successfully sent ");
                })
                .addOnCanceledListener(() -> {
                    Log.e("LocationRequest", "Cancellation requested");
                })
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("LocationRequest", "Location request timed out");
                    }
                });
    }


}
