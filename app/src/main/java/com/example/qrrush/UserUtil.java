package com.example.qrrush;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserUtil {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String HAS_USERNAME_KEY = "hasUsername";
    private static final String UUID_KEY = "uuid";

    public static boolean isFirstTimeLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return !prefs.getBoolean(HAS_USERNAME_KEY, false);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // Get UUID from Firebase as well???
    // Store Profile:UUID in FB
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

    public static void setFirstTime(Context context, boolean hasUsername) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HAS_USERNAME_KEY, hasUsername);
        editor.apply();
    }


}
