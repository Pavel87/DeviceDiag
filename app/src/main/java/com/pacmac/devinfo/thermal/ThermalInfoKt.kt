package com.pacmac.devinfo.thermal

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import java.util.Locale

private const val TAG = "ThermalInfo"

object ThermalInfoKt {

    const val EXPORT_FILE_NAME = "thermal_info"

    fun getThermalInfo(context: Context): List<UIObject> = buildList {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        // Thermal Status (API 29+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val status = powerManager.currentThermalStatus
            val statusLabel = when (status) {
                PowerManager.THERMAL_STATUS_NONE -> context.getString(R.string.thermal_none)
                PowerManager.THERMAL_STATUS_LIGHT -> context.getString(R.string.thermal_light)
                PowerManager.THERMAL_STATUS_MODERATE -> context.getString(R.string.thermal_moderate)
                PowerManager.THERMAL_STATUS_SEVERE -> context.getString(R.string.thermal_severe)
                PowerManager.THERMAL_STATUS_CRITICAL -> context.getString(R.string.thermal_critical)
                PowerManager.THERMAL_STATUS_EMERGENCY -> context.getString(R.string.thermal_emergency)
                PowerManager.THERMAL_STATUS_SHUTDOWN -> context.getString(R.string.thermal_shutdown)
                else -> context.getString(R.string.thermal_none)
            }
            add(UIObject(context.getString(R.string.thermal_status), statusLabel))
        } else {
            add(UIObject(context.getString(R.string.thermal_status), context.getString(R.string.thermal_requires_api)))
        }

        // Thermal Headroom (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val headroom = powerManager.getThermalHeadroom(10)
            val headroomValue = if (headroom.isNaN()) {
                "N/A"
            } else {
                "%.2f".format(headroom)
            }
            add(UIObject(context.getString(R.string.thermal_headroom), headroomValue))
        } else {
            add(UIObject(context.getString(R.string.thermal_headroom), "N/A"))
        }

        // CPU Headroom (API 36+)
        add(getHeadroom(context, isCpu = true))

        // GPU Headroom (API 36+)
        add(getHeadroom(context, isCpu = false))

        // Battery Temperature via sticky broadcast
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        if (temp > 0) {
            val tempCelsius = temp / 10.0
            add(UIObject(context.getString(R.string.thermal_battery_temp), "%.1f".format(tempCelsius), "\u00B0C"))
        } else {
            add(UIObject(context.getString(R.string.thermal_battery_temp), "N/A"))
        }
    }

    @android.annotation.SuppressLint("WrongConstant")
    private fun getHeadroom(context: Context, isCpu: Boolean): UIObject {
        val label = context.getString(
            if (isCpu) R.string.thermal_cpu_headroom else R.string.thermal_gpu_headroom
        )
        if (Build.VERSION.SDK_INT >= 36) {
            return try {
                val perfHintManager = context.getSystemService(Context.PERFORMANCE_HINT_SERVICE)
                        as? android.os.PerformanceHintManager
                if (perfHintManager != null) {
                    val methodName = if (isCpu) "getCpuHeadroom" else "getGpuHeadroom"
                    val method = perfHintManager.javaClass.getMethod(methodName)
                    val headroom = method.invoke(perfHintManager) as Float
                    if (headroom.isNaN() || headroom < 0) {
                        UIObject(label, "N/A")
                    } else {
                        UIObject(label, String.format(Locale.ENGLISH, "%.1f%%", headroom * 100))
                    }
                } else {
                    UIObject(label, "N/A")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get ${if (isCpu) "CPU" else "GPU"} headroom", e)
                UIObject(label, "N/A")
            }
        }
        return UIObject(label, "N/A")
    }
}
