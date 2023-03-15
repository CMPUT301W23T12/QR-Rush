package com.example.qrrush.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.SearchPlayerAdapter;
import com.example.qrrush.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is a fragment of the MainActivity, it's responsible for creating a UI interface for users to search
 * other player's profiles. It retrieves the user's search input and fetches the corresponding profile
 */
public class SocialFragment extends Fragment {
    User user;
    private ListView searchResultsList;
    private SearchPlayerAdapter searchResultsAdapter;

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
        searchResultsAdapter = new SearchPlayerAdapter(getContext(), R.layout.searchedplayers_content, new ArrayList<User>());
        searchResultsList.setAdapter(searchResultsAdapter);
        return view;
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

        ImageButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchPlayerEditField = view.findViewById(R.id.searchPlayer);
                String searchPlayer = searchPlayerEditField.getText().toString();

                if (searchPlayer.matches("")) {
                    // UX Experience: If search is empty and you press search, it will display "No player found!"
                    displayNoPlayerFound();
                    return;
                }
                FirebaseWrapper.getUserData(searchPlayer, user -> {
                    // we will retrieve the user information from firebase and create a user object
                    // update the search results adapter with the retrieved user information
                    if (!user.isPresent()){
                        displayNoPlayerFound();
                        return;
                    }
                    ArrayList<User> searchResults = new ArrayList<>();
                    searchResults.add(user.get());
                    searchResultsAdapter.setData(searchResults);

                    searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_view, new ProfileFragment(searchResults.get(position), false)).commit();
                        }
                    });
                });
            }
        });
    }
    private void displayNoPlayerFound() {
        TextView noPlayerFound = getView().findViewById(R.id.noPlayerFound);
        noPlayerFound.setVisibility(View.VISIBLE);
        searchResultsList.setVisibility(View.GONE);
    }



}