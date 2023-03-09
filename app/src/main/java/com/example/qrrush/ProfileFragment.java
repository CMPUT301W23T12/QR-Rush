package com.example.qrrush;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class ProfileFragment extends Fragment {
    User user;
    QRCodeAdapter QRCodeAdapter;

    int sortingTracker;
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
        Button sortingButton = view.findViewById(R.id.sortingButton);
        contactView.setText("Contact: "+user.getPhoneNumber());
        nameView.setText("Name: "+user.getUserName());
        rankView.setText("Rank: "+String.valueOf(user.getRank()));
        scoreView.setText("Score: "+String.valueOf(user.getTotalScore()));
        QRScanned.setText("Found: "+ String.valueOf(user.getNumberOfQRCodes()));
        /**
         * On launch sorting is set by date (sortingTracker = 1)
         *      by points (sortingTracker = 2)
         *      by score (sortingTracker = 0)
         */
        sortingTracker = 1;
        /**
         * Sorting button sorts arraylist of QR codes using custom comparators
         * Adapter gets updated each time the list gets sorted
         */
        sortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortingTracker == 0) {
                    sortingButton.setText("By Date");
                    sortingTracker += 1;
                    DateComparator dateComparator = new DateComparator();
                    Collections.sort(user.getQRCodes(), dateComparator);
                    QRCodeAdapter.notifyDataSetChanged();
                } else if (sortingTracker == 1){
                    sortingButton.setText("By Points");
                    sortingTracker += 1;
                    ScoreComparator scoreComparator = new ScoreComparator();
                    Collections.sort(user.getQRCodes(), scoreComparator);
                    QRCodeAdapter.notifyDataSetChanged();
                } else if (sortingTracker == 2){
                    sortingButton.setText("By Name");
                    sortingTracker = 0;
                    NameComparator nameComparator = new NameComparator();
                    Collections.sort(user.getQRCodes(), nameComparator);
                    QRCodeAdapter.notifyDataSetChanged();
                }
            }
        });

        /**
         * Passes User object from main activity to the QR code adapter
         */
        QRCodeAdapter = new QRCodeAdapter(requireActivity(), user.getQRCodes(), user);
        ListView qrCodeList = view.findViewById(R.id.listy);
        qrCodeList.setAdapter(QRCodeAdapter);
        QRCodeAdapter.notifyDataSetChanged();
    }
}