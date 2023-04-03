package com.example.qrrush.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.qrrush.R;
import com.example.qrrush.controller.RankComparator;
import com.example.qrrush.controller.ScoreComparator;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.LeaderboardQRCodeAdapter;
import com.example.qrrush.model.QRCode;
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
    FragmentActivity activity;

    /**
     * Creates a LeaderboardFragment for the given user.
     *
     * @param user The user to create the LeaderboardFragment for.
     */
    public LeaderboardFragment(User user, FragmentActivity activity) {
        this.user = user;
        this.activity = activity;
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

        ImageButton playersTabButton = view.findViewById(R.id.players);
        ImageButton qrCodesTabButton = view.findViewById(R.id.scanned_search);
        TextView rank = view.findViewById(R.id.rankTextView); // add TextView for rank
        ListView leaderList = view.findViewById(R.id.leaderboard_listview);
        View topThreeUser = view.findViewById(R.id.top_users_container);

        // TODO: display some loading screen while this finishes.
        // TODO: make it display the QR code leaderboard when QR codes are selected at the top.

//        ArrayList<User> users = getAllCollection(user);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<User> users = new ArrayList<User>();
        db.collection("profiles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        users.clear();
                        for (QueryDocumentSnapshot document : value) {

                            User u = new User(document.getId(),
                                    "",
                                    0,
                                    new ArrayList<>(),
                                    0, document.getString("profile_picture"));
                            ArrayList<String> hashes = (ArrayList<String>) document.get("qrcodes");
                            for (String hash : hashes) {
                                u.addQRCodeWithoutFirebase(new QRCode(hash, new Timestamp(0, 0)));
                            }

                            users.add(u);
                        }
                        Collections.sort(users, new RankComparator());
                        for (int i = 0; i < users.size(); ++i) {
                            users.get(i).setRank(users.indexOf(users.get(i)));
                        }
                        ArrayList<User> topUsers = new ArrayList<>();
                        ArrayList<User> otherUsers = new ArrayList<>();
                        userAdapter = new UserAdapter(activity, otherUsers);
                        leaderboardView.setVisibility(View.VISIBLE);
                        leaderboardView.setAdapter(userAdapter);

                        for (int i = 0; i < 3 && users.size() > i; i++) {
                            topUsers.add(users.get(i));
                        }
                        for (int i = 3; i < users.size(); i++) {
                            otherUsers.add(users.get(i));
                        }

                        for (int i = 0; i < topUsers.size(); i += 1) {
                            updateTopUserView(view, topUsers.get(i), i + 1);
                        }
                    }
                });

        playersTabButton.setColorFilter(getResources().getColor(R.color.bright_cyan));
        playersTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topThreeUser.setVisibility(View.VISIBLE);
                playersTabButton.setColorFilter(getResources().getColor(R.color.bright_cyan));
                qrCodesTabButton.setColorFilter(getResources().getColor(R.color.bright_blue));
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                ArrayList<User> users = new ArrayList<User>();
                db.collection("profiles")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    return;
                                }

                                users.clear();
                                for (QueryDocumentSnapshot document : value) {
                                    User u = new User(document.getId(),
                                            "",
                                            0,
                                            new ArrayList<>(),
                                            0, document.getString("profile_picture"));
                                    ArrayList<String> hashes = (ArrayList<String>) document.get("qrcodes");
                                    for (String hash : hashes) {
                                        u.addQRCodeWithoutFirebase(new QRCode(hash, new Timestamp(0, 0)));
                                    }

                                    users.add(u);
                                }
                                Collections.sort(users, new RankComparator());
                                for (int i = 0; i < users.size(); ++i) {
                                    users.get(i).setRank(users.indexOf(users.get(i)));
                                }
                                ArrayList<User> topUsers = new ArrayList<>();
                                ArrayList<User> otherUsers = new ArrayList<>();
                                userAdapter = new UserAdapter(activity, otherUsers);
                                leaderboardView.setVisibility(View.VISIBLE);
                                leaderboardView.setAdapter(userAdapter);

                                for (int i = 0; i < 3 && users.size() > i; i++) {
                                    topUsers.add(users.get(i));
                                }
                                for (int i = 3; i < users.size(); i++) {
                                    otherUsers.add(users.get(i));
                                }

                                for (int i = 0; i < topUsers.size(); i += 1) {
                                    updateTopUserView(view, topUsers.get(i), i + 1);
                                }
                            }
                        });
            }
        });
        // Separate the top 3 users


        loadingText.setVisibility(View.GONE);

        leaderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView v = view.findViewById(R.id.OtherUserNameView);
                String nameUser = v.getText().toString();
                FirebaseWrapper.getUserData(nameUser, user1 -> {
                    User user2 = user1.get();
                    new ProfileDialogFragment(user2).show(activity.getSupportFragmentManager(), user2.getUserName());
                });
            }
        });
        qrCodesTabButton.setColorFilter(getResources().getColor(R.color.bright_blue));
        qrCodesTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topThreeUser.setVisibility(View.GONE);
                playersTabButton.setColorFilter(getResources().getColor(R.color.bright_blue));
                qrCodesTabButton.setColorFilter(getResources().getColor(R.color.bright_cyan));
                FirebaseWrapper.getAllQRCodes(qrCodes -> {
                    ScoreComparator s = new ScoreComparator();

                    Collections.sort(qrCodes, s);
                    qrCodeAdapter = new LeaderboardQRCodeAdapter(activity, qrCodes);
                    leaderboardView.setVisibility(View.VISIBLE);
                    leaderboardView.setAdapter(qrCodeAdapter);
                });
            }
        });
    }

    private void updateTopUserView(View v, User user, int rank) {
        View view;
        switch (rank) {
            case 1:
                view = v.findViewById(R.id.top_user1);
                break;
            case 2:
                view = v.findViewById(R.id.top_user2);
                break;
            case 3:
                view = v.findViewById(R.id.top_user3);
                break;
            default:
                view = null;
        }


        ImageView userImage = view.findViewById(R.id.imageView2);
        TextView userName = view.findViewById(R.id.OtherUserNameView);
        TextView userScore = view.findViewById(R.id.OtherUserScoreView);
        TextView rankTextView = view.findViewById(R.id.rankTextView); // add TextView for rank

        // Replace with the actual method to get the user image
        if (user.hasProfilePicture()) {
            Glide.with(activity)
                    .load(user.getProfilePicture())
                    .dontAnimate()
                    .into(userImage);
        } else {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(user.getUserName());
            userImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(ResourcesCompat.getFont(activity, R.font.gatekept))
                    .toUpperCase()
                    .width(200)
                    .height(200)
                    .endConfig()
                    .buildRound(String.valueOf(user.getUserName().charAt(0)), color);
            userImage.setImageDrawable(drawable);
        }
        userName.setText(user.getUserName());
        userScore.setText(String.valueOf(user.getTotalScore()));
        rankTextView.setText(String.valueOf(rank)); // set the rank TextView

        view.setOnClickListener(theView -> {
            FirebaseWrapper.getUserData(user.getUserName(), user1 -> {
                User user2 = user1.get();
                new ProfileDialogFragment(user2).show(activity.getSupportFragmentManager(), user2.getUserName());
            });
        });
    }
}

