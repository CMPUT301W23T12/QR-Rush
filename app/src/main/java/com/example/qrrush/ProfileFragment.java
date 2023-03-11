package com.example.qrrush;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ProfileFragment extends Fragment {
    User user;
    QRCodeAdapter QRCodeAdapter;
    int sortingTracker;

    /**
     * Grabs User object from the main activity
     *
     * @param user
     */
    public ProfileFragment(User user) {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        TextView contactView = view.findViewById(R.id.contactView);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView rankView = view.findViewById(R.id.rankView);
        TextView scoreView = view.findViewById(R.id.scoreView);
        TextView QRScanned = view.findViewById(R.id.qrCodesView);
        Button sortingButton = view.findViewById(R.id.sortingButton);
        contactView.setText("Contact: " + user.getPhoneNumber());
        nameView.setText("Name: " + user.getUserName());
        rankView.setText("Rank: " + String.valueOf(user.getRank()));
        scoreView.setText("Score: " + String.valueOf(user.getTotalScore()));
        QRScanned.setText("QR Codes Found: " + String.valueOf(user.getNumberOfQRCodes()));

        // On launch sorting is set by date (sortingTracker = 1)
        //      by points (sortingTracker = 2)
        //      by score (sortingTracker = 0)
        sortingTracker = 1;

        // Sorting button sorts arraylist of QR codes using custom comparators
        // Adapter gets updated each time the list gets sorted
        sortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortingTracker == 0) {
                    sortingButton.setText("By Date");
                    sortingTracker += 1;
                    DateComparator dateComparator = new DateComparator();
                    Collections.sort(user.getQRCodes(), dateComparator);
                    QRCodeAdapter.notifyDataSetChanged();
                } else if (sortingTracker == 1) {
                    sortingButton.setText("By Points");
                    sortingTracker += 1;
                    ScoreComparator scoreComparator = new ScoreComparator();
                    Collections.sort(user.getQRCodes(), scoreComparator);
                    QRCodeAdapter.notifyDataSetChanged();
                } else if (sortingTracker == 2) {
                    sortingButton.setText("By Name");
                    sortingTracker = 0;
                    NameComparator nameComparator = new NameComparator();
                    Collections.sort(user.getQRCodes(), nameComparator);
                    QRCodeAdapter.notifyDataSetChanged();
                }
            }
        });
        ImageView profileView = view.findViewById(R.id.profileView);
        // Image will be fit into the size of the image view
        Picasso
                .get()
                .load(user.getProfilePicture())
                .fit()
                .into(profileView);

        // Passes User object from main activity to the QR code adapter
        QRCodeAdapter = new QRCodeAdapter(requireActivity(), user.getQRCodes(), user);
        ListView qrCodeList = view.findViewById(R.id.listy);
        qrCodeList.setAdapter(QRCodeAdapter);
        QRCodeAdapter.notifyDataSetChanged();

        // Get the button view from the layout
        ImageButton editNameButton = view.findViewById(R.id.edit_name);

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
                //add a positive button on the alertdialog that tells user to confirm their input
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(newView -> {
                    // store the new name input of the user
                    String newUserName = userNameEdit.getText().toString();

                    FirebaseWrapper.checkUsernameAvailability(newUserName, (Task<QuerySnapshot> task) -> {
                        if (!task.isSuccessful()) {
                            // Error occurred while querying database
                            Log.e("EditName", "ERROR QUERYING DATABASE WHILE SEARCHING PROFILES COLLECTION");
                            return;
                        }

                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.size() > 0) {
                            // Username is taken, prompt user to pick a new name
                            errorText.setVisibility(View.VISIBLE);
                            return;
                        }

                        // Username is unique, continue with edit the process
                        FirebaseWrapper.getUserData(user.getUserName(), (Task<DocumentSnapshot> task1) -> {
                            if (!task1.isSuccessful()) {
                                Log.e("EditName", "Error editing user");
                                return;
                            }

                            // Username is unique, continue with registration process
                            DocumentSnapshot document = task1.getResult();

                            Map<String, Object> updatedProfile = document.getData();
                            ArrayList<String> hashes = (ArrayList<String>) document.get("qrcodes");

                            // Add name + UUID and phone number to FB
                            FirebaseWrapper.addData("profiles", newUserName, updatedProfile);
                            FirebaseWrapper.deleteDocument("profiles", user.getUserName());

                            user.setUserName(newUserName);
                            UserUtil.setUsername(requireActivity().getApplicationContext(), newUserName);

                            nameView.setText("Name: " + user.getUserName());
                            dialog.dismiss();
                        });
                    });
                });
            });

            dialog.show();
        });
    }
}

