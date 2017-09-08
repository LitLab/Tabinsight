package com.kidappolis.cronjobs;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.kidappolis.constants.LogTags;
import com.kidappolis.database.AppsInfoDatasource;
import com.kidappolis.database.DeviceUseInfo;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Class that collects device usage statistics (a.k.a usage time, how much time device has been up or not...)
 * and save it in the device cache
 */
public class StatsCollector extends Service {

    private static final String TAG = StatsCollector.class.getSimpleName();

    public StatsCollector() {}

    Intent intent;

    UsageStatsManager mUsageStatsManager;
    AppsInfoDatasource appsInfoDatasource;

    @Override
    public IBinder onBind(Intent intent) {
        this.intent = intent;

        return null;
    }

    @Override
    public void onCreate() {
        Log.d("sync lumen", "Stats collector service started");

        appsInfoDatasource = new AppsInfoDatasource(getApplicationContext());
        if (Build.VERSION.SDK_INT < 22) {
            mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");

        } else {
            mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }

        HashMap<String, UsageStats> usageStatsMap = getUsageStatsMap();

        try {
            appsInfoDatasource.open();

            // create entry for device usages
            DeviceUseInfo deviceUseInfo = new DeviceUseInfo();
            deviceUseInfo.day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            deviceUseInfo.elapsedRealtime = SystemClock.elapsedRealtime();
            deviceUseInfo.uptime = SystemClock.uptimeMillis();
            deviceUseInfo.timestamp = System.currentTimeMillis();
            appsInfoDatasource.createDeviceEntry(deviceUseInfo);


            // create entries for application usages
            for (String name : usageStatsMap.keySet()) {
                UsageStats usageStat = usageStatsMap.get(name);
                if (usageStat.getLastTimeUsed() != 0) {
                    Log.i("stats collector: ", "name: " + usageStat.getPackageName() + " last time used: " + String.valueOf(usageStat.getLastTimeUsed()));
                    appsInfoDatasource.createAppEntry(usageStat.getPackageName(), String.valueOf(usageStat.getLastTimeUsed()),
                            usageStat.getTotalTimeInForeground(), new Date().getTime());
                }

            }

        } catch (SQLException e) {
            Log.e(LogTags.APP_EXCEPTION.name(), e.getMessage());
            finish();
        }
        finally {
            appsInfoDatasource.close();
        }

        Log.i(LogTags.APP_INFO.name(), "Service stopped");
        finish();

    }

    private void finish() {
        if (intent != null) {
            StatsCollectionAlarmReceiver.completeWakefulIntent(intent);
        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        appsInfoDatasource.close();
    }

    private HashMap<String, UsageStats> getUsageStatsMap() {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_YEAR, -1);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.HOUR_OF_DAY, 0);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(),
                        System.currentTimeMillis());
        Log.i("sync lumen", "usage stats amount... " + queryUsageStats.size());

        HashMap<String, UsageStats> usageStatsMap = new HashMap<>();
        for (UsageStats qsm : queryUsageStats) {
            usageStatsMap.put(qsm.getPackageName(), qsm);
            Log.i("sync lumen", "last time used " + qsm.getLastTimeUsed());
        }

        return usageStatsMap;
    }
}
