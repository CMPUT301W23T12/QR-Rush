package com.example.qrrush;

public enum Rarity {
    Common,
    Rare,
    Legendary;

    public static Rarity fromScore(int score) {
        if (score <= 100) {
            return Common;
        } else if (score <= 1000) {
            return Rare;
        }

        return Legendary;
    }
}
