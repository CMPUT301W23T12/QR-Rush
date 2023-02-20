package com.example.qrrush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class  MainActivity extends AppCompatActivity {

    View mainView;

    Button profileButton;
    Button shopButton;
    Button mainButton;
    Button socialButton;
    Button leaderboardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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