package com.lumen.database;


public class DeviceUseInfo {

    public long day;
    public long timestamp;
    public long uptime;
    public long elapsedRealtime;
    public long usageTime;
    public String deviceid;

    public void setUsageTime(DeviceUseInfo previous) {
        if (previous == null || this.uptime <= previous.uptime) {
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
