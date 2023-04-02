package com.example.qrrush;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrrush.view.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class leaderboardsTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), activityTestRule.getActivity());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testScannedQRCodeButtonClick() {
        // Ensure the ViewPager2 is present
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);
        solo.clickOnView(tabLayout.getTabAt(4).view);
        solo.sleep(500);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        solo.clickOnText("SCANNED QR CODES");
        solo.clickOnButton(R.id.button4);

        // Assert that the string "no location available" is not on the page
        assertFalse(solo.searchText("no location available"));
    }
}
