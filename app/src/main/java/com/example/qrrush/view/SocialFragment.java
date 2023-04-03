package com.example.qrrush.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.controller.RankComparator;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.SearchPlayerAdapter;
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
 * This class is a fragment of the MainActivity, it's responsible for creating a UI interface for users to search
 * other player's profiles. It retrieves the user's search input and fetches the corresponding profile
 */
public class SocialFragment extends Fragment {
    User user;
    TextView noPlayerFound;
    EditText searchPlayerEditField;
    private ListView searchResultsList;
    private SearchPlayerAdapter searchResultsAdapter;
    TextView refreshPlayerText;
    ArrayList<User> userList;

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
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        // Get the reference to the ListView and create the adapter
        searchResultsList = view.findViewById(R.id.searchPlayerList);
        searchResultsAdapter = new SearchPlayerAdapter(requireActivity(), R.layout.searchedplayers_content, new ArrayList<User>());
        searchResultsList.setAdapter(searchResultsAdapter);
        refreshPlayerText = view.findViewById(R.id.refresh_player_list_text);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPlayerText.setVisibility(View.VISIBLE);
        noPlayerFound.setVisibility(View.GONE);

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
                            users.get(i).setRank(users.indexOf(users.get(i)) + 1);
                        }
                        userList = users;
                        searchResultsAdapter.setData(users);
                        searchResultsAdapter.getFilter().filter(searchPlayerEditField.getText().toString());
                    }
                });
        refreshPlayerText.setVisibility(View.GONE);
        noPlayerFound.setVisibility(View.GONE);
        userList = users;
        searchResultsAdapter.setData(users);
        searchResultsAdapter.getFilter().filter(searchPlayerEditField.getText().toString());
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
        noPlayerFound = getView().findViewById(R.id.noPlayerFound);
        searchPlayerEditField = view.findViewById(R.id.searchPlayer);
        searchPlayerEditField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noPlayerFound.setVisibility(View.GONE);
                searchResultsAdapter.getFilter().filter(s.toString().toLowerCase(),
                        numberOfResults -> {
                            if (numberOfResults == 0) {
                                noPlayerFound.setVisibility(View.VISIBLE);
                            }
                        });
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}