package com.example.qrrush;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ShopTest {

    @Test
    public void testGenerateHashCommon() {
        int numZeroes = 1;
        String hashValue = ShopFragment.generateHash(numZeroes);
        int calculatedScore = Score.calculateScore(hashValue, numZeroes);
        int expectedScore = Score.generateCommonScore();
        int expectedNumConsecutiveZeroes = 0;
        int actualNumConsecutiveZeroes = Score.getNumConsecutiveZeroes(hashValue);

        // Check if the score is within the expected range
        assertTrue(calculatedScore >= expectedScore - 100);
        assertTrue(calculatedScore <= expectedScore + 100);
        assertEquals(0,Score.getRarity(hashValue));
        // Check if the number of consecutive zeroes is correct
        assertTrue(expectedNumConsecutiveZeroes<=actualNumConsecutiveZeroes && actualNumConsecutiveZeroes <=1);
    }

    @Test
    public void testGenerateHashRare() {
        int numZeroes = 3;
        String hashValue = ShopFragment.generateHash(numZeroes);
        int calculatedScore = Score.calculateScore(hashValue, numZeroes);
        int expectedScore = Score.generateRareScore();
        int expectedNumConsecutiveZeroes = 2;
        int actualNumConsecutiveZeroes = Score.getNumConsecutiveZeroes(hashValue);

        // Check if the score is within the expected range
        assertTrue(calculatedScore >= expectedScore - 100);
        assertTrue(calculatedScore <= expectedScore + 100);
        assertEquals(1,Score.getRarity(hashValue));
        // Check if the number of consecutive zeroes is correct
        assertTrue(expectedNumConsecutiveZeroes <= actualNumConsecutiveZeroes && actualNumConsecutiveZeroes <= 3);
    }

    @Test
    public void testGenerateHashLegendary() {
        int numZeroes = 4;
        String hashValue = ShopFragment.generateHash(numZeroes);
        int calculatedScore = Score.calculateScore(hashValue, numZeroes);
        int expectedScore = Score.generateLegendaryScore();
        int expectedNumConsecutiveZeroes = 4;
        int actualNumConsecutiveZeroes = Score.getNumConsecutiveZeroes(hashValue);

        // Check if the score is within the expected range
        assertTrue(calculatedScore >= expectedScore - 100);
        assertTrue(calculatedScore <= expectedScore + 100);
        assertEquals(2,Score.getRarity(hashValue));
        // Check if the number of consecutive zeroes is correct
        assertTrue(expectedNumConsecutiveZeroes <= actualNumConsecutiveZeroes && actualNumConsecutiveZeroes <= 4);
    }
}