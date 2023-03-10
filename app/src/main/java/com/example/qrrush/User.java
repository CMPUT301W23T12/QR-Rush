package com.example.qrrush;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// User class, should only be one user that can have multiple QR codes
public class User {
    private String userName;
    private String phoneNumber;
    private int rank;
    private ArrayList<QRCode> qrCodes;
    private int totalScore;
    // unsure of data type for now
    private String profilePicture;

    private int totalQRcodes;

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

    public void setTotalQRcodes(int totalQRcodes) {
        this.totalQRcodes = totalQRcodes;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScores) {
        this.totalScore = totalScores;
    }

    public void addQRCode(QRCode code) {
        HashMap<String, Object> data = new HashMap<>();
        if (code.getLocation().isPresent()) {
            data.put("location", code.getLocation().get());
        }

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
            codes.add(code.getHash());
            newData.replace("qrcodes", codes);

            FirebaseWrapper.updateData("profiles", this.getUserName(), newData);
            this.qrCodes.add(code);
        });
    }
}
