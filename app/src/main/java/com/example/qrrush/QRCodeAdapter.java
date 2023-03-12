package com.example.qrrush;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.InputType;
import android.util.Log;
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

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * ArrayAdapter which displays QR Codes from the QRCode class.
 */
public class QRCodeAdapter extends ArrayAdapter<QRCode> {
    ArrayList<QRCode> qrCodes;
    User user;
    HashMap<QRCode, String> commentsMap = new HashMap<>();

    /**
     * Creates a QRCodeAdapter given a list of QR Codes and a user.
     *
     * @param context The context object to pass to the super constructor.
     * @param objects The QR codes to display.
     * @param user    The user which is associated with the QR Codes.
     */
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

        Optional<Location> l = qrCode.getLocation();
        commentsMap.clear();

        if (commentsMap.containsKey(qrCode)) {
            commentEditText.setVisibility(View.VISIBLE);
            commentEditText.setText(commentsMap.get(qrCode));

        } else {
            commentEditText.setVisibility(View.GONE);
            commentEditText.setText("");
        }
        String location = "no location available";
        if (l.isPresent()) {
            Location loc = l.get();
            location = loc.getLongitude() + ", " + loc.getLatitude();
        }
        locationView.setText(location);

        // Delete button instance for each QR code item
        // Fetch comments for the QR code from Firebase
        FirebaseWrapper.getUserData(user.getUserName(), (Task<DocumentSnapshot> task) -> {
            if (!task.isSuccessful()) {
                Log.d("Firebase User", "Error getting user data");
                return;
            }

            DocumentSnapshot document = task.getResult();
            if (!document.exists()) {
                Log.e("Firebase User", "Document doesn't exist!");
                return;
            }
            pointView.setText(String.valueOf(document.getLong("score").intValue()));
            ArrayList<String> qrCodeComments = (ArrayList<String>) document.get("qrcodescomments");
            if (qrCodeComments != null && position < qrCodeComments.size()) {
                String comment = qrCodeComments.get(position);
                if (comment != null && !comment.isEmpty()) {
                    commentsMap.put(qrCode, comment);
                    commentEditText.setVisibility(View.VISIBLE);
                    commentEditText.setText(comment);
                }
            } else {
                // Add empty string to commentsMap to keep track of which QR codes have comments
                commentsMap.put(qrCode, "");
            }
        });

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

                // Get the QR code and its position in the list
                QRCode qrCode = qrCodes.get(position);

                // Remove the QR code from the list
                qrCodes.remove(position);

                // Remove the comment associated with the QR code from the comments map
                commentsMap.remove(qrCode);

                // Remove the comment for the QR code from Firebase
                FirebaseWrapper.deleteQrcode("profiles", user.getUserName(), qrCode.getHash());

                // Update the Firebase data with the new comments list
                FirebaseWrapper.getUserData(user.getUserName(), (Task<DocumentSnapshot> task) -> {
                    if (!task.isSuccessful()) {
                        Log.d("Firebase User", "Error getting user data");
                        return;
                    }

                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Log.e("Firebase User", "Document doesn't exist!");
                        return;
                    }

                    ArrayList<String> qrCodeComments = (ArrayList<String>) document.get("qrcodescomments");
                    if (qrCodeComments != null && position < qrCodeComments.size()) {
                        qrCodeComments.remove(position);

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("qrcodescomments", qrCodeComments);
                        FirebaseWrapper.updateData("profiles", user.getUserName(), data);
                    }
                });

                // Update the user's score and number of QR codes
                user.setTotalScore(user.getTotalScore() - qrCode.getScore());
                user.setTotalQRcodes(user.getNumberOfQRCodes() - 1);

                // Update the UI to reflect the changes
                TextView qrScannedTextView = ((Activity) getContext()).findViewById(R.id.qrCodesView);
                TextView scoreView = ((Activity) getContext()).findViewById(R.id.scoreView);
                scoreView.setText(String.valueOf(user.getTotalScore()));
                qrScannedTextView.setText(String.valueOf(user.getNumberOfQRCodes()));
                notifyDataSetChanged(); // Notify the adapter that data has changed
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

                        FirebaseWrapper.getUserData(user.getUserName(), (Task<DocumentSnapshot> task) -> {
                            if (!task.isSuccessful()) {
                                Log.d("Firebase User", "Error getting user data");
                                return;
                            }

                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                Log.e("Firebase User", "Document doesn't exist!");
                                return;
                            }

                            ArrayList<String> qrCodeComments = (ArrayList<String>) document.get("qrcodescomments");
                            if (qrCodeComments == null) {
                                qrCodeComments = new ArrayList<>();
                            }

                            int index = -1;
                            for (int i = 0; i < qrCodes.size(); i++) {
                                if (qrCodes.get(i).equals(qrCode)) {
                                    index = i;
                                    break;
                                }
                            }

                            if (index >= 0 && index < qrCodeComments.size()) {
                                qrCodeComments.set(index, comment);
                            } else {
                                qrCodeComments.add(comment);
                            }

                            HashMap<String, Object> data = new HashMap<>();
                            data.put("qrcodescomments", qrCodeComments);
                            FirebaseWrapper.updateData("profiles", user.getUserName(), data);
                            notifyDataSetChanged(); // Notify the adapter that data has changed
                        });
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
