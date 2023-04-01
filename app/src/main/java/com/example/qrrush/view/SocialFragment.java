package com.example.qrrush.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.SearchPlayerAdapter;
import com.example.qrrush.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

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
    @Override
    public void onResume() {
        super.onResume();
        searchPlayerEditField.setText("");
        noPlayerFound.setVisibility(View.INVISIBLE);
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
                ArrayList<User> searchResults = new ArrayList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("profiles")
                        .orderBy("name")
                        .startAt(String.valueOf(s).toLowerCase())
                        .endAt(String.valueOf(s).toLowerCase() + "\uf8ff");

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                noPlayerFound.setVisibility(View.INVISIBLE);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    FirebaseWrapper.getUserData(document.getId(), user -> {
                                        searchResults.add(user.get());
                                        searchResultsAdapter.setData(searchResults);
                                    });
                                }
                            } else {
                                noPlayerFound.setVisibility(View.VISIBLE);
                                searchResults.clear();
                                searchResultsAdapter.setData(searchResults);
                            }
                        } else {
                            return;
                        }
                    }
                });
                searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        new ProfileDialogFragment(searchResults.get(position)).show(
                                requireActivity().getSupportFragmentManager(),
                                searchResults.get(position).getUserName()
                        );
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
                }
        });
    }
}