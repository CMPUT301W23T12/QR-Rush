package com.example.qrrush.view;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.controller.DateComparator;
import com.example.qrrush.controller.NameComparator;
import com.example.qrrush.controller.RankComparator;
import com.example.qrrush.controller.ScoreComparator;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.QRCodeAdapter;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The fragment which displays the users profile.
 * This class is responsible for creating and setting up the users info for display,
 * sorting the users QR codes by date, score and name,
 * letting the user edit there username by interacting with firebase
 */
public class ProfileFragment extends Fragment implements Serializable {
    User user;
    QRCodeAdapter qrCodeAdapter;
    int sortingTracker;
    Boolean editable;

    /**
     * Grabs User object from the main activity
     *
     * @param user The user who's profile should be displayed.
     */
    public ProfileFragment(User user, Boolean editable) {
        // Required empty public constructor
        this.user = user;
        this.editable = editable;
    }
    public void getAllCollection(User user, TextView rankView){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            ArrayList<User> users = new ArrayList<User>();
                            users.clear();
                            for (QueryDocumentSnapshot document : value) {
                                Log.d("FirebaseWrapper", document.getId() + " => " + document.getData());
                                if (!user.getUserName().matches(document.getId())){
                                    users.add(new User(document.getId(),
                                            "",
                                            0,
                                            ((Long) document.getData().get("score")).intValue(),
                                            new ArrayList<>(),
                                            0));
                                } else{
                                    user.setTotalScore(((Long) document.getData().get("score")).intValue());
                                    users.add(user);
                                }
                            }
                            Collections.sort(users, new RankComparator());
                            for (int i = 0; i < users.size(); ++i){
                                if (user.getUserName().matches(users.get(i).getUserName())){
                                    user.setRank(((users.indexOf(users.get(i)))+1));
                                    rankView.setText(String.valueOf(user.getRank()));
                                }
                            }
                        } else {
                            Log.d("FirebaseWrapper", "Error getting documents: ",error.fillInStackTrace());
                        }
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        FirebaseWrapper.getAllCollection(user);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView scoreView = view.findViewById(R.id.scoreView);
        TextView QRScanned = view.findViewById(R.id.qrCodesView);
        TextView moneyView = view.findViewById(R.id.moneyView);
        TextView rankText = view.findViewById(R.id.rankText);
        TextView rankView = view.findViewById(R.id.rankView);
        TextView QRText = view.findViewById(R.id.qrCodesText);
        TextView scoreText = view.findViewById(R.id.scoreText);
        TextView moneyText = view.findViewById(R.id.moneyText);
        Button sortingButton = view.findViewById(R.id.sortingButton);
        nameView.setText(user.getUserName());
        rankView.setText(String.valueOf(user.getRank()));
        QRScanned.setText(String.valueOf(user.getQRCodes().size()));
        scoreView.setText(String.valueOf(user.getTotalScore()));
        moneyView.setText(String.valueOf(user.getMoney()));
        getAllCollection(user, rankView);


        // Passes User object from main activity to the QR code adapter
        qrCodeAdapter = new QRCodeAdapter(requireActivity(), user.getQRCodes(), user, this.editable);
        ListView qrCodeList = view.findViewById(R.id.listy);
        qrCodeList.setAdapter(qrCodeAdapter);

        sortingTracker = 1;

        sortingButton.setText("By Date (Newest First)");
        DateComparator dateComparator = new DateComparator();
        Collections.sort(user.getQRCodes(), dateComparator);
        // Sorts by newest to oldest (newest codes being at the top)
        Collections.reverse(user.getQRCodes());
        qrCodeAdapter.notifyDataSetChanged();

        // Sorting button sorts arraylist of QR codes using custom comparators
        // Adapter gets updated each time the list gets sorted
        sortingButton.setOnClickListener(v -> {
            if (sortingTracker == 0) {
                sortingButton.setText("By Date (Newest First)");
                sortingTracker += 1;
                Collections.sort(user.getQRCodes(), dateComparator);
                // Sorts by newest to oldest (newest codes being at the top)
                Collections.reverse(user.getQRCodes());
                qrCodeAdapter.notifyDataSetChanged();
            } else if (sortingTracker == 1) {
                sortingButton.setText("By Points (Highest First)");
                sortingTracker += 1;
                ScoreComparator scoreComparator = new ScoreComparator();
                Collections.sort(user.getQRCodes(), scoreComparator);
                qrCodeAdapter.notifyDataSetChanged();
            } else if (sortingTracker == 2) {
                sortingButton.setText("By Name");
                sortingTracker = 0;
                NameComparator nameComparator = new NameComparator();
                Collections.sort(user.getQRCodes(), nameComparator);
                qrCodeAdapter.notifyDataSetChanged();
            }
        });


        qrCodeAdapter.notifyDataSetChanged();

        // Update the UI whenever the arrayAdapter gets a change.
        qrCodeAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                rankView.setText(String.valueOf(user.getRank()));
                QRScanned.setText(String.valueOf(user.getQRCodes().size()));
                scoreView.setText(String.valueOf(user.getTotalScore()));
                moneyView.setText(String.valueOf(user.getMoney()));
            }
        });
        // Get the button view from the layout
        ImageButton editNameButton = view.findViewById(R.id.edit_name);
        FloatingActionButton addButton = view.findViewById(R.id.addFriend);
        if (!editable) {
            editNameButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when they click add friend, handle sending the friend request to that user, and add them as your friend (if they confirm they want to be your friend)
                }
            });
        }
        editNameButton.setOnClickListener(v -> {
            View addNewName = getLayoutInflater().inflate(R.layout.profile_overlay, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
            alertDialogBuilder.setView(addNewName);
            alertDialogBuilder.setTitle("Input new name:");
            alertDialogBuilder.setPositiveButton("Confirm", null);
            EditText userNameEdit = addNewName.findViewById(R.id.input_new_name);
            userNameEdit.setHint(user.getUserName());

            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setOnShowListener(dialogInterface -> {
                TextView errorText = addNewName.findViewById(R.id.errorText);
                TextView errorText1 = addNewName.findViewById(R.id.errorText1);
                //add a positive button on the alertdialog that tells user to confirm their input
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(newView -> {
                    // store the new name input of the user

                    String newUserName = userNameEdit.getText().toString();
                    if (newUserName.isEmpty()) {
                        dialog.dismiss();
                        return;
                    } else if (newUserName.length() > 10) {
                        errorText1.setVisibility(view.VISIBLE);
                        errorText.setVisibility(View.GONE);

                        return;
                    }

                    FirebaseWrapper.getUserData(newUserName, (Optional<User> userResult) -> {
                        if (userResult.isPresent()) {
                            // Username is taken, prompt user to pick a new name
                            errorText.setVisibility(View.VISIBLE);
                            errorText1.setVisibility(View.GONE);
                            return;
                        }

                        User user = userResult.get();

                        // Username is unique, continue with edit the process
                        FirebaseWrapper.getData("profiles", user.getUserName(), documentSnapshot -> {
                            Map<String, Object> updatedProfile = documentSnapshot.getData();

                            // Add name + UUID and phone number to FB
                            FirebaseWrapper.addData("profiles", newUserName, updatedProfile);
                            FirebaseWrapper.deleteDocument("profiles", user.getUserName());

                            user.setUserName(newUserName);
                            UserUtil.setUsername(requireActivity().getApplicationContext(), newUserName);

                            nameView.setText(user.getUserName());
                            dialog.dismiss();
                        });
                    });
                });
            });
            dialog.show();
        });
    }

}

