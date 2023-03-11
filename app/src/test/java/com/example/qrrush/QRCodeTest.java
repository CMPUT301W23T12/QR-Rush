package com.example.qrrush;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QRCodeTest {
    @Test
    public void testGenerateCommon() {
        QRCode code = QRCode.withRarity(Rarity.Common);
        assertEquals(Rarity.Common, code.getRarity());
    }

    @Test
    public void testGenerateRare() {
        QRCode code = QRCode.withRarity(Rarity.Rare);
        assertEquals(Rarity.Rare, code.getRarity());
    }

    @Test
    public void testGenerateLegendary() {
        QRCode code = QRCode.withRarity(Rarity.Legendary);
        assertEquals(Rarity.Legendary, code.getRarity());
    }

    @Test
    public void testImageSize() {
        // All the generated images should be 6x6 pixels large.
        QRCode code = QRCode.withRarity(Rarity.Common);
        assertEquals(6, code.getImage().getWidth());
        assertEquals(6, code.getImage().getHeight());
    }

    @Test
    public void testScorePositive() {
        QRCode code = QRCode.withRarity(Rarity.Legendary);
        assertTrue(code.getScore() >= 0);
    }
}