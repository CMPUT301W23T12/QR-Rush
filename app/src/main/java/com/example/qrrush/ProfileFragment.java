package com.example.qrrush;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    User user;
    QRCodeAdapter QRCodeAdapter;

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
        ImageView profileView = view.findViewById(R.id.profileView);
        contactView.setText(user.getPhoneNumber());
        nameView.setText(user.getUserName());
        rankView.setText(String.valueOf(user.getRank()));
        scoreView.setText(String.valueOf(user.getTotalScore()));
        QRScanned.setText(String.valueOf(user.getNumberOfQRCodes()));
        //      Image will be fit into the size of the image view
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

        Button editNameButton = view.findViewById(R.id.edit_name); // Get the button view from the layout
        TextView errorText = view.findViewById(R.id.errorText);

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

                                FirebaseWrapper.checkUsernameAvailability(newUserName, new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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

//                                         Username is unique, continue with edit the process

                                        FirebaseWrapper.getUserData(user.getUserName(), (Task<DocumentSnapshot> task1) -> {
                                            if (!task1.isSuccessful()) {
                                                Log.d("EditName", "Error editting user");
                                                return;
                                            }

                                            DocumentSnapshot document = task1.getResult();
//                                            if (!document.exists()) {
//                                                Log.e("Firebase User", "Document doesn't exist!");
//                                                return;
//                                            }
                                            // Username is unique, continue with registration process
                                            HashMap<String, Object> updatedProfile = new HashMap<>();
                                            updatedProfile.put("UUID", document.getString(user.getUserName()));
                                            updatedProfile.put("phone-number", document.getString("phone-number"));
                                            updatedProfile.put("rank", document.getLong("rank").intValue());
                                            updatedProfile.put("score", document.getLong("score").intValue());
                                            updatedProfile.put("qrcodes", document.get("qrcodes"));
                                            // Add name + UUID and phone number to FB
                                            FirebaseWrapper.addData("profiles", newUserName, updatedProfile);
                                            FirebaseWrapper.deleteDocument("profiles", user.getUserName());


                                        });


                                    }
                                });

                                dialog.dismiss();
                            }

                        });
                    }
                });

                dialog.show();
            }

        });
    }
}

