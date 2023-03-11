package com.example.qrrush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnTokenCanceledListener;

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
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        Geo.fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    onComplete.accept(location);
                    Log.e("Permission", "Accepted Location: " + location);
                })
                .addOnCanceledListener(() -> {
                    Log.e("Permission", "Cancellation requested");
                })
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("Permission", "Location request timed out");
                    }
                });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            cancellationTokenSource.cancel();
            Log.e("Permission", "Timeout triggered");
        }, 1000000);
    }



}
