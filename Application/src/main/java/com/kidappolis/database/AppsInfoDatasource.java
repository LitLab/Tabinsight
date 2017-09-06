package com.kidappolis.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kidappolis.constants.LogTags;
import com.kidappolis.system.internals.PhoneDetailHelper;
import com.kidappolis.util.SharedPrefManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pushkar on 10/3/15.
 */
public class AppsInfoDatasource {

    private long device_timestamp = 0;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper dbHelper;
    private String[] allColums = {
            DatabaseHelper.app_col_name,
            DatabaseHelper.access_time_col_name,
            DatabaseHelper.use_time_col_name,
            DatabaseHelper.day,
            DatabaseHelper.yearDay,
            DatabaseHelper.device_id_col_name
    };
    private String device_id = "";
    private Context context;

    public void close() {
        dbHelper.close();
    }

    public AppsInfoDatasource(Context context) {
        dbHelper = new DatabaseHelper(context);
        device_id = PhoneDetailHelper.getUniqueId();
        device_timestamp = SharedPrefManager.getInstance(context).readDeviceTimestamp();
        this.context = context;
    }

    public void open() throws SQLException {
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public void createDeviceEntry(DeviceUseInfo model) {
        try {

            String query = "SELECT * FROM " + DatabaseHelper.table_name_device + " ORDER BY "
                    + DatabaseHelper.timestamp + " DESC LIMIT 1;";
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            DeviceUseInfo previousRecord = null;
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                previousRecord = cursorToDeviceUseInfo(cursor);
                cursor.close();
            }
            model.deviceid = device_id;
            model.setUsageTime(previousRecord);
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.day, model.day);
            values.put(DatabaseHelper.timestamp, model.timestamp);
            values.put(DatabaseHelper.uptime, model.uptime);
            values.put(DatabaseHelper.elapsedRealtime, model.elapsedRealtime);
            values.put(DatabaseHelper.usageTime, model.usageTime);
            values.put(DatabaseHelper.device_id_col_name, model.deviceid);

            sqLiteDatabase.insertWithOnConflict(DatabaseHelper.table_name_device, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.i("Created device entry: ", model.toString());
        } catch (Exception e) {
            Log.e("db", e.getMessage());
        }

    }

    public void createAppEntry(String appName, String accessTime, long useTime, long day) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.app_col_name, appName);
        values.put(DatabaseHelper.access_time_col_name, accessTime);
        values.put(DatabaseHelper.use_time_col_name, useTime);
        values.put(DatabaseHelper.day, day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(day));
        values.put(DatabaseHelper.yearDay, calendar.get(Calendar.DAY_OF_YEAR));
        values.put(DatabaseHelper.device_id_col_name, device_id);

        String clause = DatabaseHelper.app_col_name + "=? AND " + DatabaseHelper.yearDay + "=?";
        String[] args = {appName, String.valueOf(calendar.get(Calendar.DAY_OF_YEAR))};
        try {
            Cursor cursor = sqLiteDatabase.query(DatabaseHelper.table_name_apps, allColums, clause, args,
                    null, null, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0 && isSameDay(cursorToAppUseInfo(cursor), day)) {
                sqLiteDatabase.update(DatabaseHelper.table_name_apps, values, clause, args);
            } else {
                sqLiteDatabase.insert(DatabaseHelper.table_name_apps, null, values);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(LogTags.APP_EXCEPTION.name(), e.getMessage());
        }
    }

    private boolean isSameDay(AppUseInfo data, long day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(data.day));
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date(day));

        return calendar.get(Calendar.DAY_OF_YEAR) == calendar1.get(Calendar.DAY_OF_YEAR);
    }

    public void truncateTable() {
        checkDb();

        sqLiteDatabase.execSQL("DELETE FROM " + DatabaseHelper.table_name_apps + ";");
    }

    public void checkDb() {
        if (!sqLiteDatabase.isOpen()) {
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
    }

    public void closeDb() {
        if (sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public void truncateTillLast() {
        checkDb();

        long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, DatabaseHelper.table_name_device);
        if (count != 1) {
            String ALTER_TBL = "delete from " + DatabaseHelper.table_name_device +
                    " where " + DatabaseHelper.timestamp + " in (select " + DatabaseHelper.timestamp + " from " + DatabaseHelper.table_name_device + " order by device_timestamp LIMIT " + (count - 1) + ");";

            sqLiteDatabase.execSQL(ALTER_TBL);

            String lastDeviceStanding = "SELECT * from " + DatabaseHelper.table_name_device;
            Cursor lastCursor = sqLiteDatabase.rawQuery(lastDeviceStanding, null);
            if (lastCursor != null && lastCursor.moveToFirst()) {
                DeviceUseInfo lastDevice = cursorToDeviceUseInfo(lastCursor);
                SharedPrefManager.getInstance(context).saveDeviceTimestamp(lastDevice.timestamp);
                lastCursor.close();
            }
            Log.e(LogTags.APP_EXCEPTION.name(), "DELETING DEVICE INFO");
        }
    }

    public List<AppUseInfo> getAppRecords() {
        ArrayList<AppUseInfo> result = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.table_name_apps, allColums, null, null,
                null, null, null);
        cursor.moveToFirst();

        Log.d(LogTags.APP_DEBUG.name(), "Reading appsinfo (AppRecords) from database");

        //cursor.isAfterLast()
        while (!cursor.isAfterLast()) {
            AppUseInfo appUseInfo = cursorToAppUseInfo(cursor);
            result.add(appUseInfo);
            cursor.moveToNext();
            Log.d(LogTags.APP_DEBUG.name(), appUseInfo.app_name);
        }
        cursor.close();
        return result;
    }

    public List<AppUseInfo> getWifiAppRecords() {
        ArrayList<AppUseInfo> result = new ArrayList<>();
        long today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (today == 1) {
            today = 400;
        }

        String query = "SELECT * FROM " + DatabaseHelper.table_name_apps +
                " WHERE year_day < " + today +
                " ORDER BY " + DatabaseHelper.day + ";";
//        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.table_name_device, null, null, null,
//                null, null, null);
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();

        Log.d(LogTags.APP_DEBUG.name(), "Reading appsinfo(WifiAppRecord) from database");

        while (!cursor.isAfterLast()) {
            AppUseInfo appUseInfo = cursorToAppUseInfo(cursor);
            result.add(appUseInfo);
            cursor.moveToNext();
            Log.d(LogTags.APP_DEBUG.name(), appUseInfo.app_name);
        }
        cursor.close();
        return result;
    }

    public List<DeviceUseInfo> getDeviceRecords() {
        ArrayList<DeviceUseInfo> result = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.table_name_device, null, null, null,
                null, null, null);
        cursor.moveToFirst();

        Log.d(LogTags.APP_DEBUG.name(), "Reading deviceinfo from database");

        while (!cursor.isAfterLast()) {
            DeviceUseInfo deviceUseInfo = cursorToDeviceUseInfo(cursor);
            if (deviceUseInfo.timestamp != device_timestamp) {
                result.add(deviceUseInfo);
            }
            cursor.moveToNext();
            Log.d(LogTags.APP_DEBUG.name(), String.valueOf(deviceUseInfo.usageTime));
        }
        cursor.close();

        return result;
    }

    public List<DeviceUseInfo> getWifiDeviceRecords() {
        ArrayList<DeviceUseInfo> result = new ArrayList<>();
        long today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (today == 1) {
            today = 400;
        }
        Log.e("Karakondzula", today + "");
        String query = "SELECT * FROM " + DatabaseHelper.table_name_device +
                " WHERE day < " + today +
                " ORDER BY " + DatabaseHelper.timestamp + ";";
//        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.table_name_device, null, null, null,
//                null, null, null);
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();

        Log.d(LogTags.APP_DEBUG.name(), "Reading deviceinfo from database");

        Log.e("karakondzula2", cursor.getCount() + "");
        //cursor.isAfterLast();
        while (!cursor.isAfterLast()) {
            DeviceUseInfo deviceUseInfo = cursorToDeviceUseInfo(cursor);
            if (deviceUseInfo.timestamp != device_timestamp)//avoid duplicate in DB
                result.add(deviceUseInfo);
            Log.e("Karakondzula", deviceUseInfo.toString());
            cursor.moveToNext();
            Log.d(LogTags.APP_DEBUG.name(), String.valueOf(deviceUseInfo.usageTime));

        }
        Log.e("karakondzula2size", result.size() + "");
        cursor.close();

        Log.d(LogTags.APP_DEBUG.name(), "Device info count:" + result.size());

        return result;
    }

    private AppUseInfo cursorToAppUseInfo(Cursor cursor) {
        AppUseInfo result = new AppUseInfo();
        result.app_name = cursor.getString(0);
        result.access_time = cursor.getString(1);
        result.use_time = cursor.getLong(2);
        result.day = cursor.getLong(3);
        result.yearDay = cursor.getInt(4);
        result.device_id = PhoneDetailHelper.getUniqueId();
        return result;
    }

    private DeviceUseInfo cursorToDeviceUseInfo(Cursor cursor) {
        DeviceUseInfo result = new DeviceUseInfo();
        result.day = cursor.getLong(0);
        result.timestamp = cursor.getLong(1);
        result.uptime = cursor.getLong(2);
        result.elapsedRealtime = cursor.getLong(3);
        result.usageTime = cursor.getLong(4);
        result.deviceid = PhoneDetailHelper.getUniqueId();
        return result;
    }
}
