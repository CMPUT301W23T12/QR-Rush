package com.example.qrrush.view;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.Geo;
import com.example.qrrush.model.MyViewPagerAdapater;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserUtil;
import com.google.android.material.tabs.TabLayout;
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
 * This activity serves as the entry point to the app and handles the main UI
 * and user interactions.
 * This class also sets up the main User object that that other fragments will
 * be using via constructor
 */
public class MainActivity extends AppCompatActivity {
    User user;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    MyViewPagerAdapater myViewPagerAdapater;
    TextView loadingText;

    static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    /**
     * Checks if the necessary permissions for the app have been granted by the
     * user.
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
     * Checks if the necessary permissions have been granted and initializes the app
     * if so.
     *
     * @param requestCode  The code that was used to make the permission request
     * @param permissions  The requested permissions
     * @param grantResults The grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

        setContentView(R.layout.activity_main);

        loadingText = findViewById(R.id.loading_text);

        // Retrieve data from Firebase:
        Log.d("TAG", UserUtil.getUsername(MainActivity.this));
        String username = UserUtil.getUsername(getApplicationContext());

        // Get everything from firebase
        FirebaseWrapper.getUserData(username, firebaseUser -> {
            user = firebaseUser.get();
            user.setActivity(this);

            // TabLayout//Viewpager2 allows swiping and icons to be
            // highlighted at the bottom
            tabLayout = findViewById(R.id.tabLayout);
            viewPager2 = findViewById(R.id.view_pager);
            myViewPagerAdapater = new MyViewPagerAdapater(this, user);
            viewPager2.setAdapter(myViewPagerAdapater);

            tabLayout.getTabAt(0).setIcon(R.drawable.profile);
            tabLayout.getTabAt(1).setIcon(R.drawable.shop);
            tabLayout.getTabAt(2).setIcon(R.drawable.main);
            tabLayout.getTabAt(3).setIcon(R.drawable.social);
            tabLayout.getTabAt(4).setIcon(R.drawable.leaderboard);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager2.setCurrentItem(tab.getPosition());
                    viewPager2.setUserInputEnabled(tab.getPosition() != 2);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tabLayout.getTabAt(position).select();
                }
            });

            viewPager2.setCurrentItem(2);
            loadingText.setVisibility(View.GONE);

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_permission);

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
            Log.e("Permission", "!hasPermissions line 166");
            return;
        }
        main();
    }

}
