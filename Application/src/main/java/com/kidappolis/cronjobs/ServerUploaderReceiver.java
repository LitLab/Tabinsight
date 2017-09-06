package com.kidappolis.cronjobs;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.kidappolis.constants.LogTags;

/**
 * Triggers the server uploader service only when there is internet connection
 */
public class ServerUploaderReceiver extends WakefulBroadcastReceiver {

    private static final Object mutex = new Object();
    private static boolean firstConnect = true;

    public ServerUploaderReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {

        synchronized (mutex) {

            Log.d(LogTags.APP_INFO.name(), "+++++++++++++++++++++ServerUploaderReceiver starting+++++++++++++++++++++++");

            final String action = intent.getAction();

            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManager.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected() && netInfo.isAvailable()) {
                if (firstConnect) {
                    Log.d("ServiceCreated", "ServiceCreated");
                    firstConnect = false;
                    Intent serviceIntent = new Intent(context, ServerUploader.class);
                    serviceIntent.setAction(action);
                    startWakefulService(context, serviceIntent);

                } else {
                    firstConnect = true;
                }
            }
        }
    }
}