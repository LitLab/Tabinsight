package com.lumen.database;


import android.util.Log;

public class DeviceUseInfo {

    private static final String TAG = DeviceUseInfo.class.getSimpleName();

    public long day;
    public long timestamp;
    public long uptime;
    public long elapsedRealtime;
    public long usageTime;
    public String deviceid;

    public void setUsageTime(DeviceUseInfo previous) {
        if (previous == null || this.uptime <= previous.uptime) {
            Log.i(TAG, "uptime = " + uptime);
            if (previous != null) {
                Log.i(TAG, "previous time = " + previous.uptime);
            }

            this.usageTime = this.uptime;
            return;
        }
        this.usageTime = this.uptime - previous.uptime;
    }

    @Override
    public String toString() {
        return "DeviceUseInfo{" +
                "day=" + day +
                ", timestamp=" + timestamp +
                ", uptime=" + uptime +
                ", elapsedRealtime=" + elapsedRealtime +
                ", usageTime=" + usageTime +
                ", deviceid='" + deviceid + '\'' +
                '}';
    }
}
