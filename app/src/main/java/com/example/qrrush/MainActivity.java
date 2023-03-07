package com.example.qrrush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    View mainView;

    Button profileButton;
    Button shopButton;
    Button mainButton;
    Button socialButton;
    Button leaderboardButton;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Testing new commit into branch


        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        // Get Fire store instance
        firestore = FirebaseFirestore.getInstance();


        mainView = findViewById(R.id.main_view);
        profileButton = findViewById(R.id.profile_button);
        shopButton = findViewById(R.id.shop_button);
        socialButton = findViewById(R.id.social_button);
        mainButton = findViewById(R.id.main_button);
        leaderboardButton = findViewById(R.id.leaderboard_button);


        if (UserUtil.isFirstTimeLogin(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.first_time_launch_popup, null);
            EditText usernameEditText = dialogView.findViewById(R.id.username_input);
            EditText phoneNumberEditText = dialogView.findViewById(R.id.phone_number_input);
            TextView username_Taken = dialogView.findViewById(R.id.username_error);
            builder.setTitle("Enter your information");
            builder.setView(dialogView);

            builder.setPositiveButton("OK", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(v -> {
                        String username = usernameEditText.getText().toString();
                        String phoneNumber = phoneNumberEditText.getText().toString();
                        FirebaseWrapper.checkUsernameAvailability(username, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.size() > 0) {
                                        // Username is taken, prompt user to pick a new name
                                        username_Taken.setVisibility(dialogView.VISIBLE);
                                    } else {
                                        // Username is unique, continue with registration process
                                        HashMap<String, Object> profiles = new HashMap<>();
                                        profiles.put(username, UserUtil.generateUUID());
                                        profiles.put("phone-number", phoneNumber);
                                        // Add name + UUID and phonenumber to FB
                                        FirebaseWrapper.addData("profiles", username, profiles);
                                        dialog.dismiss();

                                        // set firstTimeLogin to false

                                        UserUtil.setFirstTime(MainActivity.this, true);
                                    }
                                }
                                else {
                                    // Error occurred while querying database
                                    Log.e("Firebase", "ERROR QUERYING DATABASE WHILE SEARCHING PROFILES COLLECTION");
                                }
                            }
                        });
                    });
                }
            });
            alertDialog.show();
        }
        profileButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new ProfileFragment()).commit();
        });

        shopButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new ShopFragment()).commit();
        });

        socialButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new SocialFragment()).commit();
        });

        mainButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new MainFragment()).commit();
        });

        leaderboardButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new LeaderboardFragment()).commit();
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_view, new MainFragment()).commit();
    }
}