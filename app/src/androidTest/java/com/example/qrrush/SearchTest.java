package com.example.qrrush;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.example.qrrush.view.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The SearchTest class is used to test the search functionality in the
 * MainActivity.
 */
public class SearchTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * This method sets up the test environment by initializing the Solo object.
     *
     * @throws Exception if an error occurs during the setup.
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), activityRule.getActivity());
    }

    /**
     * This method tears down the test environment by finishing all opened
     * activities.
     *
     * @throws Exception if an error occurs during the teardown.
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * This method resets the app by going back to the MainActivity.
     */
    @Before
    public void resetApp() {
        solo.goBackToActivity("MainActivity");
    }

    /**
     * This method tests a failed search by entering an invalid player and checking
     * that the profile is not displayed.
     */
    @Test
    public void testFailedSearch() {
        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);
        solo.clickOnView(tabLayout.getTabAt(3).view);
        // Click the search player edit text
        solo.clickOnView(solo.getView(R.id.searchPlayer));
        // Enter "mdncv" as the search term
        solo.enterText(0, "mdncv");
        // Assert that the "No player found!" text is displayed
        assertTrue(solo.waitForText("No players found!", 1, 2000));
    }

    /**
     * This method tests a successful search by entering a valid player and checking
     * that the profile is displayed.
     */
    @Test
    public void testSuccessfulSearch() {
        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);
        solo.clickOnView(tabLayout.getTabAt(3).view);
        solo.clickOnView(solo.getView(R.id.searchPlayer));
        String validUserName = "ValidUser";
        solo.enterText(0, validUserName);
        solo.sleep(2000);
        ListView searchResultsList = (ListView) solo.getView(R.id.searchPlayerList);
        assertEquals(1, searchResultsList.getAdapter().getCount());
        TextView searchedUserNameTextView = (TextView) searchResultsList.getChildAt(0)
                .findViewById(R.id.nameViewSocial);
        assertEquals(validUserName, searchedUserNameTextView.getText().toString());
        solo.clickOnView(searchResultsList.getChildAt(0));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(solo.getView(R.id.profileView));
    }
}