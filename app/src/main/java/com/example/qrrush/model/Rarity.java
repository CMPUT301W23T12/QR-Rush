package com.example.qrrush.model;

/**
 * Represents a Rarity for a QR code.
 */
public enum Rarity {
    Common,
    Rare,
    Legendary;

    /**
     * Calculates the rarity for a given QR code hash.
     *
     * @param hash The hash to calculate the rarity of.
     * @return The rarity of the hash given.
     */
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
        if (r == Common) {
            return 0;
        } else if (r == Rare) {
            return 3;
        }

        return 5;
    }
}
