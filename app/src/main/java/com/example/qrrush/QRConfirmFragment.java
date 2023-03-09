package com.example.qrrush;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.mlkit.vision.barcode.common.Barcode;

public class QRConfirmFragment extends Fragment {
    User user;
    Barcode code;
    Button retakeButton;
    Button confirmButton;
    ImageView qrCodeImage;
    TextView nameView;
    TextView scoreView;
    TextView rarityView;
    TextView locationView;
    CheckBox geolocationToggle;

    public QRConfirmFragment(User user, Barcode b) {
        this.user = user;
        this.code = b;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retakeButton = view.findViewById(R.id.retake_button);
        confirmButton = view.findViewById(R.id.qr_confirm_button);
        qrCodeImage = view.findViewById(R.id.qr_code_image);
        nameView = view.findViewById(R.id.qrCode_name);
        scoreView = view.findViewById(R.id.qrCode_score);
        rarityView = view.findViewById(R.id.qrCode_rarity);
        locationView = view.findViewById(R.id.qrCode_location);
        geolocationToggle = view.findViewById(R.id.geolocation_checkbox);

        QRCode qrCode = new QRCode(this.code.getRawBytes());

        retakeButton.setOnClickListener(v -> {
            FragmentTransaction t = requireActivity().getSupportFragmentManager().beginTransaction();
            t.replace(R.id.main_view, new CameraFragment(user));
            t.addToBackStack(null);
            t.commit();
        });

        confirmButton.setOnClickListener(v -> {
            user.addQRCode(qrCode);

            FragmentTransaction t = requireActivity().getSupportFragmentManager().beginTransaction();
            t.replace(R.id.main_view, new MainFragment(user));
            t.addToBackStack(null);
            t.commit();
        });

        Bitmap b = Bitmap.createScaledBitmap(qrCode.getImage(), 200, 200, false);
        qrCodeImage.setImageBitmap(b);

        nameView.setText(qrCode.getName());
        scoreView.setText("Score: " + qrCode.getScore());
        rarityView.setText("Rarity: " + qrCode.getRarity());
        locationView.setText("Location: getting location...");
        Geo.getCurrentLocation(l -> {
            qrCode.setLocation(l);
            locationView.setText("Location: " + qrCode.getLocation().get());
        });

        geolocationToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked) {
                locationView.setText("Location: no location available");
                return;
            }

            Geo.getCurrentLocation(l -> {
                qrCode.setLocation(l);
                locationView.setText("Location: " + qrCode.getLocation().get());
            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_confirm, container, false);
    }
}