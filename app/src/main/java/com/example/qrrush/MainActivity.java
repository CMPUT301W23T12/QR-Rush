package com.example.qrrush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class  MainActivity extends AppCompatActivity {

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

    static final String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    private boolean hasPermissions() {
        for (String permission : MainActivity.PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: If they say no, explain what the permissions are for and explain that they are
        //  needed for the app to work?

        // TODO: Maybe ask for location separately since its not necessary?
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }

        Geo.initGeolocation(this);

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
        });

    }
}