package com.example.qrrush.controller;

import com.example.qrrush.model.QRCode;

import java.util.Comparator;

/**
 * Custom Comparator to sort the QR codes alphabetically
 */
public class NameComparator implements Comparator<QRCode> {
    /**
     * Compares two QRCode objects based on their name.
     * If both names are null or equal, 0 will be returned.
     * If the first name is null or less than the second, -1 will be returned.
     * If the second name is null or less than the first, 1 will be returned.
     */
    @Override
    public int compare(QRCode QR1, QRCode QR2) {
        if (QR1.getName() == QR2.getName()) {
            return 0;
        }
        if (QR1.getName() == null) {
            return -1;
        }
        if (QR2.getName() == null) {
            return 1;
        }
        return QR1.getName().compareTo(QR2.getName());
    }
}
