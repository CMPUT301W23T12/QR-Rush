package com.example.qrrush;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    View mainView;
    Button profileButton;
    Button shopButton;
    Button mainButton;
    Button socialButton;
    Button leaderboardButton;
    User user;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        // Get Fire store instance
        firestore = FirebaseFirestore.getInstance();

        // Retrieve data from Firebase:
        Log.d("TAG", UserUtil.getUsername(MainActivity.this));
        String username = UserUtil.getUsername(getApplicationContext());

        ArrayList<QRCode> qrCodes = new ArrayList<>();
        // Get everything from firebase
        FirebaseWrapper.getUserData(username, (Task<DocumentSnapshot> task) -> {
            if (!task.isSuccessful()) {
                Log.d("Firebase User", "Error creating user");
                return;
            }

            DocumentSnapshot document = task.getResult();
            if (!document.exists()) {
                Log.e("Firebase User", "Document doesn't exist!");
                return;
            }

            user = new User(username,
                    document.getString("phone-number"),
                    document.getLong("rank").intValue(),
                    document.getLong("score").intValue(),
                    qrCodes);
        });

        mainView = findViewById(R.id.main_view);
        profileButton = findViewById(R.id.profile_button);
        shopButton = findViewById(R.id.shop_button);
        socialButton = findViewById(R.id.social_button);
        mainButton = findViewById(R.id.main_button);
        leaderboardButton = findViewById(R.id.leaderboard_button);

        profileButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new ProfileFragment(user)).commit();
        });

        shopButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new ShopFragment(user)).commit();
        });

        socialButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new SocialFragment(user)).commit();
        });

        mainButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new MainFragment(user)).commit();
        });

        leaderboardButton.setOnClickListener((v) -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new LeaderboardFragment(user)).commit();
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_view, new MainFragment(user)).commit();
    }
}