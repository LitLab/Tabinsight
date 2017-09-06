package com.kidappolis.database;

/**
 * Created by pushkar on 10/3/15.
 */
public class AppUseInfo {
    public String app_name; // package id of the app
    public String access_time; // last time used within a single day
    public long use_time; // milliseconds of usage within a single day
    public long day; // day recorded
    public int yearDay;
    public boolean is_wifi; // weather the sync was done with wifi or 3g
    public String device_id;


    @Override
    public String toString() {
        return "AppUseInfo{" +
                "app_name='" + app_name + '\'' +
                ", access_time='" + access_time + '\'' +
                ", use_time=" + use_time +
                ", day=" + day +
                ", is_wifi=" + is_wifi +
                ", device_id='" + device_id + '\'' +
                '}';
    }
}
