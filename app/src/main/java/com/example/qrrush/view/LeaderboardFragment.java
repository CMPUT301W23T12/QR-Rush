package com.example.qrrush.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.controller.RankComparator;
import com.example.qrrush.controller.ScoreComparator;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.LeaderboardQRCodeAdapter;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.QRCodeAdapter;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserAdapter;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The fragment which opens the leaderboard and displays all information
 */
public class LeaderboardFragment extends Fragment {
    User user;
    UserAdapter userAdapter;
    LeaderboardQRCodeAdapter qrCodeAdapter;
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

        Button playersTabButton  = view.findViewById(R.id.button4);
        Button qrCodesTabButton = view.findViewById(R.id.button5);
        // when thuis button is pressed

        // call function(true)



        TextView rank = view.findViewById(R.id.rankTextView); // add TextView for rank
        ListView leaderList = view.findViewById(R.id.leaderboard_listview);
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

//        ArrayList<User> users = getAllCollection(user);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<User> users = new ArrayList<User>();
        db.collection("profiles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {

                            users.clear();
                            for (QueryDocumentSnapshot document : value) {
                                Log.d("FirebaseWrapper", document.getId() + " => " + document.getData());
                                User u = new User(document.getId(),
                                        "",
                                        0,
                                        new ArrayList<>(),
                                        0);
                                ArrayList<String> hashes = (ArrayList<String>) document.get("qrcodes");
                                for (String hash : hashes) {
                                    u.addQRCodeWithoutFirebase(new QRCode(hash, new Timestamp(0, 0)));
                                }

                                users.add(u);
                            }
                            Collections.sort(users, new RankComparator());
                            for (int i = 0; i < users.size(); ++i){
                                users.get(i).setRank(users.indexOf(users.get(i)));
                            }
                            ArrayList<User> topUsers = new ArrayList<>();
                            ArrayList<User> otherUsers = new ArrayList<>();
                            userAdapter = new UserAdapter(requireActivity(), otherUsers);
                            leaderboardView.setVisibility(View.VISIBLE);
                            leaderboardView.setAdapter(userAdapter);

                            for(int i = 0; topUsers.size()>2 && i<3; i++){
                                topUsers.add(users.get(i));
                            }
                            for(int i = 3; i<users.size(); i++){
                                otherUsers.add(users.get(i));
                            }
                            if (topUsers.size() > 0) {
                                updateTopUserView(user1View, topUsers.get(0), 1);

                            }
                            if (topUsers.size() > 1) {
                                updateTopUserView(user2View, topUsers.get(1), 2);

                            }
                            if (topUsers.size() > 2) {
                                updateTopUserView(user3View, topUsers.get(2), 3);

                            }

                        } else {
                            Log.d("FirebaseWrapper", "Error getting documents: ",error.fillInStackTrace());
                        }
                    }
                });
        // Separate the top 3 users
        qrCodesTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseWrapper.getAllQRCodes(qrCodes -> {
                    ScoreComparator s = new ScoreComparator();

                    Collections.sort(qrCodes,s);
                    qrCodeAdapter = new LeaderboardQRCodeAdapter(requireActivity(),qrCodes);
                    leaderboardView.setVisibility(View.VISIBLE);
                    leaderboardView.setAdapter(qrCodeAdapter);
                });
            }
        });



        loadingText.setVisibility(View.GONE);

        user1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bruh = user1Name.getText().toString();
                FirebaseWrapper.getUserData(bruh, user1 -> {
                    User user2 = user1.get();
                    new ProfileDialogFragment(user2).show(requireActivity().getSupportFragmentManager(),user2.getUserName());
                });


            }

        });
        user2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bruh = user2Name.getText().toString();
                FirebaseWrapper.getUserData(bruh, user1 -> {
                    User user2 = user1.get();
                    new ProfileDialogFragment(user2).show(requireActivity().getSupportFragmentManager(),user2.getUserName());
                });


            }

        });
        user3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bruh = user3Name.getText().toString();
                FirebaseWrapper.getUserData(bruh, user1 -> {
                    User user2 = user1.get();
                    new ProfileDialogFragment(user2).show(requireActivity().getSupportFragmentManager(),user2.getUserName());
                });


            }

        });
        leaderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView v = view.findViewById(R.id.OtherUserNameView);
                String nameUser = v.getText().toString();
                FirebaseWrapper.getUserData(nameUser, user1 -> {
                    User user2 = user1.get();
                    new ProfileDialogFragment(user2).show(requireActivity().getSupportFragmentManager(),user2.getUserName());
                });
                Log.e("name",v.getText().toString());
//                requireActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_view, new ProfileFragment(leaderboardView.get(position), false)).commit();
            }
        });
    }
    private void updateTopUserView(View view, User user, int rank) {
        ImageView userImage = view.findViewById(R.id.imageView2);
        TextView userName = view.findViewById(R.id.OtherUserNameView);
        TextView userScore = view.findViewById(R.id.OtherUserScoreView);
        TextView rankTextView = view.findViewById(R.id.rankTextView); // add TextView for rank

        // Replace with the actual method to get the user image
        userImage.setImageResource(R.drawable.discordpic);
        userName.setText(user.getUserName());
        Log.e("name",user.getUserName());
        userScore.setText(String.valueOf(user.getTotalScore()));
        rankTextView.setText(String.valueOf(rank)); // set the rank TextView

    }
    //TODO: when a user in the top 3 is clicked on, open their profile



}

