package com.example.qrrush.view;

import static com.example.qrrush.model.Rarity.Common;
import static com.example.qrrush.model.Rarity.Legendary;
import static com.example.qrrush.model.Rarity.Rare;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Rarity;
import com.example.qrrush.model.User;

/**
 * The fragment which displays the shop fragment
 * This class is responsible for creating and setting up the shop,
 * handling what happens when the common, rare or legendary button is clicked
 * and displaying the randomly generated QR code
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

    private int rarityPrice(Rarity rarity) {
        int price;

        if (rarity == Common) {
            price = 1;
        } else if (rarity == Rare) {
            price = 5;
        } else {
            price = 10;
        }
        return price;
    }

    private void tryPurchaseQRCode(QRCode code) {
        int price = rarityPrice(code.getRarity());
        if (user.getMoney() >= price) {
            user.addQRCode(code);
            user.setMoney(user.getMoney() - price);
            Toast.makeText(getContext(), "QR code purchased!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Insufficient funds.", Toast.LENGTH_SHORT).show();
        }
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
            int initialMoney = user.getMoney();
            QRCode code = QRCode.withRarity(Common);
            tryPurchaseQRCode(code);
            int FinalMoney = user.getMoney();
            if (initialMoney != FinalMoney) {
                qrCode.setImageBitmap(
                        Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
                );
                scoreText.setText("Score: " + code.getScore());
                qrContentText.setText("QR content: " + code.getHash());
                nameContentText.setText("Name: " + code.getName());
            }
        });

        rareButton.setOnClickListener(v -> {
            int initialMoney = user.getMoney();
            QRCode code = QRCode.withRarity(Rare);
            tryPurchaseQRCode(code);
            int FinalMoney = user.getMoney();
            if (initialMoney != FinalMoney) {
                qrCode.setImageBitmap(
                        Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
                );
                scoreText.setText("Score: " + code.getScore());
                qrContentText.setText("QR content: " + code.getHash());
                nameContentText.setText("Name: " + code.getName());
            }
        });

        legendaryButton.setOnClickListener(v -> {
            int initialMoney = user.getMoney();
            QRCode code = QRCode.withRarity(Legendary);
            tryPurchaseQRCode(code);
            int FinalMoney = user.getMoney();
            if (initialMoney != FinalMoney) {
                qrCode.setImageBitmap(
                        Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
                );
                scoreText.setText("Score: " + code.getScore());
                qrContentText.setText("QR content: " + code.getHash());
                nameContentText.setText("Name: " + code.getName());
            }
        });

        return view;
    }
}