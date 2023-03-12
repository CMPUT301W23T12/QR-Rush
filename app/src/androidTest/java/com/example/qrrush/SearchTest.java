package com.example.qrrush;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SearchTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), activityRule.getActivity());
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
    public void testFailedSearch() {
        // Click the social button
        solo.clickOnView(solo.getView(R.id.social_button));
        // Click the search player edit text
        solo.clickOnView(solo.getView(R.id.searchPlayer));
        // Enter "mdncv" as the search term
        solo.enterText(0, "mdncv");
        // Click the search button
        solo.clickOnView(solo.getView(R.id.searchButton));
        // Assert that the "No player found!" text is displayed
        assertTrue(solo.waitForText("Name", 1, 2000));
        assertTrue(solo.waitForText("Rank", 1, 2000));
    }

    @Test
    public void testSuccessfulSearch() {
        // Click the social button
        solo.clickOnView(solo.getView(R.id.social_button));
        // Click the search player edit text
        solo.clickOnView(solo.getView(R.id.searchPlayer));
        // Enter "Bruh123" as the search term
        solo.enterText(0, "Bruh123");
        // Click the search button
        solo.clickOnView(solo.getView(R.id.searchButton));
        // Assert that the profile is displayed
        assertFalse(solo.waitForText("Name", 1, 2000));
        assertFalse(solo.waitForText("Rank", 1, 2000));
    }
}