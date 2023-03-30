package com.example.qrrush.view;

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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.qrrush.R;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.User;
import com.google.mlkit.vision.barcode.common.Barcode;

/**
 * this class is responsible for
 * The fragment displaying the screen after a QR code is scanned and asking the user to
 * confirm that they want to add the QR code to their account.
 */
public class QRConfirmFragment extends DialogFragment {
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
    FragmentManager manager;

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
            new CameraFragment(user).show(manager, "Confirm QR code");
            dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            Geo.getCurrentLocation(l -> {
                user.addQRCode(qrCode);
                dismiss();
            });
        });

        Bitmap b = Bitmap.createScaledBitmap(qrCode.getImage(), 200, 200, false);
        qrCodeImage.setImageBitmap(b);

        nameView.setText(qrCode.getName());
        scoreView.setText("Score: " + qrCode.getScore());
        rarityView.setText("Rarity: " + qrCode.getRarity());
        locationView.setText("Location: not saved");

        geolocationToggle.setChecked(false);
        geolocationToggle.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked) {
                locationView.setText("Location: not saved");
                qrCode.removeLocation();
                return;
            }

            locationView.setText("Location: getting location...");
            Geo.getCurrentLocation(l -> {
                qrCode.setLocation(l);
                locationView.setText("Location: " + l.getLongitude() + ", " + l.getLatitude());
            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_qr_confirm, container, false);
        manager = getParentFragmentManager();
        return result;
    }
}
