package com.example.qrrush;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

/**
 * Attempts to get the permissions to open the camera, then navigates to the CameraFragment.
 */
public class PermissionsFragment extends Fragment {

    static final String PERMISSIONS = Manifest.permission.CAMERA;

    public PermissionsFragment() {
        // Required empty public constructor
    }

    // Register the permissions callback
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), (granted) -> {
                if (granted) {
                    return;
                }

                // TODO: explain to the user that this permission is necessary for the game to
                //       work, and thus the game can't continue without it.
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Do we already have permission? If so just go to the camera.
        if (ContextCompat.checkSelfPermission(requireContext(), PERMISSIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new CameraFragment()).commit();
            return;
        }

        // Tell the user why we need camera permission before asking
        if (shouldShowRequestPermissionRationale(PERMISSIONS)) {
            // TODO: show a UI explaining why we need to use the camera before prompting and go
            //       back to the main screen.
        }

        Log.e("QR Rush", "no permissions");

        requestPermissionLauncher.launch(PERMISSIONS);
        // If they granted permission, continue on. Else, go back to the main screen.
        if (ContextCompat.checkSelfPermission(requireContext(), PERMISSIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.e("QR Rush", "permissions");
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new CameraFragment()).commit();
            return;
        }

        Log.e("QR Rush", "no permissions");

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_view, new MainFragment()).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permissions, container, false);
    }
}