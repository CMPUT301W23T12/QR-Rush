package com.example.qrrush.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Rarity;
import com.example.qrrush.model.User;

/**
 * Shop Fragment for QR codes.
 */
public class ShopFragment extends Fragment {
    private ImageView qrCode;
    private TextView scoreText;
    private TextView qrContentText;
    private TextView nameContentText;
    User user;

    public ShopFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Now you can call findViewById on the inflated view
        Button commonButton = view.findViewById(R.id.common_button);
        Button rareButton = view.findViewById(R.id.rare_button);
        Button legendaryButton = view.findViewById(R.id.legendary_button);

        qrCode = view.findViewById(R.id.qr_code);
        scoreText = view.findViewById(R.id.score);
        qrContentText = view.findViewById(R.id.qr_content);
        nameContentText = view.findViewById(R.id.name_content);

        // Add click listeners to buttons
        // TODO: add these scores to the current User.
        commonButton.setOnClickListener(v -> {
            QRCode code = QRCode.withRarity(Rarity.Common);
            qrCode.setImageBitmap(
                    Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
            );
            scoreText.setText("Score: " + code.getScore());
            qrContentText.setText("QR content: " + code.getHash());
            nameContentText.setText("Name: " + code.getName());

            user.addQRCode(code);
        });

        rareButton.setOnClickListener(v -> {
            QRCode code = QRCode.withRarity(Rarity.Rare);
            qrCode.setImageBitmap(
                    Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
            );
            scoreText.setText("Score: " + code.getScore());
            qrContentText.setText("QR content: " + code.getHash());
            nameContentText.setText("Name: " + code.getName());

            user.addQRCode(code);
        });

        legendaryButton.setOnClickListener(v -> {
            QRCode code = QRCode.withRarity(Rarity.Legendary);
            qrCode.setImageBitmap(
                    Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
            );
            scoreText.setText("Score: " + code.getScore());
            qrContentText.setText("QR content: " + code.getHash());
            nameContentText.setText("Name: " + code.getName());

            user.addQRCode(code);
        });

        return view;
    }
}