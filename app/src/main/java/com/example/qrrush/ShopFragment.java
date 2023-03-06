package com.example.qrrush;

import static com.example.qrrush.Score.getNumConsecutiveZeroes;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qrrush.Score;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Shop Fragment for QR codes
 */
public class ShopFragment extends Fragment {
    private ImageView qrCode;
    private TextView scoreText;
    private Button commonButton;
    private Button rareButton;
    private Button legendaryButton;
    private Score score;
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
        score = new Score(0);
        qrContentText = view.findViewById(R.id.qr_content);

        // Add click listeners to buttons
        commonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate hash, calculate score, and update views
                String hashValue = generateHash(1);
                int commonScore = score.calculateScore(hashValue, 1);
                scoreText.setText("Score: " + commonScore);
                qrContentText.setText("QR content: " + hashValue);
            }
        });

        rareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate hash, calculate score, and update views
                String hashValue = generateHash(2);
                int rareScore = Score.generateRareScore();
                scoreText.setText("Score: " + rareScore);
                qrContentText.setText("QR content: " + hashValue);
            }
        });

        legendaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate hash, calculate score, and update views
                String hashValue = generateHash(4);
                int legendaryScore = Score.generateLegendaryScore();
                scoreText.setText("Score: " + legendaryScore);
                qrContentText.setText("QR content: " + hashValue);
            }
        });

        return view;
    }

    public static String generateHash(int numZeroes) {
        Random rand = new Random();
        String hashValue;
        while (true) {
            StringBuilder sb = new StringBuilder(32);
            for (int i = 0; i < 32; i++) {
                sb.append(Integer.toHexString(rand.nextInt(16)));
            }
            String str = sb.toString();

            hashValue = sha256(str);
            int scoreValue = 0;
            if (numZeroes >= 4) {
                // Legendary
                if (getNumConsecutiveZeroes(hashValue) >= 4) {
                    scoreValue = Score.generateLegendaryScore();
                } else {
                    // Choose a random index to insert the zeroes
                    int index = rand.nextInt(28) + 2; // exclude first and last two characters
                    // Replace a substring of length 4 with four zeroes
                    hashValue = hashValue.substring(0, index) + "0000" + hashValue.substring(index + 4);
                    scoreValue = Score.generateLegendaryScore();
                }
            } else if (numZeroes >= 2) {
                // Rare
                int index = rand.nextInt(30); // choose a random index to insert zeroes
                String substring = hashValue.substring(index, index + 2); // get a 2 character substring
                if (substring.contains("0")) {
                    continue; // the substring already contains a zero, start over
                } else {
                    // insert zeroes into the substring
                    String newSubstring = substring.substring(0, rand.nextInt(1)) + "00"
                            + substring.substring(rand.nextInt(1) + 1, 2);
                    hashValue = hashValue.substring(0, index) + newSubstring
                            + hashValue.substring(index + 2);
                    scoreValue = Score.generateRareScore();
                }
            } else {
                // Common
                if (getNumConsecutiveZeroes(hashValue) == 0) {
                    scoreValue = Score.generateCommonScore();
                } else {
                    continue;
                }
            }

            // Check if the score matches and return the hash if it does
            int calculatedScore = Score.calculateScore(hashValue, numZeroes);
            if (calculatedScore == scoreValue) {
                return hashValue;
            }
        }
    }

    private static String sha256(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}