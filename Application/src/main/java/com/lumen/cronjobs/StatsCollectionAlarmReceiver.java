package com.lumen.cronjobs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.lumen.constants.LogTags;

/**
 * Triggers device usage collector service
 */
public class StatsCollectionAlarmReceiver extends WakefulBroadcastReceiver {

    private static boolean firstConnect = true;


    public StatsCollectionAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LogTags.APP_INFO.name(),"------------Alarm received in StatsCollectionAlarmReceiver------------------");
        if(firstConnect) {
            startWakefulService(context, new Intent(context, StatsCollector.class));
            firstConnect = false;
        } else {
            firstConnect = true;
        }
    }
}
