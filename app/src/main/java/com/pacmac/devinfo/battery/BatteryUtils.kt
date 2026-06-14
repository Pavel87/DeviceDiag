package com.pacmac.devinfo.battery

import android.content.Context
import android.os.BatteryManager
import com.pacmac.devinfo.R

object BatteryUtils {

    const val EXPORT_FILE_NAME = "battery_info"

    internal fun getBatStatus(context: Context, i: Int): String = when (i) {
        BatteryManager.BATTERY_STATUS_CHARGING -> context.getString(R.string.charging)
        BatteryManager.BATTERY_STATUS_DISCHARGING -> context.getString(R.string.discharging)
        BatteryManager.BATTERY_STATUS_FULL -> context.getString(R.string.full)
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> context.getString(R.string.not_charging)
        BatteryManager.BATTERY_STATUS_UNKNOWN -> context.getString(R.string.unknown)
        else -> context.getString(R.string.not_available_info)
    }

    internal fun getBatHealth(context: Context, value: Int): String = when (value) {
        BatteryManager.BATTERY_HEALTH_COLD -> context.getString(R.string.cold)
        BatteryManager.BATTERY_HEALTH_DEAD -> context.getString(R.string.dead)
        BatteryManager.BATTERY_HEALTH_GOOD -> context.getString(R.string.good)
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> context.getString(R.string.over_voltage)
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> context.getString(R.string.overheated)
        BatteryManager.BATTERY_HEALTH_UNKNOWN -> context.getString(R.string.unknown)
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> context.getString(R.string.unspecified_failure)
        else -> context.getString(R.string.not_available_info)
    }
}
