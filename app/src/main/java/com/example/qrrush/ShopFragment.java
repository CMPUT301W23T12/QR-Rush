package com.example.qrrush;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Shop Fragment for QR codes
 */
public class ShopFragment extends Fragment {
    private ImageView qrCode;
    private TextView scoreText;
    private Button commonButton;
    private Button rareButton;
    private Button legendaryButton;
    private TextView qrContentText;

    public ShopFragment() {
        // Required empty public constructor
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
        qrCode = view.findViewById(R.id.qr_code);
        scoreText = view.findViewById(R.id.score);
        commonButton = view.findViewById(R.id.common_button);
        rareButton = view.findViewById(R.id.rare_button);
        legendaryButton = view.findViewById(R.id.legendary_button);
        qrContentText = view.findViewById(R.id.qr_content);

        // Add click listeners to buttons
        // TODO: add these scores to the current User.
        commonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCode code = QRCode.withRarity(Rarity.Common);
                scoreText.setText("Score: " + code.getScore());
                qrContentText.setText("QR content: " + code.getHash());
            }
        });

        rareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCode code = QRCode.withRarity(Rarity.Rare);
                scoreText.setText("Score: " + code.getScore());
                qrContentText.setText("QR content: " + code.getHash());
            }
        });

        legendaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCode code = QRCode.withRarity(Rarity.Rare);
                scoreText.setText("Score: " + code.getScore());
                qrContentText.setText("QR content: " + code.getHash());
            }
        });

        return view;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}