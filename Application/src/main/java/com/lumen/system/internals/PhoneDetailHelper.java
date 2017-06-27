package com.lumen.system.internals;

import android.content.Context;
import android.preference.PreferenceManager;

import com.lumen.constants.Keys;
import com.lumen.database.LocalRepo;


public class PhoneDetailHelper {

    public static String getUniqueId() {
        return LocalRepo.getId();
    }
}
