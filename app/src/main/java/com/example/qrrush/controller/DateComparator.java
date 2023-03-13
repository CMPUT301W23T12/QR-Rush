package com.example.qrrush.controller;

import com.example.qrrush.model.QRCode;

import java.util.Comparator;

/**
 * Custom Comparator to sort the QR codes by date
 */
public class DateComparator implements Comparator<QRCode> {
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
