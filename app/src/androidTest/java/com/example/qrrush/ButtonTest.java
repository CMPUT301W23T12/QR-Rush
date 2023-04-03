package com.example.qrrush;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrrush.view.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
public class ButtonTest {
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
    public void testSwipeButtonFragmentChanges() {
        // Ensure the ViewPager2 is present
        assertTrue(solo.waitForView(R.id.view_pager));

        // Get the TabLayout
        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);

        // Click on each tab and verify the ViewPager2's current item changes accordingly
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            solo.clickOnView(tabLayout.getTabAt(i).view);
            solo.sleep(500); // Add a delay to allow for fragment transitions
            assertEquals(i, ((ViewPager2) solo.getView(R.id.view_pager)).getCurrentItem());
        }
    }
}

