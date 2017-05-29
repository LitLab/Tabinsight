package com.lumen.usage.satistics;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lumen.constants.LogTags;
import com.lumen.cronjobs.BootReceiver;
import com.lumen.cronjobs.ServerUploaderReceiver;
import com.lumen.cronjobs.StatsCollectionAlarmReceiver;
import com.lumen.database.AppUseInfo;
import com.lumen.database.AppsInfoDatasource;
import com.lumen.database.DeviceUseInfo;
import com.lumen.rest.ObservableCron;
import com.lumen.util.SharedPrefManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView lastSyncTv;
    private TextView remainingRecordsTv;
    private TextView apiAvailableTv;
    private TextView permissionGrantedTv;
    private TextView versionTv;
    private Switch wifiSwitch;
    private EditText pageCountEdit;
    private Button permissionButton;
    private Button updatePageButton;

    private Dialog loadingDialog;
    private SharedPrefManager sharedManager;

    private AppsInfoDatasource appsInfoDatasource;
    private ServerUploaderReceiver serverUploaderReceiver;
    private boolean mIsGranted;

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiSwitch.isChecked())
            return wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
        else
            return netInfo != null && netInfo.isConnected();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        serverUploaderReceiver = new ServerUploaderReceiver();

        findViews();

        enableBootReceiver();
//        setRecurringAlarmTest(this);
        setRecurringAlarmProd(this);
        wifiPresentAlarm();

        sharedManager = SharedPrefManager.getInstance(this);
        loadingDialog = new AlertDialog.Builder(DashboardActivity.this)
                .setMessage(R.string.loading_data_message)
                .show();
        appsInfoDatasource = new AppsInfoDatasource(this);

    }

    @Override
    protected void onResume() {
        readData();
        super.onResume();
    }


    private void enableBootReceiver() {
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    // count and show un-synced records
    private void readData() {
        try {
            appsInfoDatasource.open();
            int unsynced = appsInfoDatasource.getAppRecords().size();
            remainingRecordsTv.setText("Un-synced records remaining: " + String.valueOf(unsynced));
        } catch (SQLException e) {
            remainingRecordsTv.setText(R.string.unsynced_message);
        }

        updateSyncStatus();

        checkApi();

        permissionGrantedTv.setText("Usage statistics permission: ");
        int mode = ((AppOpsManager) getSystemService(Context.APP_OPS_SERVICE))
                .checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_ALLOWED) {
            mIsGranted = true;
            permissionButton.setVisibility(View.GONE);
            permissionGrantedTv.append("granted");

        } else {
            mIsGranted = false;
            permissionButton.setVisibility(View.VISIBLE);
            Toast.makeText(DashboardActivity.this, "Permission missing", Toast.LENGTH_LONG).show();
            permissionGrantedTv.append("not granted");
        }

        loadingDialog.dismiss();
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
        AlarmManager alarms = (AlarmManager) getSystemService(
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
        AlarmManager uploadAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        uploadAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                uploadTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                uploadPending);
    }

    /**
     * Used to set recurring alarms for test
     * Collection is set to every minute on the 30th second
     * and the upload to every minute on the 45th second
     */
    private void setRecurringAlarmTest(Context context) {
        long minuteCollect = 1000 * 60L;

        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeInMillis(System.currentTimeMillis());

        Intent downloader = new Intent(context, StatsCollectionAlarmReceiver.class);
        PendingIntent statCollector = PendingIntent.getBroadcast(context,
                0, downloader, 0);
        AlarmManager alarms = (AlarmManager) getSystemService(
                Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(),
                minuteCollect, statCollector);


        Calendar uploadTime = Calendar.getInstance();
        uploadTime.setTimeInMillis(System.currentTimeMillis());

        Intent uploadIntent = new Intent(context, ServerUploaderReceiver.class);
        PendingIntent uploadPending = PendingIntent.getBroadcast(context, 0, uploadIntent, 0);
        AlarmManager uploadAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        uploadAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                uploadTime.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR,
                uploadPending);
    }

    private void wifiPresentAlarm() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcast_action));
        registerReceiver(serverUploaderReceiver, intentFilter);
    }

    /**
     * Update all un-synced db records
     */
    private void updateSyncStatus() {
        Date dateSynced = sharedManager.readSyncProgress();
        if (dateSynced != null) {
            lastSyncTv.setText(dateToString(dateSynced));
        } else {
            lastSyncTv.setText(R.string.never_synced_message);
        }
        remainingRecordsTv.setText("Unsynced records remaining: " + appsInfoDatasource.getAppRecords().size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serverUploaderReceiver);
    }

    /**
     * Checks if server is working
     */
    private void checkApi() {
        apiAvailableTv.setText("Loading Api status...");
        if (isOnline()) {

            ObservableCron.getService(this).pingServer().enqueue(new Callback<Object>() {

                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful())
                        apiAvailableTv.setText("Api status: available");
                    else
                        apiAvailableTv.setText("Api status: not available");
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    apiAvailableTv.setText("Api status: not available");
                }
            });
        } else {
            apiAvailableTv.setText("Api status: not available or offline");
        }
    }

    private String dateToString(Date dateSynced) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US);
        return "Last sync day: " + simpleDateFormat.format(dateSynced);
    }

    private void findViews() {
        versionTv = (TextView) findViewById(R.id.version_tv);
        lastSyncTv = (TextView) findViewById(R.id.last_sync_tv);
        remainingRecordsTv = (TextView) findViewById(R.id.remaining_records_tv);
        apiAvailableTv = (TextView) findViewById(R.id.api_available_tv);
        permissionGrantedTv = (TextView) findViewById(R.id.permission_granted_tv);
        updatePageButton = (Button) findViewById(R.id.update_page_count);
        pageCountEdit = (EditText) findViewById(R.id.edit_page_count);
        pageCountEdit.setText(String.valueOf(ObservableCron.PAGE_COUNT));
        wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
        permissionButton = (Button) findViewById(R.id.get_permission);
        permissionButton.setOnClickListener(this);

        FloatingActionButton infoButton = (FloatingActionButton) findViewById(R.id.info);
        infoButton.setOnClickListener(this);

        Button checkApiButton = (Button) findViewById(R.id.check_api);
        checkApiButton.setOnClickListener(this);

        Button syncDataButton = (Button) findViewById(R.id.sync_data);
        syncDataButton.setOnClickListener(this);

        updatePageButton.setOnClickListener(this);
        versionTv.setText("Version " + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info: {
                startActivity(new Intent(this, AppUsageStatisticsActivity.class));
                finish();
                break;
            }
            case R.id.check_api: {
                Toast.makeText(DashboardActivity.this, "Api check started...", Toast.LENGTH_LONG).show();
                checkApi();
                break;
            }
            case R.id.sync_data: {
                if (mIsGranted) {
                    Toast.makeText(DashboardActivity.this, "Syncing started...", Toast.LENGTH_LONG).show();
                    uploadDeviceInfos(appsInfoDatasource.getDeviceRecords());

                } else {
                    Toast.makeText(this, "Please grant usage statistics permission", Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.get_permission: {
                try {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "error in settings permission", Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.update_page_count: {
                ObservableCron.PAGE_COUNT = Integer.valueOf(pageCountEdit.getText().toString());
                Toast.makeText(DashboardActivity.this, "Updated page count...", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void uploadDeviceInfos(List<DeviceUseInfo> data) {
        final Calendar calendar = Calendar.getInstance();

        for (DeviceUseInfo d : data) {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.DAY_OF_YEAR, (int) d.day);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            d.day = calendar.getTime().getTime();
        }

        Log.i("sync size ", "data size " + data.size());

        ObservableCron.getDeviceDataObservable(data, this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        Log.d(LogTags.APP_INFO.name(), "Syncing device info completed successfully");
                        appsInfoDatasource.truncateTillLast();
                        syncData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LogTags.APP_EXCEPTION.name(), "Could not sync uptime. Syncing apps...");
                        syncData();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(LogTags.APP_INFO.name(), "Syncing next device batch");
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void syncData() {
        List<AppUseInfo> records = appsInfoDatasource.getAppRecords();
        ObservableCron.getAppDataObservable(records, this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        Log.d(LogTags.APP_INFO.name(), "Syncing apps completed successfully");
                        Toast.makeText(DashboardActivity.this, "Sync success", Toast.LENGTH_LONG).show();
                        appsInfoDatasource.truncateTable();
                        SharedPrefManager.getInstance(getBaseContext())
                                .saveSyncProgress(new Date());
                        updateSyncStatus();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DashboardActivity.this, "Error syncing", Toast.LENGTH_LONG).show();
                        updateSyncStatus();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(LogTags.APP_INFO.name(), "Syncing next batch");
                    }
                });
    }
}