package com.example.qrrush;


import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrrush.view.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

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
    public void testQRcount() {


        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);
        solo.clickOnView(tabLayout.getTabAt(0).view);
        TextView qrCountView = (TextView) solo.getView(R.id.qrCodesView);
        // Get the text from the QR code count view
        String qrCountText = qrCountView.getText().toString();
        Log.e("count", qrCountText);
        // Extract the numeric part of the text
        String numericPart = qrCountText.replaceAll("[^0-9]", "");
        Log.e("count", numericPart);
        // Convert the numeric part to an integer
        int qrCountSub = Integer.parseInt(numericPart);

        TextView qrMoney = (TextView) solo.getView(R.id.moneyView);
        int rushMoney = Integer.parseInt(qrMoney.getText().toString());




        solo.clickOnView(tabLayout.getTabAt(1).view);
        solo.clickOnView(solo.getView(R.id.common_button));
        solo.sleep(1000);
        solo.waitForView(R.id.common_button);
        solo.clickOnView(solo.getView(R.id.rare_button));
        solo.sleep(1000);
        solo.waitForView(R.id.rare_button);

        solo.clickOnView(tabLayout.getTabAt(0).view);
        solo.waitForView(R.id.qrCodesView);


// Check if the view appeared within the timeout period
        //assertTrue(viewAppeared);
        solo.waitForView(R.id.profile_swipe);
        // Get the QR code count view
        TextView qrMoney1 = (TextView) solo.getView(R.id.moneyView);
        int rushMoney1 = Integer.parseInt(qrMoney1.getText().toString());

        TextView qrCountView1 = (TextView) solo.getView(R.id.qrCodesView);

        // Get the text from the QR code count view
        String qrCountText1 = qrCountView1.getText().toString();
        Log.e("count", qrCountText1);
        // Extract the numeric part of the text
        String numericPart1 = qrCountText1.replaceAll("[^0-9]", "");
        Log.e("count", numericPart1);

        // Convert the numeric part to an integer
        int qrCount = Integer.parseInt(qrCountText1);
        int qrCOUNT = qrCount - qrCountSub;
        // 2-0
        // Compare the QR code count value to the expected value
        int moneyRush = rushMoney - rushMoney1;
        assertEquals(2, qrCOUNT);
        assertEquals(6,moneyRush);
        solo.clickOnImageButton(0);
        solo.sleep(2000);
        solo.waitForView(R.id.qrCodesView);
        TextView qrCountView2 = (TextView) solo.getView(R.id.qrCodesView);

        // Get the text from the QR code count view
        String qrCountText2 = qrCountView2.getText().toString();

        // Extract the numeric part of the text
        String numericPart2 = qrCountText2.replaceAll("[^0-9]", "");

        // Convert the numeric part to an integer
        int qrCount2 = Integer.parseInt(qrCountText);
        int qrCount3 = Integer.parseInt(qrCountText2);

        // Compare the QR code count value to the expected value
        assertEquals(1, qrCount3 - qrCount2);
        solo.clickOnImageButton(0);
    }



}

