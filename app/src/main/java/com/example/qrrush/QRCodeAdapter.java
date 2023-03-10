package com.example.qrrush;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

// ArrayAdapter which displays QR Codes from the QRCode class.
public class QRCodeAdapter extends ArrayAdapter<QRCode> {
    ArrayList<QRCode> qrCodes;
    User user;
    HashMap<QRCode, String> commentsMap = new HashMap<>();

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
        TextView commentEditText = view.findViewById(R.id.commentEditText);
        nameView.setText(qrCode.getName());
        pointView.setText(String.valueOf(qrCode.getScore()));
        Optional<Location> l = qrCode.getLocation();
        if (commentsMap.containsKey(qrCode)) {
            commentEditText.setVisibility(View.VISIBLE);
            commentEditText.setText(commentsMap.get(qrCode));
        } else {
            commentEditText.setVisibility(View.GONE);
            commentEditText.setText("");
        }
        String location = "no location available";
        if (l.isPresent()) {
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
                // Clear the commentEditText view
                qrCode.setComment(null); // Clear the comment when the QR code is deleted
                commentsMap.remove(qrCode); // Remove the comment from the HashMap
                commentEditText.setText(""); // Clear the commentEditText view
                commentEditText.setVisibility(View.GONE);

                TextView qrScannedTextView = ((Activity) getContext()).findViewById(R.id.qrCodesView);
                TextView scoreView = ((Activity) getContext()).findViewById(R.id.scoreView);
                scoreView.setText(String.valueOf(user.getTotalScore()));
                qrScannedTextView.setText(String.valueOf(user.getNumberOfQRCodes()));
                notifyDataSetChanged();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(commentsMap.containsKey(qrCode) ? "Edit comment" : "Add comment");

                // Set up the input
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                if (commentsMap.containsKey(qrCode)) {
                    input.setText(commentsMap.get(qrCode));
                }
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = input.getText().toString();
                        commentsMap.put(qrCode, comment); // Update the comment in the HashMap
                        commentEditText.setVisibility(View.VISIBLE); // Set the comment EditText to visible
                        commentEditText.setText(comment); // Set the text of the comment EditText
                        notifyDataSetChanged(); // Notify the adapter that data has changed
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        return view;
    }
}
