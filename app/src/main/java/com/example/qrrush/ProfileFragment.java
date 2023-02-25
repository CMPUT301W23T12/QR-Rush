package com.example.qrrush;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    User user;
    QRCodeAdapter QRCodeAdapter;

    public ProfileFragment() {
        // Required empty public constructor
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
        // TODO: These are copyrighted material. They shouldn't be allowed to be merged into main,
        //       since main should only contain things we can present.
        qrCodes.add(new QRCode("Mike Wazowski", 125, "Monstropolis",
                "https://static.wikia.nocookie.net/disney/images/d/d5/Profile_-_Mike_Wazowski.jpg/revision/latest?cb=20220608010955"));
        qrCodes.add(new QRCode("James P. Sullivan", 21441, "Monstropolis",
                "https://static.wikia.nocookie.net/pixar/images/8/8c/Sulleymonsters%2Cinc..png/revision/latest?cb=20170807224356"));
        qrCodes.add(new QRCode("Mary Gibbs(Boo)", 12, "Earth",
                "https://static.wikia.nocookie.net/disney/images/3/31/Profile_-_Boo.png/revision/latest?cb=20190313094050"));

        // Sets the users profile, still need to crop the image to fit a certain size
        // TODO: remove copyrighted material before merging into main.
        user = new User("TheLegend27",
                "987-6543-321", 1, 25412, qrCodes,
                "https://static.wikia.nocookie.net/intothespiderverse/images/b/b0/Wilson_Fisk_%28E-1610%29_001.png/revision/latest?cb=20210609163717");
        TextView contactView = view.findViewById(R.id.contactView);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView rankView = view.findViewById(R.id.rankView);
        TextView scoreView = view.findViewById(R.id.scoreView);
        ImageView profileView = view.findViewById(R.id.profileView);
        contactView.setText(user.getPhoneNumber());
        nameView.setText(user.getUserName());
        rankView.setText(String.valueOf(user.getRank()));
        scoreView.setText(String.valueOf(user.getScore()));

        // Image will be fit into the size of the image view
        Picasso
                .get()
                .load(user.getProfilePicture())
                .fit()
                .into(profileView);


        QRCodeAdapter = new QRCodeAdapter(requireActivity(), user.getQRCodes());
        ListView qrCodeList = view.findViewById(R.id.listy);
        qrCodeList.setAdapter(QRCodeAdapter);
    }
}