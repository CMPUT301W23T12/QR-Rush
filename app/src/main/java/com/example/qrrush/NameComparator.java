package com.example.qrrush;

import java.util.Comparator;

/**
 * Custom Comparator to sort the QR codes alphabetically
 */
public class NameComparator implements Comparator<QRCode> {
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
