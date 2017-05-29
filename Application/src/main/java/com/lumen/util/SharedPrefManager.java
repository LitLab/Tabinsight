package com.lumen.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;


public class SharedPrefManager {

    private static final String KEY_SYNC_IN_PROGRESS = "lastSynced";
    private static final String KEY_SYNC_LAST_DEVICE = "device_time_stamp";
    private static final String KEY_WIFI_LOCK = "is_wifi_locked";

    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        this.context = context;
        //preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public static SharedPrefManager getInstance(Context context) {
        return new SharedPrefManager(context);
    }

    public void saveSyncProgress(Date lastSyncDate) {
        editor.putLong(KEY_SYNC_IN_PROGRESS, lastSyncDate.getTime());
        editor.commit();
    }

    public Date readSyncProgress() {
        if (preferences.getLong(KEY_SYNC_IN_PROGRESS, 0) == 0)
            return null; // never got synced
        return new Date(preferences.getLong(KEY_SYNC_IN_PROGRESS, 0));
    }

    public void saveDeviceTimestamp(long timestamp) {
        Log.i("TIMESTAMP", "Last timestamp saved " + timestamp);
        editor.putLong(KEY_SYNC_LAST_DEVICE, timestamp);
        editor.commit();
    }

    public long readDeviceTimestamp() {
        Log.i("TIMESTAMP", "Last timestamp read " + preferences.getLong(KEY_SYNC_LAST_DEVICE, 0));
        return preferences.getLong(KEY_SYNC_LAST_DEVICE, 0);
    }

    public void saveWifiState(String wifistate) {
        editor.putString(KEY_WIFI_LOCK, wifistate);
        editor.commit();
    }
}