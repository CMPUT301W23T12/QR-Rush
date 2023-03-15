package com.example.qrrush.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.LeaderboardAdapter;
import com.example.qrrush.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * The fragment which opens the leaderboard and displays all information
 */
public class LeaderboardFragment extends Fragment {
    ListView LeaderList;
    User user;
    ArrayList<User> users;
    LeaderboardAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Creates a LeaderboardFragment for the given user.
     *
     * @param user The user to create the LeaderboardFragment for.
     */
    public LeaderboardFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: display some loading screen while this finishes.

        FirebaseWrapper.getAllUsers(users -> {
            adapter = new LeaderboardAdapter(requireActivity(), users);
            ListView usersView = view.findViewById(R.id.leaderboard_listview);
            usersView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
    }
}