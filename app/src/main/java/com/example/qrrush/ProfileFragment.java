package com.example.qrrush;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemClickListener {
    User user;
    ArrayList<QRcode> qRcodes;
    ProfileAdapter profileAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Sets the users profile, still need to crop the image to fit a certain size
        User user = new User("TheLegend27",
                "987-6543-321",1,25412,qRcodes,
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

        // TODO Add QRcodes to user class and use the users classes built in arraylist for the profile adapter
        ListView usersQRcodes = view.findViewById(R.id.listy);
        qRcodes = new ArrayList<>();
        qRcodes.add(new QRcode("Mike Wazowski",125,"Monstropolis",
                "https://static.wikia.nocookie.net/disney/images/d/d5/Profile_-_Mike_Wazowski.jpg/revision/latest?cb=20220608010955"));
        qRcodes.add(new QRcode("James P. Sullivan",21441,"Monstropolis",
                "https://static.wikia.nocookie.net/pixar/images/8/8c/Sulleymonsters%2Cinc..png/revision/latest?cb=20170807224356"));
        qRcodes.add(new QRcode("Mary Gibbs(Boo)",12,"Earth",
                "https://static.wikia.nocookie.net/disney/images/3/31/Profile_-_Boo.png/revision/latest?cb=20190313094050"));

        profileAdapter = new ProfileAdapter(requireActivity(),qRcodes);
        usersQRcodes.setAdapter(profileAdapter);
        usersQRcodes.setOnItemClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }
    // TODO Fix the buttons so they delete the respective item they are attached to
    @Override

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Button deleteButton = requireView().findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!qRcodes.isEmpty()){
                    qRcodes.remove(position);
                    Log.e("QR Rush","this is a test");
                    profileAdapter.notifyDataSetChanged();
                }else{
                    return;
                }
            }
        });
    }
}