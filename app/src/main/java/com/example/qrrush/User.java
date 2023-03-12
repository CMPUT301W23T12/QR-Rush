package com.example.qrrush;

import android.location.Location;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

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
    private int totalScore;
    // unsure of data type for now
    private String profilePicture;

    private int totalQRcodes;

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
        totalQRcodes = qrCodes.size();

        if (totalQRcodes == 0) {
            totalScore = 0;
        } else {
            for (int i = 0; i < qrCodes.size(); i++) {
                totalScore += qrCodes.get(i).getScore();
            }
            this.totalScore = totalScore;
        }
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

    public int getNumberOfQRCodes() {
        return totalQRcodes;
    }

    public void AddToTotalQRcodes() {
        this.totalQRcodes += 1;
    }

    public void setTotalQRcodes(int totalQRcodes) {
        this.totalQRcodes = totalQRcodes;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void AddToTotalScore(QRCode qrCode) {
        totalScore += qrCode.getScore();
    }

    public void setTotalScore(int totalScores) {
        this.totalScore = totalScores;
    }

    /**
     * Adds a QR Code to the user, both locally and in Firebase.
     *
     * @param code The QR code to add to the user's account.
     */
    public void addQRCode(QRCode code) {
        AddToTotalQRcodes();
        AddToTotalScore(code);
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
            newData.put("score", totalScore);
            FirebaseWrapper.updateData("profiles", this.getUserName(), newData);
            FirebaseWrapper.updateData("qrcodescomments", this.getUserName(), data);
            this.qrCodes.add(code);
        });
    }
}
