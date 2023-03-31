package com.example.qrrush.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserUtil;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * LoginActivity is responsible for managing user login and registration.
 * If the user has already logged in before, the app will skip the login page.
 */
public class LoginActivity extends AppCompatActivity {
    EditText usernameInput;
    ImageButton confirmButton;
    EditText phoneNumberInput;
    TextView errorText;

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Regular expression to match valid phone number formats
        String regex = "^\\+?[1]?[- ]?\\(?[0-9]{3}\\)?[- ]?\\(?[0-9]{3}\\)?[- ]?[0-9]{3,4}$";
        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);
        // Check if the phone number matches the pattern
        if (phoneNumber.isEmpty()) {
            return true;
        }
        return pattern.matcher(phoneNumber).matches();
    }

    /**
     * Initializes the activity and checks if the user has already logged in before. If the user
     * has logged in before, the app will skip the login page and direct the user to the main
     * activity. If the user has not logged in before, the login page will be displayed and register the user
     *
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!UserUtil.isFirstTimeLogin(getApplicationContext())) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return;
        }

        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.username_input);
        confirmButton = findViewById(R.id.confirmButton);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        errorText = findViewById(R.id.error);
        confirmButton.setOnClickListener((view) -> {
            String username = usernameInput.getText().toString();
            String phoneNumber = phoneNumberInput.getText().toString();
            if (!username.matches("")) {
                FirebaseWrapper.getUserData(username, (Optional<User> result) -> {
                    if (result.isPresent()) {
                        // Username is taken, prompt user to pick a new name11
                        errorText.setText("Username is taken!");
                        errorText.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Username is unique, continue with registration process
                    if (username.length() > 10 & !isValidPhoneNumber(phoneNumber)) {
                        errorText.setText("Invalid phone number & Invalid username!");
                        errorText.setVisibility(View.VISIBLE);
                        return;
                    } else if (!isValidPhoneNumber(phoneNumber)) {
                        errorText.setText("Invalid phone number!");
                        errorText.setVisibility(View.VISIBLE);
                        return;
                    } else if (username.length() > 10) {
                        errorText.setText("Username must be less then 10 or less characters!");
                        errorText.setVisibility(View.VISIBLE);
                        return;
                    }


                    errorText.setVisibility(View.GONE);
                    HashMap<String, Object> profiles = new HashMap<>();
                    profiles.put("UUID", UserUtil.generateUUID());
                    profiles.put("phone-number", phoneNumber);
                    profiles.put("rank", 0);
                    profiles.put("score", 0);
                    profiles.put("money", 100);
                    profiles.put("qrcodes", new ArrayList<QRCode>());
                    profiles.put("qrcodescomments", new ArrayList<String>());
                    profiles.put("qrcodespictures", new ArrayList<String>());
                    // Add name + UUID and phone number to FB
                    Task<Void> t = FirebaseWrapper.addData("profiles", username, profiles);
                    while (!t.isComplete()) {
                        // Intentionally empty loop to wait for data to be fetched
                    }

                    // set firstTimeLogin to false
                    UserUtil.setFirstTime(getApplicationContext(), true);
                    UserUtil.setUsername(getApplicationContext(), username);

                    // Start MainActivity
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                });
            }
        });
    }
}