package com.example.qrrush;

import android.location.Location;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The class representing a user in the game.
 */
public class User {
    private String userName;
    private String phoneNumber;
    private int rank;
    private ArrayList<QRCode> qrCodes;
    // unsure of data type for now
    private String profilePicture;

    /**
     * Creates a new user with the given username, phone number, rank, total score, and QR Codes.
     *
     * @param userName    The username to initialize the user with.
     * @param phoneNumber The phone number to initialize the user with.
     * @param rank        The rank to initialize the user with.
     * @param totalScore  The score to initialize the user with.
     * @param qrCodes     The list of QR Codes to initialize the user with.
     */
    public User(String userName, String phoneNumber, int rank, int totalScore, ArrayList<QRCode> qrCodes) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.rank = rank;
        this.qrCodes = qrCodes;
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
     * @throws InvalidParameterException The QR code given was not already in the user's account.
     */
    public void removeQRCode(QRCode code) {
        if (!this.qrCodes.contains(code)) {
            throw new InvalidParameterException("The QR Code specified to remove was not in the user's account!");
        }

        FirebaseWrapper.deleteQrcode("profiles", this.getUserName(), code.getHash());
        this.qrCodes.remove(code);
    }

    /**
     * Adds a QR Code to the user, both locally and in Firebase.
     *
     * @param code The QR code to add to the user's account.
     */
    public void addQRCode(QRCode code) {
        HashMap<String, Object> data = new HashMap<>();
        if (code.getLocation().isPresent()) {
            Location l = code.getLocation().get();
            data.put("location", new GeoPoint(l.getLatitude(), l.getLongitude()));
        }
        data.put("date", new Timestamp(new Date()));
        FirebaseWrapper.addData("qrcodes", code.getHash(), data);

        FirebaseWrapper.getUserData(this.getUserName(), task -> {
            if (!task.isSuccessful()) {
                Log.e("addQRCode", "Failed to read from firebase!");
                return;
            }

            DocumentSnapshot ds = task.getResult();
            if (!ds.exists()) {
                Log.e("addQRCode", "User was deleted from Firebase!");
                return;
            }

            Map<String, Object> newData = ds.getData();

            // TODO: check if they've already scanned this one before.
            ArrayList<String> codes = (ArrayList<String>) newData.get("qrcodes");
            ArrayList<String> comments = (ArrayList<String>) newData.get("qrcodescomments");
            codes.add(code.getHash());
            comments.add("");
            newData.replace("qrcodescomments", comments);
            newData.replace("qrcodes", codes);
            newData.put("score", this.getTotalScore());
            FirebaseWrapper.updateData("profiles", this.getUserName(), newData);
            FirebaseWrapper.updateData("qrcodescomments", this.getUserName(), data);
            this.qrCodes.add(code);
        });
    }
}
