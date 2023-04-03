package com.example.qrrush.model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.qrrush.R;
import com.example.qrrush.controller.RankComparator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;





public class MapQRCodeAdapter extends ArrayAdapter<User> {

    public MapQRCodeAdapter(Context context, List<User> users) {
        super(context, 0, users);
        Log.e("Debug", users.toString());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.map_qrcode_listview, parent, false);
        }

        TextView userNameView = convertView.findViewById(R.id.OtherUserNameView);
        TextView rankTextView = convertView.findViewById(R.id.rankTextView);
        TextView scoreView = convertView.findViewById(R.id.OtherUserScoreView);
        CircleImageView userImageView = convertView.findViewById(R.id.imageView2);

        User user = getItem(position);

        if (user != null) {
            userNameView.setText(user.getUserName());
            getAllCollection(user, rankTextView);
            rankTextView.setText(String.valueOf(user.getRank()));

            scoreView.setText(String.valueOf(user.getTotalScore()));
            if (user.hasProfilePicture()) {
                Glide.with(getContext())
                        .load(user.getProfilePicture())
                        .dontAnimate()
                        .into(userImageView);
            } else {
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(user.getUserName());
                userImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                TextDrawable drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(ResourcesCompat.getFont(getContext(), R.font.gatekept))
                        .toUpperCase()
                        .width(200)
                        .height(200)
                        .endConfig()
                        .buildRound(String.valueOf(user.getUserName().charAt(0)), color);
                userImageView.setImageDrawable(drawable);
            }

        }

        return convertView;
    }

    public void getAllCollection(User user, TextView rankView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profiles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            ArrayList<User> users = new ArrayList<User>();
                            users.clear();
                            for (QueryDocumentSnapshot document : value) {
                                Log.d("FirebaseWrapper", document.getId() + " => " + document.getData());
                                User u = new User(document.getId(),
                                        "",
                                        0,
                                        new ArrayList<>(),
                                        0,
                                        "");
                                ArrayList<String> hashes = (ArrayList<String>) document.get("qrcodes");
                                for (String hash : hashes) {
                                    u.addQRCodeWithoutFirebase(new QRCode(hash, new Timestamp(0, 0)));
                                }

                                users.add(u);
                            }
                            Collections.sort(users, new RankComparator());
                            for (int i = 0; i < users.size(); ++i) {
                                if (user.getUserName().matches(users.get(i).getUserName())) {
                                    user.setRank(((users.indexOf(users.get(i))) + 1));
                                    rankView.setText(String.valueOf(user.getRank()));
                                }
                            }
                        } else {
                            Log.d("FirebaseWrapper", "Error getting documents: ", error.fillInStackTrace());
                        }
                    }
                });
    }




}


