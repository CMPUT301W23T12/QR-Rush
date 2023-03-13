package com.example.qrrush;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrrush.view.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * A test class for the camera functionality in the app. Uses the Solo framework to simulate user interactions
 * and verify expected behavior.
 */
public class CameraTest {

    private Solo solo; // An instance of Solo to simulate user interactions

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    /**
     * Sets up the test environment before running the test cases. Launches the MainActivity and initializes
     * the Solo instance.
     *
     * @throws Exception if an error occurs during setup
     */
    @Before
    public void setUp() throws Exception {
        // Launch the activity manually
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(context, MainActivity.class);
        activityTestRule.launchActivity(intent);

        // Initialize solo instance
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), activityTestRule.getActivity());
    }

    /**
     * Tears down the test environment after running the test cases. Finishes all opened activities.
     *
     * @throws Exception if an error occurs during teardown
     */
    @After
    public void tearDown() throws Exception {
        // Finish the activity after each test
        solo.finishOpenedActivities();
    }

    /**
     * Tests the functionality of the camera fragment. Simulates clicking on the camera button,
     * waits for the camera view to appear, and then verifies that the camera view is displayed.
     */
    @Test
    public void testCameraFragment() {
        // Open the camera fragment by clicking the camera button
        solo.clickOnButton("Camera");

        // Wait for the camera view to appear
        solo.waitForView(R.id.camera_view);

        // Check that the camera view is displayed
        assertTrue(solo.getView(R.id.camera_view).isShown());
    }
}