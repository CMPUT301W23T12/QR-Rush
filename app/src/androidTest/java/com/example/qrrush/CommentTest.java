package com.example.qrrush;


import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
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

public class CommentTest {
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
    public void commentTest(){
        try {
            Thread.sleep(10000); // waits for 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TabLayout tabLayout = (TabLayout) solo.getView(R.id.tabLayout);


        solo.clickOnView(tabLayout.getTabAt(1).view);
        solo.clickOnView(solo.getView(R.id.common_button));
        solo.sleep(1000);
        solo.waitForView(R.id.common_button);
        solo.clickOnView(tabLayout.getTabAt(0).view);
        for(int i = 0; i<6; i++){
            solo.clickOnView(solo.getView(R.id.sortingButton));
        }

        ListView ListView=(ListView)solo.getView(R.id.listy);
        View view=ListView.getChildAt(0);
        ImageButton button=(ImageButton)view.findViewById(R.id.commentButton);
        solo.clickOnView(button);
        solo.enterText(0, "nice");
        solo.clickOnText("OK");
        solo.sleep(3000);
        ListView ListView1=(ListView)solo.getView(R.id.listy);
        View view1=ListView1.getChildAt(0);
        TextView commentView=(TextView) view1.findViewById(R.id.commentEditText);
        String confirmComment = commentView.getText().toString();
        Log.d("comment", confirmComment);
        assertEquals("Comment: nice", confirmComment);
    }
}

