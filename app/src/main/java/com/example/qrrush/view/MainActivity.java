package com.example.qrrush.view;

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

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;


/**
 * The main activity class for the QR Rush app.
 * This activity serves as the entry point to the app and handles the main UI and user interactions.
 * This class also sets up the main User object that that other fragments will be using via constructor
 */
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

    /**
     * Checks if the necessary permissions for the app have been granted by the user.
     *
     * @return true if all permissions have been granted, false otherwise
     */
    private boolean hasPermissions() {
        for (String permission : MainActivity.PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                Log.e("Permission", "Error with permissions");
                return false;
            }
        }
        return true;
    }

    /**
     * Called when the user responds to the permission request dialog.
     * Checks if the necessary permissions have been granted and initializes the app if so.
     *
     * @param requestCode  The code that was used to make the permission request
     * @param permissions  The requested permissions
     * @param grantResults The grant results for the corresponding permissions
     */
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

    @Override
    public void onBackPressed() {
    }

    /**
     * Initializes the app and sets up the main UI components.
     * Retrieves user data from Firebase and populates the UI with it.
     */
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

            user = new User(
                    username,
                    document.getString("phone-number"),
                    document.getLong("rank").intValue(),
                    document.getLong("score").intValue(),
                    new ArrayList<>()
            );

            ArrayList<String> hashes = (ArrayList<String>) document.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) document.get("qrcodescomments");
            for (String hash : hashes) {
                Task<DocumentSnapshot> t = FirebaseWrapper.getQRCodeData(hash, (Task<DocumentSnapshot> task1) -> {
                    if (!task1.isSuccessful()) {
                        Log.d("Firebase User", "Error creating user");
                        return;
                    }

                    DocumentSnapshot document1 = task1.getResult();
                    if (!document1.exists()) {
                        Log.e("Firebase User", "Document doesn't exist!");
                        return;
                    }

                    GeoPoint g = (GeoPoint) document.get("location");
                    Timestamp timestamp = (Timestamp) document1.get("date");
                    QRCode code = new QRCode(hash, timestamp);
                    if (g != null) {
                        Location l = new Location("");
                        l.setLatitude(g.getLatitude());
                        l.setLongitude(g.getLongitude());
                        code.setLocation(l);
                    }

                    user.addQRCodeWithoutFirebase(code);
                    if (comments.size() > 0 && comments.size() >= hashes.size()) {
                        user.setCommentWithoutUsingFirebase(code, comments.get(hashes.indexOf(hash)));
                    }
                });

                while (!t.isComplete()) {
                    // Empty loop is on purpose. We need to wait for these to finish.
                }
            }

            mainView = findViewById(R.id.main_view);
            profileButton = (ImageButton) findViewById(R.id.profile_button);
            shopButton = (ImageButton) findViewById(R.id.shop_button);
            socialButton = (ImageButton) findViewById(R.id.social_button);
            mainButton = (ImageButton) findViewById(R.id.main_button);
            leaderboardButton = (ImageButton) findViewById(R.id.leaderboard_button);
            // User object passes into each fragment constructor
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