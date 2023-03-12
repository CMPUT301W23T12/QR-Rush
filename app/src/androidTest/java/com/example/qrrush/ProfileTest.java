package com.example.qrrush;


import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

public class ProfileTest {
    //public User user = new User("attn", "123")
    private Solo solo; // An instance of Solo to simulate user interactions
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);
    @Before
    public void setUp() throws Exception {
        // Launch the activity manually
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(context, MainActivity.class);
        activityTestRule.launchActivity(intent);

        // Initialize solo instance
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), activityTestRule.getActivity());
    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
    @Before
    public void resetApp() {
        solo.goBackToActivity("MainActivity");
    }

    @Test
    public void testQRcount(){
        solo.clickOnView(solo.getView(R.id.shop_button));
        solo.clickOnView(solo.getView(R.id.common_button));
        solo.clickOnView(solo.getView(R.id.rare_button));
        solo.clickOnView(solo.getView(R.id.profile_button));

        // Get the QR code count view
        TextView qrCountView = (TextView) solo.getView(R.id.qrCodesView);

        // Get the QR code count value
        int qrCount = Integer.parseInt(qrCountView.getText().toString());

        // Compare the QR code count value to the expected value
        assertEquals(2, qrCount);



//        int totalScore = user1.getTotalScore();
//        int numQRcodes = user1.getNumberOfQRCodes();
//        int countQR = 0;
//        int countScore = 0;
//        ArrayList<QRCode> qrCodes = user1.getQRCodes();
//        for (QRCode qrCode : qrCodes) {
//            countScore += qrCode.getScore();
//            countQR += 1;
//        }
//
//        assertTrue(countQR == numQRcodes);
//        assertTrue(countScore == totalScore);


    }
}
