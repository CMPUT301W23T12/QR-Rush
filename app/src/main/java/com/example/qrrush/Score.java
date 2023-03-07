package com.example.qrrush;

import java.util.Random;

public class Score {
    private int scoreValue;

    public Score(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public static int calculateScore(String hashValue, int numZeroes) {
        if (numZeroes >= 4) {
            return generateLegendaryScore();
        } else if (numZeroes >= 2) {
            return generateRareScore();
        } else {
            return generateCommonScore();
        }
    }

    public static int generateCommonScore() {
        return new Random().nextInt(10) + 1;
    }

    public static int generateRareScore() {
        return new Random().nextInt(40) + 11;
    }

    public static int generateLegendaryScore() {
        return new Random().nextInt(50) + 51;
    }

    static int getNumConsecutiveZeroes(String hashValue) {
        int numConsecutiveZeroes = 0;
        int maxConsecutiveZeroes = 0;
        for (int i = 0; i < hashValue.length(); i++) {
            if (hashValue.charAt(i) == '0') {
                numConsecutiveZeroes++;
                maxConsecutiveZeroes = Math.max(maxConsecutiveZeroes, numConsecutiveZeroes);
            } else {
                numConsecutiveZeroes = 0;
            }
        }
        return maxConsecutiveZeroes;
    }

}