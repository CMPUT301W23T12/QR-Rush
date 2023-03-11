package com.example.qrrush;

import java.util.Comparator;

/**
 * Custom Comparator to sort the QR codes by date
 */
public class DateComparator implements Comparator<QRCode> {
    @Override
    public int compare(QRCode QR1, QRCode QR2) {
        if (QR1.getDate() == QR2.getDate()) {
            return 0;
        }
        if (QR1.getDate() == null) {
            return -1;
        }
        if (QR2.getDate() == null) {
            return 1;
        }
        return QR1.getDate().compareTo(QR2.getDate());
    }
}
