package com.example.qrrush.controller;

import com.example.qrrush.model.User;

import java.util.Comparator;

/**
 * Custom Comparator to sort the users by score to set there rank.
 */
public class RankComparator implements Comparator<User> {

    /**
     * Compares two User objects based on their score.
     * If the first score is greater than the second, 1 will be returned.
     * If the second score is greater than the first, 1 will be returned.
     * If the scores are equal, 0 will be returned.
     *
     * @param user1 The first user to be compared.
     * @param user2 The second user to be compared.
     * @return A negative integer, zero, or a positive integer as the first user's total score is less than, equal to, or greater than the second user's total score.
     */
    @Override
    public int compare(User user1, User user2) {
        return Integer.compare(user2.getTotalScore(), user1.getTotalScore());
    }
}
