package com.example.qrrush;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

/**
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    Button cameraButton;

    public MainFragment() {
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

        // Scan a new QR code
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_view, new PermissionsFragment()).commit();
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
}