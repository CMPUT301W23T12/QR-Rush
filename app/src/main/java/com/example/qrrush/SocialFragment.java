package com.example.qrrush;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SocialFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    User user;

    public SocialFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we extract the search input box and reference our profile card
                EditText searchPlayerEditField = view.findViewById(R.id.searchPlayer);
                TextView noPlayerFound = view.findViewById(R.id.noPlayerFound);
                ImageView PlayerImg = view.findViewById(R.id.profileView);
                TextView searchedName = view.findViewById(R.id.nameView);
                TextView searchedRank = view.findViewById(R.id.rankView);
                String searchPlayer = searchPlayerEditField.getText().toString();

                if (!searchPlayer.matches("")) {
                    FirebaseWrapper.getUserData(searchPlayer, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Player Search is found, Display the Profile
                                    noPlayerFound.setVisibility(View.GONE);
                                    searchedName.setText(searchPlayer);
                                    searchedRank.setText(String.format("%d", document.getLong("rank").intValue()));
                                    searchedName.setVisibility(View.VISIBLE);
                                    searchedRank.setVisibility(View.VISIBLE);
                                    PlayerImg.setVisibility(View.VISIBLE);

                                } else {
                                    // Player is not found, display the "No player found!"
                                    noPlayerFound.setVisibility(View.VISIBLE);
                                    searchedName.setVisibility(View.GONE);
                                    PlayerImg.setVisibility(View.GONE);
                                    searchedRank.setVisibility(View.GONE);
                                }
                            } else {
                                Log.e("SearchingPlayer", "Error fetching usernames in firebase");
                            }

                        }
                    });
                } else {
                    // UX Experience: If search is empty and you press search, it will display "No player found!"
                    noPlayerFound.setVisibility(View.VISIBLE);
                    searchedName.setVisibility(View.GONE);
                    PlayerImg.setVisibility(View.GONE);
                    searchedRank.setVisibility(View.GONE);
                }

            }
        });

    }
}