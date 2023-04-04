package com.example.qrrush.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Quest;
import com.example.qrrush.model.QuestType;
import com.example.qrrush.model.Rarity;
import com.example.qrrush.model.User;
import com.smb.glowbutton.GlowButton;

import java.util.ArrayList;

/**
 * The fragment which displays the shop fragment
 * This class is responsible for creating and setting up the shop,
 * handling what happens when the common, rare or legendary button is clicked
 * and displaying the randomly generated QR code
 */
public class ShopFragment extends Fragment {
    private ImageView qrCode;
    private TextView scoreText;

    private TextView nameContentText;
    private TextView coinsText;
    User user;
    /**
     * Constructs a new instance of the ShopFragment with a specified user.
     *
     * @param user the user object to associate with the fragment
     */
    public ShopFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**

     *Calculates and returns the price of a QR code based on its rarity.
     *
     * @param rarity The rarity of the QR code.
     * @return The price of the QR code.
     */
    private int rarityPrice(Rarity rarity) {
        int price;

        if (rarity == Rarity.Common) {
            price = 1;
        } else if (rarity == Rarity.Rare) {
            price = 5;
        } else {
            price = 10;
        }
        return price;
    }
    /**
     * Attempts to purchase the given QRCode by subtracting its rarity price from the user's money.
     * If the user has sufficient funds, the QRCode is added to the user's collection and a toast
     * message is displayed. Additionally, if the user has an active "BuyCodeOfRarity" quest that
     * matches the rarity of the purchased QRCode, the quest progress is increased by 1.
     * If the user does not have enough funds, a "Insufficient funds" toast message is displayed.
     *
     * @param code the QRCode to be purchased.
     */

    private void tryPurchaseQRCode(QRCode code) {
        int price = rarityPrice(code.getRarity());
        if (user.getMoney() >= price) {
            user.addQRCode(code);
            user.setMoney(user.getMoney() - price);
            Toast.makeText(getContext(), "QR code purchased!", Toast.LENGTH_SHORT).show();
            ArrayList<Quest> quests = Quest.getCurrentQuests();
            for (int i = 0; i < quests.size(); i += 1) {
                Quest q = quests.get(i);
                if (q.getType() == QuestType.BuyCodeOfRarity &&
                        q.getRarity() == code.getRarity()) {
                    user.setQuestProgress(i, 1);
                }
            }
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
        GlowButton commonButton = view.findViewById(R.id.common_button);
        GlowButton rareButton = view.findViewById(R.id.rare_button);
        GlowButton legendaryButton = view.findViewById(R.id.legendary_button);
        GlowButton QrCodeGlow = view.findViewById(R.id.button);
        qrCode = view.findViewById(R.id.qr_code);
        scoreText = view.findViewById(R.id.score);
        nameContentText = view.findViewById(R.id.name_content);
        coinsText = view.findViewById(R.id.currency);
        QrCodeGlow.setVisibility(View.INVISIBLE);
        // Add click listeners to buttons
        commonButton.setOnClickListener(v -> {
            int initialMoney = user.getMoney();
            QRCode code = QRCode.withRarity(Rarity.Common);
            tryPurchaseQRCode(code);
            int FinalMoney = user.getMoney();
            if (initialMoney != FinalMoney) {
                qrCode.setImageBitmap(
                        Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
                );
                scoreText.setText("Score: " + code.getScore());
                nameContentText.setText(code.getName());
                nameContentText.setTextColor(code.getColor());
                coinsText.setText("Coins: " + 1);
                QrCodeGlow.setVisibility(View.VISIBLE);
                QrCodeGlow.setGlowColor(code.getColor());


            }
        });
        rareButton.setOnClickListener(v -> {
            int initialMoney = user.getMoney();
            QRCode code = QRCode.withRarity(Rarity.Rare);
            tryPurchaseQRCode(code);
            int FinalMoney = user.getMoney();
            if (initialMoney != FinalMoney) {
                qrCode.setImageBitmap(
                        Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
                );
                scoreText.setText("Score: " + code.getScore());
                nameContentText.setText(code.getName());
                nameContentText.setTextColor(code.getColor());
                coinsText.setText("Coins: " + 5);
                QrCodeGlow.setVisibility(View.VISIBLE);
                QrCodeGlow.setGlowColor(code.getColor());

            }
        });

        legendaryButton.setOnClickListener(v -> {
            int initialMoney = user.getMoney();
            QRCode code = QRCode.withRarity(Rarity.Legendary);
            tryPurchaseQRCode(code);
            int FinalMoney = user.getMoney();
            if (initialMoney != FinalMoney) {
                qrCode.setImageBitmap(
                        Bitmap.createScaledBitmap(code.getImage(), 250, 250, false)
                );
                scoreText.setText("Score: " + code.getScore());
                nameContentText.setText(code.getName());
                nameContentText.setTextColor(code.getColor());
                coinsText.setText("Coins: " + 10);
                QrCodeGlow.setVisibility(View.VISIBLE);
                QrCodeGlow.setGlowColor(code.getColor());

            }
        });

        return view;
    }
}