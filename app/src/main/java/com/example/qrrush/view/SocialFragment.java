package com.example.qrrush.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * This class is a fragment of the MainActivity, it's responsible for creating a UI interface for users to search
 * other player's profiles. It retrieves the user's search input and fetches the corresponding profile
 */
public class SocialFragment extends Fragment {
    User user;

    public SocialFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    /**
     * This method is called once the View is created, it handles the search button click events
     * and retrieves the searched player's data from Firebase.
     *
     * @param view               the created view that contains the UI components.
     * @param savedInstanceState the previous saved state of the app.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageButton searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we extract the search input box and reference our profile card
                EditText searchPlayerEditField = view.findViewById(R.id.searchPlayer);
                TextView noPlayerFound = view.findViewById(R.id.noPlayerFound);
                ImageView PlayerImg = view.findViewById(R.id.profileView);
                TextView searchedName = view.findViewById(R.id.nameViewSocial);
                TextView searchedRank = view.findViewById(R.id.rankView);
                String searchPlayer = searchPlayerEditField.getText().toString();

                if (searchPlayer.matches("")) {
                    // UX Experience: If search is empty and you press search, it will display "No player found!"
                    noPlayerFound.setVisibility(View.VISIBLE);
                    searchedName.setVisibility(View.GONE);
                    PlayerImg.setVisibility(View.GONE);
                    searchedRank.setVisibility(View.GONE);
                    return;
                }

                FirebaseWrapper.getUserData(searchPlayer, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("SearchingPlayer", "Error fetching usernames in firebase");
                            return;
                        }

                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            // Player is not found, display the "No player found!"
                            noPlayerFound.setVisibility(View.VISIBLE);
                            searchedName.setVisibility(View.GONE);
                            PlayerImg.setVisibility(View.GONE);
                            searchedRank.setVisibility(View.GONE);
                            return;
                        }

                        // Player Search is found, Display the Profile
                        noPlayerFound.setVisibility(View.GONE);
                        searchedName.setText(searchPlayer);
                        searchedRank.setText(String.format("%d", document.getLong("rank").intValue()));
                        searchedName.setVisibility(View.VISIBLE);
                        searchedRank.setVisibility(View.VISIBLE);
                        PlayerImg.setVisibility(View.VISIBLE);
                    }
                });

            }
        });

    }
}