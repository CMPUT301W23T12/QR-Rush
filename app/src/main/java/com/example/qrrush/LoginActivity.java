package com.example.qrrush;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText usernameInput;
    Button confirmButton;
    EditText phoneNumberInput;
    TextView errorText;

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
        errorText = findViewById(R.id.username_error);

        confirmButton.setOnClickListener((view) -> {
            String username = usernameInput.getText().toString();
            String phoneNumber = phoneNumberInput.getText().toString();

            FirebaseWrapper.checkUsernameAvailability(username, (Task<QuerySnapshot> task) -> {
                if (!task.isSuccessful()) {
                    // Error occurred while querying database
                    Log.e("Firebase", "ERROR QUERYING DATABASE WHILE SEARCHING PROFILES COLLECTION");
                    return;
                }

                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot.size() > 0) {
                    // Username is taken, prompt user to pick a new name
                    errorText.setVisibility(View.VISIBLE);
                    return;
                }

                // Username is unique, continue with registration process
                HashMap<String, Object> profiles = new HashMap<>();
                profiles.put(username, UserUtil.generateUUID());
                profiles.put("phone-number", phoneNumber);
                profiles.put("rank", 0);
                profiles.put("score", 0);
                profiles.put("qrcodes", new ArrayList<QRCode>());
                // Add name + UUID and phone number to FB
                FirebaseWrapper.addData("profiles", username, profiles);

                // set firstTimeLogin to false
                UserUtil.setFirstTime(getApplicationContext(), true);
                UserUtil.setUsername(getApplicationContext(), username);

                // Start MainActivity
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            });
        });

    }
}