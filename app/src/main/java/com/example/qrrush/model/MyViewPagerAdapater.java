package com.example.qrrush.model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.qrrush.view.LeaderboardFragment;
import com.example.qrrush.view.MainFragment;
import com.example.qrrush.view.ProfileFragment;
import com.example.qrrush.view.ShopFragment;
import com.example.qrrush.view.SocialFragment;

public class MyViewPagerAdapater extends FragmentStateAdapter {
    User user;
    FragmentActivity activity;

    public MyViewPagerAdapater(@NonNull FragmentActivity fragmentActivity, User user) {
        super(fragmentActivity);
        this.activity = fragmentActivity;
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment(user, true, activity);
            case 1:
                return new ShopFragment(user);
            case 2:
                return new MainFragment(user);
            case 3:
                return new SocialFragment(user);
            case 4:
                return new LeaderboardFragment(user, activity);
            default:
                return new MainFragment(user);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
