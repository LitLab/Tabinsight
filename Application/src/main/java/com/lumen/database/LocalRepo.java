package com.lumen.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lumen.constants.Keys;

/**
 * Local repository
 */

public class LocalRepo {

    private static SharedPreferences preferences;

    public static void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveId(String id) {
        preferences.edit().putString(Keys.DEVICE_ID, id).apply();
    }

    public static String getId() {
       return  preferences.getString(Keys.DEVICE_ID, "");
    }

    public static boolean isIdExist() {
        return preferences.contains(Keys.DEVICE_ID);
    }
}
