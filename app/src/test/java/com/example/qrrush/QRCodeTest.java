package com.example.qrrush;

import static org.junit.Assert.assertEquals;

import com.example.qrrush.model.QRCode;
import com.example.qrrush.model.Rarity;

import org.junit.Test;

public class QRCodeTest {
    @Test
    public void testGenerateCommon() {
        QRCode code = QRCode.withRarity(Rarity.Common);
        assertEquals(Rarity.Common, Rarity.fromHash(code.getHash()));
    }

    @Test
    public void testGenerateRare() {
        QRCode code = QRCode.withRarity(Rarity.Rare);
        assertEquals(Rarity.Rare, Rarity.fromHash(code.getHash()));
    }

    @Test
    public void testGenerateLegendary() {
        QRCode code = QRCode.withRarity(Rarity.Legendary);
        assertEquals(Rarity.Legendary, Rarity.fromHash(code.getHash()));
    }
}