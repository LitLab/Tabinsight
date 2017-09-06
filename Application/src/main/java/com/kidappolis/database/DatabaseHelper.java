package com.kidappolis.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by pushkar on 10/3/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Lumen";
    private static final int DATABASE_VERSION = 3;
    public static final String table_name_apps = "App_use_info";
    public static final String table_name_device = "Device_use_info";

    // app use info table columns
    public static final String app_col_name = "app_name";
    public static final String access_time_col_name = "access_time";
    public static final String use_time_col_name = "use_time";
    public static final String day = "day";
    public static final String yearDay = "year_day";
    public static final String device_id_col_name = "device_id";

    // device use info table columns
    public static final String timestamp = "device_timestamp";
    public static final String uptime = "device_uptime";
    public static final String elapsedRealtime = "device_elapsedtime";
    public static final String usageTime = "device_usage";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + table_name_apps + " (" +
                app_col_name + " text not null, " +
                access_time_col_name + " text not null, " +
                use_time_col_name + " float not null, " +
                day + " float not null, " +
                yearDay + " integer not null, " +
                device_id_col_name + " text" +
                ");");

        db.execSQL("CREATE TABLE " + table_name_device + " (" +
                day + " float not null, " +
                timestamp + " float not null, " +
                uptime + " float not null, " +
                elapsedRealtime + " float not null, " +
                usageTime + " integer not null, " +
                device_id_col_name + " text unique" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB", "rebuilding...");
        db.execSQL("DROP TABLE IF EXISTS " + table_name_apps);
        db.execSQL("DROP TABLE IF EXISTS " + table_name_device);
        onCreate(db);
    }
}