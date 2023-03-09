package com.example.qrrush;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Optional;

// ArrayAdapter which displays QR Codes from the QRCode class.
public class QRCodeAdapter extends ArrayAdapter<QRCode> {
    ArrayList<QRCode> qrCodes;
    User user;

    public QRCodeAdapter(Context context, ArrayList<QRCode> objects, User user) {
        super(context, 0, objects);
        qrCodes = objects;
        this.user = user;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.profile_content, parent, false);
        }
        QRCode qrCode = getItem(position);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView pointView = view.findViewById(R.id.pointView);
        TextView locationView = view.findViewById(R.id.locationView);
        ImageView imageView = view.findViewById(R.id.imageView);
        nameView.setText(qrCode.getName());
        pointView.setText(String.valueOf(qrCode.getScore()));
        Optional<Location> l = qrCode.getLocation();
        String location = "no location available";
        if (!l.isPresent()) {
            location = l.get().toString();
        }
        locationView.setText(location);
        /**
         * Delete button instance for each QR code item
         */
        Button deleteButton = view.findViewById(R.id.deleteButton);

        // Image will be fit into the size of the image view
        Bitmap b = Bitmap.createScaledBitmap(qrCode.getImage(), 100, 100, false);
        imageView.setImageBitmap(b);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            // TODO: Make a confirmation button before actually deleting things in the final
            //       product.
            @Override
            public void onClick(View v) {
                if (qrCodes.isEmpty()) {
                    return;
                }
                /**
                 * Updates the users total score and number of QR codes
                 * when the respective item is deleted
                 */
                user.setTotalScore(user.getTotalScore() - qrCodes.get(position).getScore());
                qrCodes.remove(position);
                user.setTotalQRcodes(user.getNumberOfQRCodes() - 1);
                TextView qrScannedTextView = ((Activity) getContext()).findViewById(R.id.qrCodesView);
                TextView scoreView = ((Activity) getContext()).findViewById(R.id.scoreView);
                scoreView.setText(String.valueOf(user.getTotalScore()));
                qrScannedTextView.setText(String.valueOf(user.getNumberOfQRCodes()));
                notifyDataSetChanged();
            }

        });
        return view;
    }
}
