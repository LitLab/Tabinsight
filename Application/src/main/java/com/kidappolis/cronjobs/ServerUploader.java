package com.kidappolis.cronjobs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kidappolis.constants.LogTags;
import com.kidappolis.database.AppUseInfo;
import com.kidappolis.database.AppsInfoDatasource;
import com.kidappolis.database.DeviceUseInfo;
import com.kidappolis.rest.RemoteRepo;
import com.kidappolis.util.NetworkUtils;
import com.kidappolis.util.SharedPrefManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * uploads to the server all the relevant data : device and app/package usage time
 */
public class ServerUploader extends Service {

    private static final String TAG = ServerUploader.class.getSimpleName();

    private boolean isWiFiChangeAction = false;
    private Intent mIntent;

    public ServerUploader() {
    }

    AppsInfoDatasource appsInfoDatasource;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("sync lumen", "server uploader service started");

        mIntent = intent;
        appsInfoDatasource = new AppsInfoDatasource(getApplicationContext());
        List<DeviceUseInfo> deviceUseInfos = new ArrayList<>();

        if (intent.getAction() != null) {
            Log.e("karakondzula", intent.getAction());
            isWiFiChangeAction = true;

            try {
                appsInfoDatasource.open();
                deviceUseInfos = appsInfoDatasource.getWifiDeviceRecords();

            } catch (SQLException e) {
                Log.e("sync lumen", "error in sql");
                e.printStackTrace();

                finish();
            }

        } else {

            try {
                appsInfoDatasource.open();
                deviceUseInfos = appsInfoDatasource.getDeviceRecords();

                Log.d("sync lumen", "device use info size " + deviceUseInfos.size());

            } catch (Exception e) {
                finish();
                Log.e(LogTags.APP_EXCEPTION.name(), e.getMessage());
            }
            finally {
                appsInfoDatasource.close();
            }
        }

        uploadDeviceInfos(deviceUseInfos);
        Log.i(LogTags.APP_INFO.name(), "Service stopped");

        return START_NOT_STICKY;
    }

    private void finish() {
        ServerUploaderReceiver.completeWakefulIntent(mIntent);
        stopSelf();
    }

    @Override
    public void onCreate() {}

    @SuppressWarnings("unchecked")
    private void uploadDeviceInfos(List<DeviceUseInfo> data) {
        Log.d("sync lumen", "device info size " + data.size());

        final Calendar calendar = Calendar.getInstance();

        for (DeviceUseInfo d : data) {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.DAY_OF_YEAR, (int) d.day);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            d.day = calendar.getTime().getTime();
        }

        RemoteRepo.getDeviceDataObservable(data, getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        Log.d(LogTags.APP_INFO.name(), "Syncing device info completed successfully");
                        appsInfoDatasource.truncateTillLast();

                        if (isWiFiChangeAction) {
                            uploadAppInfos(appsInfoDatasource.getWifiAppRecords());

                        } else {
                            uploadAppInfos(appsInfoDatasource.getAppRecords());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LogTags.APP_EXCEPTION.name(), "Could not sync uptime. Syncing apps...");
                        e.printStackTrace();

                        finish();
                    }

                    @Override
                    public void onNext(Object o) {}
                });
    }

    @SuppressWarnings("unchecked")
    private void uploadAppInfos(List<AppUseInfo> data) {
        Log.i("sync lumen", "app info size " + data.size());

        boolean isWifiOn = NetworkUtils.isWifi(getApplicationContext());
        for (AppUseInfo model : data) {
            model.is_wifi = isWifiOn;
        }

        RemoteRepo.getAppDataObservable(data, getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error upload app infos " + e.getMessage());
                        SharedPrefManager.getInstance(getApplicationContext()).saveWifiState("not_syncing");
                        Log.e(LogTags.APP_EXCEPTION.name(), e.getMessage());
                        finish();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(LogTags.APP_INFO.name(), "Syncing apps completed successfully");

                        appsInfoDatasource.truncateTable();
                        SharedPrefManager.getInstance(getApplicationContext())
                                .saveSyncProgress(new Date());
                        SharedPrefManager.getInstance(getBaseContext()).saveWifiState("not_syncing");
                        finish();
                    }
                });
    }


    @Override
    public void onDestroy() {
        SharedPrefManager.getInstance(getApplicationContext()).saveWifiState("not_syncing");
        if (appsInfoDatasource != null)
            appsInfoDatasource.closeDb();
    }
}