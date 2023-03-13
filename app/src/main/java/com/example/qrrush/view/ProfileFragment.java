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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.qrrush.R;
import com.example.qrrush.controller.DateComparator;
import com.example.qrrush.controller.NameComparator;
import com.example.qrrush.controller.ScoreComparator;
import com.example.qrrush.model.FirebaseWrapper;
import com.example.qrrush.model.QRCodeAdapter;
import com.example.qrrush.model.User;
import com.example.qrrush.model.UserUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Map;

/**
 * The fragment which displays the profile.
 */
public class ProfileFragment extends Fragment {
    User user;
    QRCodeAdapter qrCodeAdapter;
    int sortingTracker;

    /**
     * Grabs User object from the main activity
     *
     * @param user The user who's profile should be displayed.
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

        ImageView profileView = view.findViewById(R.id.profileView);
        //TextView contactView = view.findViewById(R.id.contactView);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView rankView = view.findViewById(R.id.rankView);
        TextView scoreView = view.findViewById(R.id.scoreView);
        TextView QRScanned = view.findViewById(R.id.qrCodesView);
        TextView rankText = view.findViewById(R.id.rankText);
        TextView QRText = view.findViewById(R.id.qrCodesText);
        TextView scoreText = view.findViewById(R.id.scoreText);
        Button sortingButton = view.findViewById(R.id.sortingButton);
        //contactView.setText("Contact: " + user.getPhoneNumber());
        nameView.setText(user.getUserName());
        rankView.setText(String.valueOf(user.getRank()));
        QRScanned.setText(String.valueOf(user.getQRCodes().size()));
        scoreView.setText(String.valueOf(user.getTotalScore()));
        rankText.setText("RANK");
        QRText.setText("QRCODES FOUND");
        scoreText.setText("SCORE");

        // On launch sorting is set by date (sortingTracker = 1)
        //      by points (sortingTracker = 2)
        //      by score (sortingTracker = 0)
        sortingTracker = 1;

        // Sorting button sorts arraylist of QR codes using custom comparators
        // Adapter gets updated each time the list gets sorted
        sortingButton.setOnClickListener(v -> {
            if (sortingTracker == 0) {
                sortingButton.setText("By Date");
                sortingTracker += 1;
                DateComparator dateComparator = new DateComparator();
                Collections.sort(user.getQRCodes(), dateComparator);
                // Sorts by newest to oldest (newest codes being at the top)
                Collections.reverse(user.getQRCodes());
                qrCodeAdapter.notifyDataSetChanged();
            } else if (sortingTracker == 1) {
                sortingButton.setText("By Points");
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
        // Image will be fit into the size of the image view
//        Picasso
//                .get()
//                .load(user.getProfilePicture())
//                .fit()
//                .into(profileView);

        // Passes User object from main activity to the QR code adapter
        qrCodeAdapter = new QRCodeAdapter(requireActivity(), user.getQRCodes(), user);
        ListView qrCodeList = view.findViewById(R.id.listy);
        qrCodeList.setAdapter(qrCodeAdapter);
        qrCodeAdapter.notifyDataSetChanged();

        // Update the UI whenever the arrayAdapter gets a change.
        qrCodeAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                rankView.setText(String.valueOf(user.getRank()));
                QRScanned.setText(String.valueOf(user.getQRCodes().size()));
                scoreView.setText(String.valueOf(user.getTotalScore()));
            }
        });

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
                TextView errorText1 = addNewName.findViewById(R.id.errorText1);
                //add a positive button on the alertdialog that tells user to confirm their input
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(newView -> {
                    // store the new name input of the user

                    String newUserName = userNameEdit.getText().toString();
                    if (newUserName.isEmpty()) {
                        dialog.dismiss();
                        return;
                    }

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

