package com.example.qrrush;

import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.IdlingResource;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrrush.view.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The MapTest class is used to test the display of a map in the MainActivity.
 */
public class MapTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, false);

    /**
     * This method sets up the test environment by launching the MainActivity and initializing the Solo object.
     *
     * @throws Exception if an error occurs during the setup.
     */
    @Before
    public void setUp() throws Exception {
        // Start the activity under test explicitly
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Intent intent = new Intent(instrumentation.getTargetContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity activity = rule.launchActivity(intent);

        // Initialize the Solo object
        solo = new Solo(instrumentation, activity);
    }

    /**
     * This method tears down the test environment by finishing all opened activities.
     *
     * @throws Exception if an error occurs during the teardown.
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * This method tests that a map is displayed in the MainActivity.
     *
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void testMapIsDisplayed() throws Exception {
        // Wait for the map to be displayed
        assertNotNull(solo.waitForView(com.google.android.gms.maps.MapView.class, 0, 5000));
    }
    @Test
    public void testQuest(){
        assertNotNull(solo.searchText("Daily Quests"));
    }
}

