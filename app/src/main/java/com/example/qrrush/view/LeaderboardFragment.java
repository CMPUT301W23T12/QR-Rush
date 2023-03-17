package com.example.qrrush.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.controller.UserScoreComparator;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.QRCodeAdapter;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserAdapter;

/**
 * The fragment which opens the leaderboard and displays all information
 */
public class LeaderboardFragment extends Fragment {
    User user;
    UserAdapter userAdapter;
    QRCodeAdapter qrCodeAdapter;
    TextView loadingText;
    ListView leaderboardView;

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

        loadingText = view.findViewById(R.id.leaderboard_loading_text);
        leaderboardView = view.findViewById(R.id.leaderboard_listview);

        loadingText.setVisibility(View.VISIBLE);
        leaderboardView.setVisibility(View.GONE);

        // TODO: display some loading screen while this finishes.
        // TODO: make it display the QR code leaderboard when QR codes are selected at the top.

        FirebaseWrapper.getAllUsers(users -> {
            leaderboardView.setVisibility(View.VISIBLE);

            UserScoreComparator sc = new UserScoreComparator();
            users.sort(sc);
            userAdapter = new UserAdapter(requireActivity(), users);
            leaderboardView.setAdapter(userAdapter);

            loadingText.setVisibility(View.GONE);
        });
    }
}