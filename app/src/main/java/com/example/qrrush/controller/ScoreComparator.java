package com.example.qrrush.controller;

import com.example.qrrush.model.QRCode;

import java.util.Comparator;

/**
 * Custom Comparator to sort the QR codes by score.
 */
public class ScoreComparator implements Comparator<QRCode> {
    /**
     * Compares two QRCode objects based on their score.
     * If the first score is greater than the second, -1 will be returned.
     * If the second score is greater than the first, 1 will be returned.
     * If the scores are equal, 0 will be returned.
     */
    @Override
    public int compare(QRCode QR1, QRCode QR2) {
        return Integer.compare(QR2.getScore(), QR1.getScore());
    }
}
