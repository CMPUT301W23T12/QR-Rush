package com.example.qrrush;

import android.location.Location;
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
        ArrayList<QRCode> qrCodes = new ArrayList<>();
        byte[] data1 = "example data 1".getBytes();
        Location location1 = new Location("");
        byte[] data2 = "example data 2".getBytes();
        Location location2 = new Location("");
        byte[] data3 = "example data 3".getBytes();
        Location location3 = new Location("");
        QRCode qrCode1 = new QRCode(data1, location1);
        QRCode qrCode2 = new QRCode(data2, location2);
        QRCode qrCode3 = new QRCode(data3, location3);
        qrCodes.add(qrCode1);
        qrCodes.add(qrCode2);
        qrCodes.add(qrCode3);
        user = new User("TheLegend27",
                "987-6543-321", 1, 25412, qrCodes);

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

    }
}