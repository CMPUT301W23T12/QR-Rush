package com.example.qrrush;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

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
        /**
         * Sets up initial profile
         */
        super.onViewCreated(view, savedInstanceState);

        ArrayList<QRCode> qrCodes = new ArrayList<>();
        byte[] b = new byte[20];
        Random rand = new Random();
        byte[][] sampleData = {
                new byte[20],
                new byte[20],
                new byte[20],
        };

        rand.nextBytes(sampleData[0]);
        rand.nextBytes(sampleData[1]);
        rand.nextBytes(sampleData[2]);

        qrCodes.add(new QRCode(sampleData[1]));
        qrCodes.add(new QRCode(sampleData[0]));
        qrCodes.add(new QRCode(sampleData[2]));

        // Sets the users profile, still need to crop the image to fit a certain size
        // TODO: remove copyrighted material before merging into main.
//        user = new User("TheLegend27",
//                "987-6543-321", 1, qrCodes);
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
    }
}