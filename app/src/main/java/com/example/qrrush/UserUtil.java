package com.example.qrrush;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * This class is meant to act as a helper class to the User class, it mainly uses SharedPreferences to track if its
 * the users first time login.
 */
public class UserUtil {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String HAS_USERNAME_KEY = "hasUsername";
    private static final String UUID_KEY = "uuid";
    private static final String USERNAME_KEY = "username";

    /**
     * This method checks if it's the user's first time logging in.
     *
     * @param context the context used to access the SharedPreferences file
     * @return true if the user has not set a username before, false otherwise
     */
    public static boolean isFirstTimeLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return !prefs.getBoolean(HAS_USERNAME_KEY, false);
    }

    /**
     * Generates a UUID and returns a String representation of the UUID.
     *
     * @return a String representation of the generated UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gets the username stored in SharedPreferences.
     *
     * @param context the context used to access the SharedPreferences file
     * @return the username stored in SharedPreferences, or "null" if not found
     */
    public static String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(USERNAME_KEY, "null");
    }

    /**
     * Sets the username in SharedPreferences.
     *
     * @param context  the context used to access the SharedPreferences file
     * @param username the username to set in SharedPreferences
     */
    public static void setUsername(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }

    /**
     * Gets the UUID stored in SharedPreferences. If the UUID is not found, generates a new one and stores it.
     *
     * @param context the context used to access the SharedPreferences file
     * @return the UUID stored in SharedPreferences, or a new generated UUID if not found
     */
    public static String getUUID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String uuid = prefs.getString(UUID_KEY, null);
        if (uuid == null) {
            uuid = generateUUID();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(UUID_KEY, uuid);
            editor.apply();
        }
        return uuid;
    }

    /**
     * Sets the "hasUsername" flag in SharedPreferences to indicate whether the user has set a username before.
     *
     * @param context     the context used to access the SharedPreferences file
     * @param hasUsername true if the user has set a username before, false otherwise
     */
    public static void setFirstTime(Context context, boolean hasUsername) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HAS_USERNAME_KEY, hasUsername);
        editor.apply();
    }


}
