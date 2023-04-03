package com.example.qrrush.view;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * this class is responsible for
 * The fragment displaying the screen after a QR code is scanned and asking the user to
 * confirm that they want to add the QR code to their account.
 */
public class QRConfirmFragment extends DialogFragment {
    User user;
    Barcode code;
    QRCode qrCode;
    ImageButton retakeButton;
    ImageButton confirmButton;
    ImageView qrCodeImage;
    TextView nameView;
    TextView scoreView;
    TextView rarityView;
    TextView locationView;
    TextView uploadingText;
    CheckBox geolocationToggle;
    FragmentManager manager;
    Runnable onDismiss;
    byte[] picture = null;
    ImageButton foundLocationButton;
    ImageView locationImage;
    private static final int CAMERA_REQUEST_CODE = 100;

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
        foundLocationButton = view.findViewById(R.id.foundLocation);
        locationImage = view.findViewById(R.id.locationImage);
        uploadingText = view.findViewById(R.id.uploadingText);
        uploadingText.setVisibility(View.GONE);

        qrCode = new QRCode(this.code.getRawBytes());

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

                if (picture != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imagesRef = storage.getReference()
                            .child("qrcodeimage/" + user.getUserName() + ".jpg");

                    // Upload the image data to Firebase Storage
                    UploadTask uploadTask = imagesRef.putBytes(picture);

                    // display the uploading... message after clicking confirm
                    setCancelable(false);
                    uploadingText.setVisibility(View.VISIBLE);

                    // Register observers to listen for when the upload is done or if it fails
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        // Now we need to send it to firebase wrapper
                        imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            this.qrCode.setLocationImage(uri.toString());
                            user.addQRCode(qrCode);
                            dismiss();
                        });

                    }).addOnFailureListener(err -> {
                        // Image upload failed
                        Toast.makeText(
                                getContext(),
                                "Image upload failed: " + err.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
                } else {
                    user.addQRCode(qrCode);
                    dismiss();
                }
            });
        });


        foundLocationButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the camera activity and wait for the result
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
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
                locationView.setText(String.format(
                        Locale.ENGLISH,
                        "%.6f, %.6f",
                        l.getLongitude(),
                        l.getLatitude()
                ));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CAMERA_REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }

        // Get the image data from the intent
        Bitmap photo = (Bitmap) data.getExtras().get("data");

        // Display the image in an ImageView
        locationImage.setVisibility(View.VISIBLE);
        // correct the positioning on the emulator -> devices?
        // Device is fine
        // locationImage.setRotation(270);
        locationImage.setImageBitmap(photo);
        // Convert the image to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, CAMERA_REQUEST_CODE, baos);
        byte[] imageData = baos.toByteArray();
        picture = imageData;
    }
}
