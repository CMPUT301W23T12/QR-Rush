package com.example.qrrush.view;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.qrrush.R;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Quest;
import com.example.qrrush.model.QuestType;
import com.example.qrrush.model.User;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.ArrayList;

/**
 * this class is responsible for
 * The fragment displaying the screen after a QR code is scanned and asking the user to
 * confirm that they want to add the QR code to their account.
 */
public class QRConfirmFragment extends DialogFragment {
    User user;
    Barcode code;
    ImageButton retakeButton;
    ImageButton confirmButton;
    ImageView qrCodeImage;
    TextView nameView;
    TextView scoreView;
    TextView rarityView;
    TextView locationView;
    CheckBox geolocationToggle;
    FragmentManager manager;
    Runnable onDismiss;

    public QRConfirmFragment(User user, Barcode b, Runnable onDismiss) {
        this.user = user;
        this.code = b;
        this.onDismiss = onDismiss;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        this.onDismiss.run();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retakeButton = (ImageButton) view.findViewById(R.id.retake_button);
        confirmButton = (ImageButton) view.findViewById(R.id.qr_confirm_button);
        qrCodeImage = view.findViewById(R.id.qr_code_image);
        nameView = view.findViewById(R.id.qrCode_name);
        scoreView = view.findViewById(R.id.qrCode_score);
        rarityView = view.findViewById(R.id.qrCode_rarity);
        locationView = view.findViewById(R.id.qrCode_location);
        geolocationToggle = view.findViewById(R.id.geolocation_checkbox);

        QRCode qrCode = new QRCode(this.code.getRawBytes());

        retakeButton.setOnClickListener(v -> {
            new CameraFragment(user, onDismiss).show(manager, "Confirm QR code");
            dismiss();
        });

        confirmButton.setOnClickListener(v -> {
            Geo.getCurrentLocation(l -> {
                ArrayList<Quest> quests = Quest.getCurrentQuests();
                ArrayList<Integer> progress = user.getQuestProgress();

                for (int i = 0; i < quests.size(); i += 1) {
                    Quest quest = quests.get(i);
                    int current = progress.get(i);

                    if (quest.getType() == QuestType.ScanNCodes && current <= quest.getN()) {
                        user.setQuestProgress(i, current + 1);
                        continue;
                    }

                    if (quest.getType() == QuestType.ScanCodeOfRarity &&
                            qrCode.getRarity() == quest.getRarity() &&
                            current == 0) {
                        Log.e("Quest", "Quest progress");
                        user.setQuestProgress(i, 1);
                        continue;
                    }

                    if (quest.getType() == QuestType.SaveGeolocationForNCodes &&
                            geolocationToggle.isChecked() &&
                            current <= quest.getN()) {
                        user.setQuestProgress(i, current + 1);
                        continue;
                    }
                }

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
