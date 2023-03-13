package com.example.qrrush.controller;

import com.example.qrrush.model.QRCode;

import java.util.Comparator;

/**
 * Custom Comparator to sort the QR codes by score.
 */
public class ScoreComparator implements Comparator<QRCode> {
    @Override
    public int compare(QRCode QR1, QRCode QR2) {
        return Integer.compare(QR2.getScore(), QR1.getScore());
    }
}
