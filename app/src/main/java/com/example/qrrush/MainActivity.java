package com.example.qrrush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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