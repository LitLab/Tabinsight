package com.kidappolis.cronjobs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * {@link BroadcastReceiver} that triggered every time after boot
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setRecurringAlarmTest(context);
        }

    }

    /**
     * Used to set recurring alarms for production
     * Collection is set to every hour on the 55th minute
     * and the upload to every day at 23:55 local time
     */
    private void setRecurringAlarmProd(Context context) {

        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeInMillis(System.currentTimeMillis());
        // collect stats on every 50th minute of the day
        updateTime.set(Calendar.MINUTE, 50);
        updateTime.set(Calendar.SECOND, 0);
        Intent downloader = new Intent(context, StatsCollectionAlarmReceiver.class);
        PendingIntent statCollector = PendingIntent.getBroadcast(context,
                0, downloader, 0);
        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, statCollector);

        // send data at 23:55 every day
        Calendar uploadTime = Calendar.getInstance();
        uploadTime.setTimeInMillis(System.currentTimeMillis());
        uploadTime.set(Calendar.HOUR_OF_DAY, 23);
        uploadTime.set(Calendar.MINUTE, 55);
        uploadTime.set(Calendar.SECOND, 0);

        Intent uploadIntent = new Intent(context, ServerUploaderReceiver.class);
        PendingIntent uploadPending = PendingIntent.getBroadcast(context, 0, uploadIntent, 0);
        AlarmManager uploadAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        uploadAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                uploadTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                uploadPending);    }

    /**
     * Used to set recurring alarms for test
     * Collection is set to every minute on the 30th second
     * and the upload to every minute on the 45th second
     */
    private void setRecurringAlarmTest(Context context) {
        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeInMillis(System.currentTimeMillis());

        Intent downloader = new Intent(context, StatsCollectionAlarmReceiver.class);
        PendingIntent statCollector = PendingIntent.getBroadcast(context,
                0, downloader, 0);
        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, statCollector);


        Calendar uploadTime = Calendar.getInstance();
        uploadTime.setTimeInMillis(System.currentTimeMillis());

        Intent uploadIntent = new Intent(context, ServerUploaderReceiver.class);
        PendingIntent uploadPending = PendingIntent.getBroadcast(context, 0, uploadIntent, 0);
        AlarmManager uploadAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        uploadAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                uploadTime.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR,
                uploadPending);
    }

}
