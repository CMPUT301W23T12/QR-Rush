package com.example.qrrush;

import android.util.Log;

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
