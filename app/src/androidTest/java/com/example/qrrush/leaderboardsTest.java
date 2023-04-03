package com.example.qrrush;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        solo.clickOnText("Scanned QR Codes");


        // Assert that the string "no location available" is not on the page
        assertFalse(solo.searchText("no location available"));
    }
    @Test
    public void testLeaderBoardRanking(){
        // Ensure the ViewPager2 is present
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);
        solo.clickOnView(tabLayout.getTabAt(4).view);

        // Add a delay to ensure the fragment has loaded
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get the scores and ranks for user 1, user 2, and user 3
        View user1View = solo.getView(R.id.top_user1);
        View user2View = solo.getView(R.id.top_user2);
        View user3View = solo.getView(R.id.top_user3);

        TextView user1ScoreTextView = user1View.findViewById(R.id.OtherUserScoreView);
        TextView user2ScoreTextView = user2View.findViewById(R.id.OtherUserScoreView);
        TextView user3ScoreTextView = user3View.findViewById(R.id.OtherUserScoreView);

        TextView user1RankTextView = user1View.findViewById(R.id.rankTextView);
        TextView user2RankTextView = user2View.findViewById(R.id.rankTextView);

        int user1Score = Integer.parseInt(user1ScoreTextView.getText().toString());
        int user2Score = Integer.parseInt(user2ScoreTextView.getText().toString());
        int user3Score = Integer.parseInt(user3ScoreTextView.getText().toString());

        int user1Rank = Integer.parseInt(user1RankTextView.getText().toString());
        int user2Rank = Integer.parseInt(user2RankTextView.getText().toString());

        assertTrue(user1Score >= user2Score);
        assertFalse(user2Score < user3Score);
        assertEquals(1, user1Rank);
        assertNotEquals(3, user2Rank);
    }
    @Test
    public void testClickOnUser() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);
        solo.clickOnView(tabLayout.getTabAt(4).view);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View user1View = solo.getView(R.id.top_user1);

        solo.clickOnView(user1View);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(solo.getView(R.id.profileView));

    }
}
