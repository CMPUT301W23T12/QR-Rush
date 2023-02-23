package com.example.qrrush;

import java.util.ArrayList;

// User class, should only be one user that can have multiple QR codes
public class User {
    private String userName;
    private String phoneNumber;
    private int rank;
    private int score;
    private ArrayList<QRcode> QRcodes;
    // unsure of data type for now
    private String profilePicture;

    public User(String userName, String phoneNumber, int rank, int score, ArrayList<QRcode> QRcodes, String profilePicture) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.rank = rank;
        this.score = score;
        this.QRcodes = QRcodes;
        this.profilePicture = profilePicture;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<QRcode> getQRcodes() {
        return QRcodes;
    }

    public void addQRcode(QRcode qRcode) {
        QRcodes.add(qRcode);
    }

}
