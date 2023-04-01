package com.example.qrrush.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.qrrush.R;

import java.util.List;

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

    public static class LeaderboardAdapter extends ArrayAdapter<String> {

        private final Context context;
        private final List<String> names;

        public LeaderboardAdapter(Context context, List<String> names) {
            super(context, R.layout.leaderboard_content, names);
            this.context = context;
            this.names = names;
        }
    }
}
