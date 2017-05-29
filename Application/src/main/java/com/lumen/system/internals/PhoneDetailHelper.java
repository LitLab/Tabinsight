package com.lumen.system.internals;

import android.content.Context;
import android.preference.PreferenceManager;

import com.lumen.constants.Keys;


public class PhoneDetailHelper {

    public static String getUniqueId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.DEVICE_ID, "invalid");
    }
}
