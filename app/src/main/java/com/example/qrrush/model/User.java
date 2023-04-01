package com.example.qrrush.model;

import android.location.Location;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The class representing a user in the game.
 */
public class User implements Serializable {
    private String userName;
    private String phoneNumber;
    private int rank;
    private ArrayList<QRCode> qrCodes;
    // unsure of data type for now
    private String profilePicture;
    private HashMap<QRCode, String> commentMap = new HashMap<>();
    private int money;

    /**
     * Creates a new user with the given username, phone number, rank, total score,
     * and QR Codes.
     *
     * @param userName    The username to initialize the user with.
     * @param phoneNumber The phone number to initialize the user with.
     * @param rank        The rank to initialize the user with.
     * @param qrCodes     The list of QR Codes to initialize the user with.
     */
    public User(String userName, String phoneNumber, int rank, ArrayList<QRCode> qrCodes, int money,
            String profilePicture) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.rank = rank;
        this.qrCodes = qrCodes;
        this.money = money;
        this.profilePicture = profilePicture;
    }

    public User(String userName, int rank, ArrayList<QRCode> qrCodes, int money) {
        this.userName = userName;
        this.rank = rank;
        this.qrCodes = qrCodes;
        this.money = money;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public boolean hasProfilePicture() {
        return profilePicture != null && !profilePicture.isEmpty();
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public ArrayList<QRCode> getQRCodes() {
        return qrCodes;
    }

    /**
     * Returns the user's total score.
     */
    public int getTotalScore() {
        int result = 0;
        for (QRCode code : this.qrCodes) {
            result += code.getScore();
        }

        return result;
    }

    /**
     * Removes a QR Code from the user's account, both locally and in Firebase.
     *
     * @param code The QR code to remove from the user's account.
     * @throws InvalidParameterException The QR code given was not already in the
     *                                   user's account.
     */
    public void removeQRCode(QRCode code) {
        if (!this.qrCodes.contains(code)) {
            throw new InvalidParameterException("The QR Code specified to remove was not in the user's account!");
        }

        FirebaseWrapper.deleteQrcode("profiles", this.getUserName(), code.getHash());
        this.qrCodes.remove(code);
        HashMap<String, Object> updatedScore = new HashMap<>();
        updatedScore.put("score", this.getTotalScore());
        FirebaseWrapper.updateData("profiles", this.getUserName(), updatedScore);

    }

    public void setCommentWithoutUsingFirebase(QRCode code, String text) {
        if (text == null) {

            return;
        }

        if (this.commentMap.containsKey(code)) {
            this.commentMap.replace(code, text);
            return;
        }

        this.commentMap.put(code, text);
    }

    /**
     * Removes the comment for the given QR code.
     *
     * @param code The QRCode to remove the comment for.
     * @throws InvalidParameterException The QRCode given is not on the current
     *                                   user's account.
     */
    public void removeCommentFor(QRCode code) {
        if (!this.getQRCodes().contains(code)) {
            throw new InvalidParameterException("The QR Code specified to remove was not in the user's account!");
        }

        this.commentMap.remove(code);
        FirebaseWrapper.getData("profiles", this.getUserName(), documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.e("removeCommentFor", "Profile " + this.getUserName() +
                        " does not exist in the database!");
                return;
            }

            Map<String, Object> data = documentSnapshot.getData();
            ArrayList<String> hashes = (ArrayList<String>) documentSnapshot.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) documentSnapshot.get("qrcodescomments");

            comments.set(hashes.indexOf(code.getHash()), null);
            data.replace("qrcodescomments", comments);

            FirebaseWrapper.updateData("profiles", this.getUserName(), data);
        });
    }

    /**
     * Adds a comment to the QRCode code.
     *
     * @param code        The QRCode to add a comment to.
     * @param commentText The text to set the comment to.
     * @throws InvalidParameterException The QRCode given is not on the current
     *                                   user's account.
     */
    public void setCommentFor(QRCode code, String commentText) {
        if (!this.getQRCodes().contains(code)) {
            throw new InvalidParameterException("The QR Code specified to remove was not in the user's account!");
        }

        setCommentWithoutUsingFirebase(code, commentText);
        FirebaseWrapper.getData("profiles", this.getUserName(), documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.e("setCommentFor", "Profile " + this.getUserName() +
                        " does not exist in the database!");
                return;
            }

            Map<String, Object> data = documentSnapshot.getData();
            ArrayList<String> hashes = (ArrayList<String>) documentSnapshot.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) documentSnapshot.get("qrcodescomments");

            comments.set(hashes.indexOf(code.getHash()), commentText);
            data.replace("qrcodescomments", comments);

            FirebaseWrapper.updateData("profiles", this.getUserName(), data);
        });
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
        Map<String, Object> updatedMoney = new HashMap<>();
        updatedMoney.put("money", this.money);
        FirebaseWrapper.updateData("profiles", this.getUserName(), updatedMoney);
    }

    /**
     * Returns the comment for the given QR code or Optional.empty() if this QR code
     * doesn't have a
     * comment.
     *
     * @param code The QR code to get the comment for.
     * @throws InvalidParameterException The QRCode given is not on the current
     *                                   user's account.
     */
    public Optional<String> getCommentFor(QRCode code) {
        if (!this.getQRCodes().contains(code)) {
            throw new InvalidParameterException("The QR Code specified to remove was not in the user's account!");
        }

        if (!commentMap.containsKey(code)) {
            return Optional.empty();
        }

        return Optional.of(commentMap.get(code));
    }

    public void addQRCodeWithoutFirebase(QRCode code) {
        this.qrCodes.add(code);
    }

    /**
     * Adds a QR Code to the user, both locally and in Firebase.
     *
     * @param code The QR code to add to the user's account.
     */
    public void addQRCode(QRCode code) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference qrCodeRef = db.collection("qrcodes").document(code.getHash());

        qrCodeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                HashMap<String, Object> data = new HashMap<>();
                if (code.getLocation().isPresent()) {
                    Location l = code.getLocation().get();
                    data.put("location", new GeoPoint(l.getLatitude(), l.getLongitude()));
                } else {
                    Location l = null;
                    data.put("location", l);
                }
                data.put("date", new Timestamp(new Date()));

                if (document.exists()) {
                    data.put("scannedby", FieldValue.arrayUnion(this.getUserName()));
                    qrCodeRef.update(data)
                            .addOnSuccessListener(aVoid -> Log.d("addQRCode", "QR code document updated"))
                            .addOnFailureListener(e -> Log.w("addQRCode", "Error updating QR code document", e));
                } else {
                    data.put("scannedby", Arrays.asList(this.getUserName()));
                    qrCodeRef.set(data)
                            .addOnSuccessListener(aVoid -> Log.d("addQRCode", "New QR code document created"))
                            .addOnFailureListener(e -> Log.w("addQRCode", "Error creating new QR code document", e));
                }
            } else {
                Log.w("addQRCode", "Error getting document", task.getException());
            }
        });

        FirebaseWrapper.getData("profiles", this.getUserName(), documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.e("addQRCode", "Profile " + this.getUserName() +
                        " does not exist in the database!");
                return;
            }

            Map<String, Object> profileData = documentSnapshot.getData();
            this.qrCodes.add(code);
            ArrayList<String> codes = (ArrayList<String>) profileData.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) profileData.get("qrcodescomments");
            codes.add(code.getHash());
            comments.add(null);
            profileData.replace("qrcodescomments", comments);
            profileData.replace("qrcodes", codes);
            profileData.put("score", this.getTotalScore());
            FirebaseWrapper.updateData("profiles", this.getUserName(), profileData);
        });
    }
}
