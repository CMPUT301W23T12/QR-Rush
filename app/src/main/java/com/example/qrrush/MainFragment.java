package com.example.qrrush;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MainFragment extends Fragment {
    User user;

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

    /**
     * The onClick handler for the MainFragment. This should handle everything the user can click
     * on in the main page, which is the camera button, the weekly tasks, etc.
     * @param view is the View which was clicked on.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.camera_button) {
            return;
        }

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

        requestPermissionLauncher.launch(PERMISSIONS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_main, container, false);

        cameraButton = result.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView scoreView = view.findViewById(R.id.scoreView);
        /**
         * sets users total score on the main page
         */
//        scoreView.setText(String.valueOf(user.getTotalScore()));
    }
}