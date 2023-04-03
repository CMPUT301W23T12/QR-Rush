package com.example.qrrush.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Daily quests class to display and track users progress of quests.
 */
public class Quest {
    QuestType type;
    int n;
    Rarity rarity;

    /**
     * Quest constructor
     *
     * @param type Quest type.
     * @param n if the quest type has a number its defined as n.
     * @param rarity if the quest type has a rarity its defined as rarity.
     */
    private Quest(QuestType type, int n, Rarity rarity) {
        this.type = type;
        this.n = n;
        this.rarity = rarity;
    }

    /**
     * Gets a list of the current users quests.
     *
     * @return and ArrayList<Quest> of the quests.
     */
    public static ArrayList<Quest> getCurrentQuests() {
        ArrayList<Quest> result = new ArrayList<>(3);

        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.DAY_OF_MONTH, 1);

        int num_quests = QuestType.values().length;
        int year = currentCal.get(Calendar.YEAR);
        int week = currentCal.get(Calendar.WEEK_OF_YEAR);
        int month = currentCal.get(Calendar.MONTH);
        int random_number = (week * year) ^ month;
        int quest_number = random_number % num_quests;
        for (int i = 0; i < 3; i += 1) {
            if (quest_number >= num_quests) {
                quest_number = 0;
            }

            result.add(Quest.ofType(QuestType.values()[quest_number], random_number));
            quest_number += 1;
        }

        return result;
    }

    /**
     * @return type of quest.
     */
    public QuestType getType() {
        return type;
    }

    /**
     * @return Type of rarity.
     */
    public Rarity getRarity() {
        return rarity;
    }

    /**
     * @return n.
     */
    public int getN() {
        return n;
    }

    /**
     *
     * @param type Type of quest.
     * @param seed Random number to use.
     * @return A quest.
     */
    private static Quest ofType(QuestType type, int seed) {
        Rarity[] rarities = {
                Rarity.Common,
                Rarity.Rare,
                Rarity.Legendary
        };

        switch (type) {
            case ScanNCodes:
            case SaveGeolocationForNCodes:
            case LeaveACommentOnNCodes:
                return new Quest(type, (seed + type.ordinal()) % 3 + 1, null);
            case BuyCodeOfRarity:
            case ScanCodeOfRarity:
                return new Quest(type, -1, rarities[(seed * seed) % rarities.length]);
        }

        return new Quest(QuestType.ScanNCodes, seed % 3, null);
    }

    /**
     * Gets a description of a quest
     *
     * @return A string of the description.
     */
    public String getDescription() {
        switch (type) {
            case ScanNCodes:
                return String.format(Locale.ENGLISH, "Scan %d QR codes", n);
            case SaveGeolocationForNCodes:
                return String.format(Locale.ENGLISH, "Save Geolocation for %d QR codes", n);
            case LeaveACommentOnNCodes:
                return String.format(Locale.ENGLISH, "Leave a comment on %d QR codes", n);
            case BuyCodeOfRarity:
                return String.format(Locale.ENGLISH, "Buy a %s QR code", rarity.name());
            case ScanCodeOfRarity:
                return String.format(Locale.ENGLISH, "Scan a %s QR code", rarity.name());
        }

        return null;
    }

}
