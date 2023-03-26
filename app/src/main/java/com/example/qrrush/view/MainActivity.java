package com.example.qrrush.view;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qrrush.R;
import com.example.qrrush.controller.RankComparator;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;


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
    ArrayList<User> users;
    private FirebaseFirestore firestore;
    TextView loadingText;

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
        //TODO Get the players from firebase and remove hardcoded values/ need to use Users instead of Players

        loadingText = findViewById(R.id.main_loading_text);
        loadingText.setVisibility(View.VISIBLE);

        // Get everything from firebase
        FirebaseWrapper.getUserData(username, firebaseUser -> {
            user = firebaseUser.get();
            users = getAllCollection(user);
            mainView = findViewById(R.id.main_view);
            profileButton = (ImageButton) findViewById(R.id.profile_button);
            shopButton = (ImageButton) findViewById(R.id.shop_button);
            socialButton = (ImageButton) findViewById(R.id.social_button);
            mainButton = (ImageButton) findViewById(R.id.main_button);
            leaderboardButton = (ImageButton) findViewById(R.id.leaderboard_button);
            // User object passes into each fragment constructor
            profileButton.setOnClickListener((v) -> {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_view, new ProfileFragment(user, true)).commit();
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
                        .replace(R.id.main_view, new LeaderboardFragment(user,users)).commit();
            });

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new MainFragment(user)).commit();
        });
    }

    public void removeLoadingScreen() {
        this.loadingText.setVisibility(View.GONE);
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
    public ArrayList<User> getAllCollection(User user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<User> users = new ArrayList<User>();
        db.collection("profiles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {

                            users.clear();
                            for (QueryDocumentSnapshot document : value) {
                                Log.d("FirebaseWrapper", document.getId() + " => " + document.getData());
                                if (!user.getUserName().matches(document.getId())){
                                    users.add(new User(document.getId(),
                                            "",
                                            0,
                                            ((Long) document.getData().get("score")).intValue(),
                                            new ArrayList<>(),
                                            0));
                                } else{
                                    user.setTotalScore(((Long) document.getData().get("score")).intValue());
                                    users.add(user);
                                }
                            }
                            Collections.sort(users, new RankComparator());
                            for (int i = 0; i < users.size(); ++i){
                                users.get(i).setRank(users.indexOf(users.get(i)));
                            }
                        } else {
                            Log.d("FirebaseWrapper", "Error getting documents: ",error.fillInStackTrace());
                        }
                    }
                });
        return users;
    }

}