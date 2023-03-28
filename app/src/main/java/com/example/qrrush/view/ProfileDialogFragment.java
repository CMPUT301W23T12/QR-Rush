package com.example.qrrush.view;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrrush.R;
import com.example.qrrush.controller.DateComparator;
import com.example.qrrush.controller.NameComparator;
import com.example.qrrush.controller.ScoreComparator;
import com.example.qrrush.model.QRCodeAdapter;
import com.example.qrrush.model.User;

import java.io.Serializable;
import java.util.Collections;

/**
 * The fragment which displays the users profile.
 * This class is responsible for creating and setting up the users info for display,
 * sorting the users QR codes by date, score and name,
 * letting the user edit there username by interacting with firebase
 */
public class ProfileDialogFragment extends DialogFragment implements Serializable {
    User user;
    QRCodeAdapter qrCodeAdapter;
    int sortingTracker;

    /**
     * Grabs User object from the main activity
     *
     * @param user The user who's profile should be displayed.
     */
    public ProfileDialogFragment(User user) {
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
    public void onResume() {
        super.onResume();
        qrCodeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView rankView = view.findViewById(R.id.rankView);
        TextView scoreView = view.findViewById(R.id.scoreView);
        TextView QRScanned = view.findViewById(R.id.qrCodesView);
        TextView rankText = view.findViewById(R.id.rankText);
        TextView QRText = view.findViewById(R.id.qrCodesText);
        TextView scoreText = view.findViewById(R.id.scoreText);
        Button sortingButton = view.findViewById(R.id.sortingButton);
        nameView.setText(user.getUserName());
        rankView.setText(String.valueOf(user.getRank()));
        QRScanned.setText(String.valueOf(user.getQRCodes().size()));
        scoreView.setText(String.valueOf(user.getTotalScore()));
        rankText.setText("RANK");
        QRText.setText("QRCODES FOUND");
        scoreText.setText("SCORE");

        // Passes User object from main activity to the QR code adapter
        qrCodeAdapter = new QRCodeAdapter(requireActivity(), user.getQRCodes(), user, false);
        ListView qrCodeList = view.findViewById(R.id.listy);
        qrCodeList.setAdapter(qrCodeAdapter);

        // On launch sorting is set by date (sortingTracker = 1)
        //      by points (sortingTracker = 2)
        //      by score (sortingTracker = 0)
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
            }
        });

        // Get the button view from the layout
        ImageButton editNameButton = view.findViewById(R.id.edit_name);
        editNameButton.setVisibility(View.GONE);
    }

}

