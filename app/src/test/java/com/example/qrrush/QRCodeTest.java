package com.example.qrrush;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QRCodeTest {

    @Test
    public void testGenerateCommon() {
        QRCode code = QRCode.withRarity(Rarity.Common);
        assertEquals(Rarity.fromScore(code.getScore()), Rarity.Common);
    }

    @Test
    public void testGenerateRare() {
        QRCode code = QRCode.withRarity(Rarity.Rare);
        assertEquals(Rarity.fromScore(code.getScore()), Rarity.Rare);
    }

    @Test
    public void testGenerateLegendary() {
        QRCode code = QRCode.withRarity(Rarity.Legendary);
        assertEquals(Rarity.fromScore(code.getScore()), Rarity.Legendary);
    }
}