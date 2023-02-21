package com.example.qrrush;

import java.util.ArrayList;

public class User {
    private String userName;
    private String phoneNumber;
    private String rank;
    private ArrayList<QRcode> QRcodes;
    // unsure of data type for now
    private String profilePicture;


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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public ArrayList<QRcode> getQRcodes() {
        return QRcodes;
    }

    public void addQRcode(QRcode qRcode) {
        QRcodes.add(qRcode);
    }

}
