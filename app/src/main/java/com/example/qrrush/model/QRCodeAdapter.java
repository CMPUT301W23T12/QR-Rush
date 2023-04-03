package com.example.qrrush.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrrush.R;
import com.example.qrrush.view.ProfileDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

/**
 * ArrayAdapter which displays QR Codes from the QRCode class.
 */
public class QRCodeAdapter extends ArrayAdapter<QRCode> {
    ArrayList<QRCode> qrCodes;
    User user;
    Boolean editable;
    Context context;

    /**
     * Creates a QRCodeAdapter given a list of QR Codes and a user.
     *
     * @param context The context object to pass to the super constructor.
     * @param objects The QR codes to display.
     * @param user    The user which is associated with the QR Codes.
     */
    public QRCodeAdapter(Context context, ArrayList<QRCode> objects, User user, Boolean editable) {
        super(context, 0, objects);
        qrCodes = objects;
        this.user = user;
        this.editable = editable;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(super.getContext()).inflate(R.layout.profile_content, parent, false);
        }

        QRCode qrCode = getItem(position);
        TextView nameView = view.findViewById(R.id.nameViewQR);
        TextView pointView = view.findViewById(R.id.pointView);
        TextView locationView = view.findViewById(R.id.locationView);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView commentEditText = view.findViewById(R.id.commentEditText);
        ImageView locationImage = view.findViewById(R.id.locationImageQRCode);
        locationImage.setVisibility(View.GONE);
        commentEditText.setVisibility(View.GONE);
        nameView.setText(qrCode.getName());

        if (qrCode.getLocationImage() != null) {
            Picasso.get().load(Uri.parse(qrCode.getLocationImage())).into(locationImage);
            locationImage.setVisibility(View.VISIBLE);
        }

        Optional<Location> l = qrCode.getLocation();

        if (user.getCommentFor(qrCode).isPresent()) {
            commentEditText.setVisibility(View.VISIBLE);
            commentEditText.setText("Comment: " + user.getCommentFor(qrCode).get());
        } else {
            commentEditText.setVisibility(View.GONE);
            commentEditText.setText("");
        }
        String location = "no location available";
        if (l.isPresent()) {
            Location loc = l.get();
            location = String.format(
                    Locale.ENGLISH,
                    "%.6f, %.6f",
                    loc.getLongitude(),
                    loc.getLatitude()
            );
        }
        locationView.setText(location);

        pointView.setText("Score: " + qrCode.getScore());

        ImageButton deleteButton = view.findViewById(R.id.deleteButton);

        if (!editable) {
            deleteButton.setVisibility(View.GONE);
        }

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
                user.removeQRCode(qrCode);
                notifyDataSetChanged();
            }
        });

        ImageButton commentButton = view.findViewById(R.id.commentButton);
        if (!editable) {
            commentButton.setVisibility(View.GONE);
        }
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(user.getCommentFor(qrCode).isPresent() ? "Edit comment" : "Add comment");

                // Set up the input
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                if (user.getCommentFor(qrCode).isPresent()) {
                    input.setText(user.getCommentFor(qrCode).get());
                }
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = input.getText().toString();
                        if (comment.isEmpty()) {
                            user.removeCommentFor(qrCode);
                            commentEditText.setVisibility(View.GONE);
                            commentEditText.setText("");
                            return;
                        }

                        user.setCommentFor(qrCode, comment);
                        ArrayList<Quest> quests = Quest.getCurrentQuests();
                        for (int i = 0; i < 3; i += 1) {
                            Quest q = quests.get(i);
                            if (q.getType() == QuestType.LeaveACommentOnNCodes) {
                                user.setQuestProgress(i, user.getQuestProgress().get(i) + 1);
                                break;
                            }
                        }

                        commentEditText.setVisibility(View.VISIBLE);
                        commentEditText.setText("Comment: " + user.getCommentFor(qrCode).get());
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

        view.setOnClickListener(v -> {
            // Display other users who have scanned the same QR code as an AlertDialog
            FirebaseWrapper.getScannedQRCodeData(qrCode.getHash(), user.getUserName(), (scannedByList) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Scanned by...");
                if (scannedByList.isEmpty()) {
                    builder.setMessage("No other user has scanned this QR code yet.");
                } else {
                    builder.setItems(scannedByList.toArray(new String[scannedByList.size()]),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int pos) {
                                    // position is tracked by "pos" so now we pass the clickable profile
                                    // We need to create a user object with that so we gotta use getUserData
                                    User user = scannedByList.get(pos);
                                        // scannedByList.get(pos) returns the name -> STRING
                                        // send the user object to the profile fragment
                                        new ProfileDialogFragment(user).show(
                                                ((AppCompatActivity) context).getSupportFragmentManager(),
                                                ""
                                        );


                                }
                            });
                }
                builder.setPositiveButton("OK", null);
                builder.show();
            });
        });

        return view;
    }
}
