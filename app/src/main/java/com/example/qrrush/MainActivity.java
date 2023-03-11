package com.example.qrrush;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    View mainView;
    ImageButton profileButton;
    ImageButton shopButton;
    ImageButton mainButton;
    ImageButton socialButton;
    ImageButton leaderboardButton;
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
                Log.e("Permission", "Error with permissions");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // TODO: Check if each permission is actually granted. Do we have to do this?

        if (requestCode != 101) {
            Log.e("MainActivity", "Permissions maybe not granted?");
            return;
        }

        main();
    }

    private void main() {
        Geo.initGeolocation(this);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Get Fire store instance
        firestore = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_main);

        // Retrieve data from Firebase:
        Log.d("TAG", UserUtil.getUsername(MainActivity.this));
        String username = UserUtil.getUsername(getApplicationContext());

        // Get everything from firebase
        // TODO: show a loading animation while we get everything from firebase, then load the UI
        //       once its done.
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

            ArrayList<String> hashes = (ArrayList<String>) document.get("qrcodes");
            ArrayList<QRCode> qrCodes = new ArrayList<>();
            ArrayList<String> comments = (ArrayList<String>) document.get("qrcodescomments");
            HashMap<String, ArrayList<String>> firebaseComment = new HashMap<>();

            for (String hash : hashes) {
                QRCode code = new QRCode(hash);
                GeoPoint g = (GeoPoint) document.get("location");
                if (g != null) {
                    Location l = new Location("");
                    l.setLatitude(g.getLatitude());
                    l.setLongitude(g.getLongitude());
                    code.setLocation(l);
                }
                qrCodes.add(code);
                comments.add("");

            }
            user = new User(
                    username,
                    document.getString("phone-number"),
                    document.getLong("rank").intValue(),
                    document.getLong("score").intValue(),
                    qrCodes
            );

            mainView = findViewById(R.id.main_view);
            profileButton = (ImageButton) findViewById(R.id.profile_button);
            shopButton = (ImageButton) findViewById(R.id.shop_button);
            socialButton = (ImageButton) findViewById(R.id.social_button);
            mainButton = (ImageButton) findViewById(R.id.main_button);
            leaderboardButton = (ImageButton) findViewById(R.id.leaderboard_button);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_permission);

        // TODO: If they say no, explain what the permissions are for and explain that they are
        //  needed for the app to work?

        // TODO: Maybe ask for location separately since its not necessary?
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
            Log.e("Permission", "!hasPermissions line 166");
            return;
        }

        main();
    }


}