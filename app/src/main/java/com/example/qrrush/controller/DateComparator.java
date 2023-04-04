package com.example.qrrush.controller;

import com.example.qrrush.model.QRCode;

import java.util.Comparator;

/**
 * Custom Comparator to sort the QR codes by date
 */
public class DateComparator implements Comparator<QRCode> {
    /**
     * Compares two QRCode objects based on their timestamp.
     * If both timestamps are null or equal, 0 will be returned.
     * If the first timestamp is null or less than the second, -1 will be returned.
     * If the second timestamp is null or less than the first, 1 will be returned.
     *
    *@param QR1 the first QRCode to compare.
    *@param QR2 the second QRCode to compare.
    *@return an integer indicating their order based on their dates.
     */
    @Override
    public int compare(QRCode QR1, QRCode QR2) {
        if (QR1.getTimestamp() == QR2.getTimestamp()) {
            return 0;
        }
        if (QR1.getTimestamp() == null) {
            return -1;
        }
        if (QR2.getTimestamp() == null) {
            return 1;
        }
        return QR1.getTimestamp().compareTo(QR2.getTimestamp());
    }
}
