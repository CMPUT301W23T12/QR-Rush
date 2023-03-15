package com.example.qrrush.view;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.qrrush.LeaderboardAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.User;

/**
 * The fragment which opens the leaderboard and displays all information

 */
public class LeaderboardFragment extends Fragment {

    ListView LeaderList;
    User user;

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
        ArrayList<Object> userNames = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        //LeaderList = view.findViewById(R.id.leaderboard_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve user names from Firebase and populate them in a list view
//        db.collection("profiles")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String docUserName = document.getId();
//                            System.out.println("Document name: " + docUserName);
//                            userNames.add(docUserName);
//                        }
//                        LeaderboardAdapter adapter = new LeaderboardAdapter(getContext(), userNames);
//                        LeaderList.setAdapter(adapter);
//                    } else {
//                        Log.d(TAG, "Error getting documents: ", task.getException());
//                    }
//                });
//    }
}