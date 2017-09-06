package com.kidappolis.util;

import android.app.AppOpsManager;
import android.content.Context;

/**
 * Permission helper
 */

public class Permissions {

    public static boolean isPermissionGranted(Context context) {
        final AppOpsManager appOpsManager = ((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE));
        int mode = appOpsManager
                .checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());


        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
