package com.example.qrrush;

import java.util.ArrayList;

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

    public void addQRCode(QRCode qrCode) {
        qrCodes.add(qrCode);
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
}
