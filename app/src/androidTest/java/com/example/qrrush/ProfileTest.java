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
    public void testQRcount(){
        try {
            Thread.sleep(10000); // waits for 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        solo.clickOnView(solo.getView(R.id.profile_button));
        TextView qrCountView = (TextView) solo.getView(R.id.qrCodesView);

        // Get the text from the QR code count view
        String qrCountText = qrCountView.getText().toString();

        // Extract the numeric part of the text
        String numericPart = qrCountText.replaceAll("[^0-9]", "");

        // Convert the numeric part to an integer
        int qrCountSub = Integer.parseInt(numericPart);


        solo.clickOnView(solo.getView(R.id.shop_button));
        solo.clickOnView(solo.getView(R.id.common_button));
        solo.clickOnView(solo.getView(R.id.rare_button));
        solo.clickOnView(solo.getView(R.id.profile_button));
        solo.waitForView(R.id.qrCodesView);


// Check if the view appeared within the timeout period
        //assertTrue(viewAppeared);
        solo.waitForView(R.id.profile_button);
        // Get the QR code count view
        TextView qrCountView1 = (TextView) solo.getView(R.id.qrCodesView);

        // Get the text from the QR code count view
        String qrCountText1 = qrCountView1.getText().toString();

        // Extract the numeric part of the text
        String numericPart1 = qrCountText1.replaceAll("[^0-9]", "");

        // Convert the numeric part to an integer
        int qrCount = Integer.parseInt(numericPart1);
        int qrCOUNT = qrCount - qrCountSub;

        // Compare the QR code count value to the expected value

        assertEquals(2, qrCOUNT);
        solo.clickOnButton("Delete");
        solo.waitForView(R.id.deleteButton);
        solo.waitForView(R.id.qrCodesView);
        TextView qrCountView2 = (TextView) solo.getView(R.id.qrCodesView);

        // Get the text from the QR code count view
        String qrCountText2 = qrCountView2.getText().toString();

        // Extract the numeric part of the text
        String numericPart2 = qrCountText2.replaceAll("[^0-9]", "");

        // Convert the numeric part to an integer
        int qrCount2 = Integer.parseInt(numericPart2);

        // Compare the QR code count value to the expected value
        assertEquals(1, qrCount - qrCount2);
        solo.clickOnButton("Delete");



        //edit name
        solo.waitForCondition(() -> solo.getView(R.id.edit_name) != null, 5000);
        solo.clickOnView(solo.getView(R.id.edit_name));
        // Enter "Bruh123" as the search term
        solo.enterText(0, "Bruh1234");
        solo.clickOnText("Confirm");

        // Wait for the AlertDialog to close
        solo.waitForDialogToClose();
        TextView newName = (TextView) solo.getView(R.id.nameView);
        String name = newName.getText().toString();
        assertEquals("Bruh1234", name);
        solo.waitForCondition(() -> solo.getView(R.id.edit_name) != null, 5000);





        solo.clickOnView(solo.getView(R.id.edit_name));
        // Enter "Bruh123" as the search term
        solo.enterText(0, "attn");
        solo.clickOnText("Confirm");
        solo.waitForDialogToClose();
        TextView newName3 = (TextView) solo.getView(R.id.nameView);
        String name2 = newName3.getText().toString();
        assertEquals("attn", name2);
        solo.waitForCondition(() -> solo.getView(R.id.edit_name) != null, 5000);
        //make it error
        solo.clickOnView(solo.getView(R.id.edit_name));
        // Enter "Bruh123" as the search term
        solo.enterText(0, "Bruh123");
        solo.clickOnText("Confirm");

        TextView newName2 = (TextView) solo.getView(R.id.errorText);
        assertTrue(newName2.getVisibility() == View.VISIBLE);

    }

}

