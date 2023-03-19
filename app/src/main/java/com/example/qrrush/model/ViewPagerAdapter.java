package com.example.qrrush.model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.qrrush.view.LeaderboardFragment;
import com.example.qrrush.view.MainFragment;
import com.example.qrrush.view.ProfileFragment;
import com.example.qrrush.view.SocialFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    User user;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ProfileFragment(user, true);
            case 1: return new SocialFragment(user);
            case 2: return new MainFragment(user);
            case 3: return new SocialFragment(user);
            case 4: return new LeaderboardFragment(user);
            default: return new MainFragment(user);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

