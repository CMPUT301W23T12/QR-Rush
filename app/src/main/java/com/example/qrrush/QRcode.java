package com.example.qrrush;

public class QRcode {
    private String name;
    private int value;

//    Unsure of data type for now
    private String location;
    private String imageURL;

    public QRcode(String name, int value, String location, String imageURL) {
        this.name = name;
        this.value = value;
        this.location = location;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String image) {
        this.imageURL = imageURL;
    }
}
