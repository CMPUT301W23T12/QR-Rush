package com.example.qrrush.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.QRCodeAdapter;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserAdapter;

import java.util.ArrayList;

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

        View user1View = view.findViewById(R.id.top_user1);
        View user2View = view.findViewById(R.id.top_user2);
        View user3View = view.findViewById(R.id.top_user3);

        TextView user1Name = user1View.findViewById(R.id.OtherUserNameView);
        TextView user2Name = user2View.findViewById(R.id.OtherUserNameView);
        TextView user3Name = user3View.findViewById(R.id.OtherUserNameView);

        TextView user1Score = user1View.findViewById(R.id.OtherUserScoreView);
        TextView user2Score = user2View.findViewById(R.id.OtherUserScoreView);
        TextView user3Score = user3View.findViewById(R.id.OtherUserScoreView);

        // TODO: display some loading screen while this finishes.
        // TODO: make it display the QR code leaderboard when QR codes are selected at the top.

        FirebaseWrapper.getAllUsers(users -> {
            // Separate the top 3 users
            ArrayList<User> topUsers = new ArrayList<>(users.subList(0, Math.min(3, users.size())));
            ArrayList<User> otherUsers = new ArrayList<>(users.subList(Math.min(3, users.size()), users.size()));

            // Set the top 3 users in the appropriate TextViews
//            LayoutInflater inflater = LayoutInflater.from(requireContext());
//            LinearLayout topUsersLayout = view.findViewById(R.id.top_users_layout);


            if (topUsers.size() > 0) {
                updateTopUserView(user1View, topUsers.get(0));
            }
            if (topUsers.size() > 1) {
                updateTopUserView(user2View, topUsers.get(1));
            }
            if (topUsers.size() > 2) {
                updateTopUserView(user3View, topUsers.get(2));
            }

            userAdapter = new UserAdapter(requireContext(), otherUsers);
            leaderboardView.setVisibility(View.VISIBLE);
            leaderboardView.setAdapter(userAdapter);

            loadingText.setVisibility(View.GONE);
        });
//        user1View.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//
//        });
    }
    private void updateTopUserView(View view, User user) {
        ImageView userImage = view.findViewById(R.id.user_image);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userScore = view.findViewById(R.id.user_score);

        // Replace with the actual method to get the user image
        userImage.setImageResource(R.drawable.discordpic);
        userName.setText(user.getUserName());
        userScore.setText(String.valueOf(user.getTotalScore()));
    }
    //TODO: when a user in the top 3 is clicked on, open their profile


}

