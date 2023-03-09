package com.example.qrrush;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ProfileFragment extends Fragment {
    User user;
    QRCodeAdapter QRCodeAdapter;

    /**
     * Grabs User object from the main activity
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
        ImageView profileView = view.findViewById(R.id.profileView);
        contactView.setText(user.getPhoneNumber());
        nameView.setText(user.getUserName());
        rankView.setText(String.valueOf(user.getRank()));
        scoreView.setText(String.valueOf(user.getTotalScore()));
        QRScanned.setText(String.valueOf(user.getNumberOfQRCodes()));
        /**
         *      Image will be fit into the size of the image view
         */
        Picasso
                .get()
                .load(user.getProfilePicture())
                .fit()
                .into(profileView);

        /**
         * Passes User object from main activity to the QR code adapter
         */
        QRCodeAdapter = new QRCodeAdapter(requireActivity(), user.getQRCodes(), user);
        ListView qrCodeList = view.findViewById(R.id.listy);
        qrCodeList.setAdapter(QRCodeAdapter);
        QRCodeAdapter.notifyDataSetChanged();

        Button editNameButton = view.findViewById(R.id.edit_name); // Get the button view from the layout

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View addNewName = getLayoutInflater().inflate(R.layout.profile_overlay, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
                alertDialogBuilder.setView(addNewName);
                alertDialogBuilder.setMessage("Input New Name:");
                alertDialogBuilder.setTitle("Alert!");

                alertDialogBuilder.setPositiveButton("Confirm", null);

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        //add a positive button on the alertdialog that tells user to confirm their input
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // store the new name input of the user
                                EditText userNameEdit = addNewName.findViewById(R.id.input_new_name);
                                String newUserName = userNameEdit.getText().toString();

                                FirebaseWrapper.getUserData(UserUtil.getUsername(requireContext().getApplicationContext()), new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) // if username is present
                                            {
                                                /// Retrieve all the fields from the Database of that user
                                                //String newName = "Bob";
                                                String phonenumber = document.getString("phone-number");
                                                int rank = document.getLong("rank").intValue();
                                                int score = document.getLong("score").intValue();
                                                HashMap<String, Object> updatedProfile = new HashMap<>();
                                                updatedProfile.put("name", newUserName);
                                                updatedProfile.put("phone-number", phonenumber);
                                                updatedProfile.put("rank", rank);
                                                updatedProfile.put("score", score);
                                                // Check if username is already taken, if so do not add it to firebase
                                                FirebaseWrapper.addData("profiles", "UpdatedName", updatedProfile);
                                                FirebaseWrapper.removeUserProfile(UserUtil.getUsername(requireContext().getApplicationContext()));
                                                UserUtil.getUsername(requireContext().getApplicationContext(),newUserName);
                                                user = new User(newUserName,
                                                        document.getString("phone-number"),
                                                        document.getLong("rank").intValue(),
                                                        document.getLong("score").intValue(),
                                                        qrCodeList);

                                            } else {

                                            }
                                        } else {

                                        }
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });
    }
}