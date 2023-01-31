package com.pacmac.devinfo.battery;

import android.content.Context;
import android.os.BatteryManager;

import com.pacmac.devinfo.R;

public class BatteryUtils {

    public static String EXPORT_FILE_NAME = "battery_info";

    protected static String getBatStatus(Context context, int i) {

        switch (i) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return context.getString(R.string.charging);
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return context.getString(R.string.discharging);
            case BatteryManager.BATTERY_STATUS_FULL:
                return context.getString(R.string.full);
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return context.getString(R.string.not_charging);
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                return context.getString(R.string.unknown);
        }
        return context.getResources().getString(R.string.not_available_info);
    }

    protected static String getBatHealth(Context context, int value) {
        switch (value) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                return context.getString(R.string.cold);
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return context.getString(R.string.dead);
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return context.getString(R.string.good);
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return context.getString(R.string.over_voltage);
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return context.getString(R.string.overheated);
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return context.getString(R.string.unknown);
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return context.getString(R.string.unspecified_failure);
        }
        return context.getResources().getString(R.string.not_available_info);
    }

}
