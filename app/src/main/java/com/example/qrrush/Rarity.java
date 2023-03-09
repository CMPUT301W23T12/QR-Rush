package com.example.qrrush;

public enum Rarity {
    Common,
    Rare,
    Legendary;

    public static Rarity fromHash(String hash) {
        int numZeroes = QRCode.getMaxConsecutiveZeroes(hash);
        if (numZeroes >= Rarity.minConsecutiveZeroesFor(Legendary)) {
            return Legendary;
        }

        if (numZeroes >= Rarity.minConsecutiveZeroesFor(Rare)) {
            return Rare;
        }

        return Common;
    }

    /**
     * Returns the number of consecutive zeroes needed in a hash to achieve a certain rarity of QR
     * Code.
     */
    public static int minConsecutiveZeroesFor(Rarity r) {
        // common scores: from 1 to 4 consecutive zeroes.
        // rare scores: from 5 to 7 consecutive zeroes.
        // legendary scores: 8 or more consecutive zeroes
        if (r == Common) {
            return 0;
        } else if (r == Rare) {
            return 3;
        }

        return 5;
    }

}
