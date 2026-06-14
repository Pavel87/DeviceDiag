package com.pacmac.devinfo.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BatteryViewModelKt @Inject constructor() : ViewModel() {

    private var isRegistered = false

    private val _batteryData = MutableStateFlow<List<UIObject>>(emptyList())
    val batteryData: StateFlow<List<UIObject>> = _batteryData.asStateFlow()

    fun getBatteryInfoForExport(context: Context): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.title_activity_battery_info), "", ListType.TITLE))
        add(UIObject(context.getString(R.string.battery_param), context.getString(R.string.value), ListType.TITLE))
        addAll(batteryData.value)
    }

    fun registerReceiver(context: Context) {
        if (!isRegistered) {
            isRegistered = true
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(batteryReceiver, intentFilter)
        }
    }

    fun unRegisterReceiver(context: Context) {
        if (isRegistered) {
            context.unregisterReceiver(batteryReceiver)
            isRegistered = false
        }
    }

    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = 100 * level / scale.toFloat()
            val batTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0f
            val batVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            val batPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val batPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
            val batStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val batHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            val list: MutableList<UIObject> = ArrayList()
            list.add(UIObject(context.getString(R.string.battery_level), String.format(Locale.ENGLISH, "%.1f", batteryPct), "%"))
            list.add(UIObject(context.getString(R.string.battery_voltage), String.format(Locale.ENGLISH, "%d", batVoltage), "mV"))
            list.add(UIObject(context.getString(R.string.battery_temperature), String.format(Locale.ENGLISH, "%.1f", batTemp), "°C"))
            list.add(UIObject(context.getString(R.string.battery_is_present),
                if (batPresent) context.getString(R.string.battery_present) else context.getString(R.string.battery_missing)
            ))
            if (batPlugged == BatteryManager.BATTERY_PLUGGED_AC) {
                list.add(UIObject(context.getString(R.string.charging), context.getString(R.string.battery_ac_power)))
            } else if (batPlugged == BatteryManager.BATTERY_PLUGGED_USB) {
                list.add(UIObject(context.getString(R.string.charging), context.getString(R.string.battery_usb_connected)))
            } else if (batPlugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                list.add(UIObject(context.getString(R.string.charging), context.getString(R.string.battery_wireless_charging)))
            } else {
                list.add(UIObject(context.getString(R.string.charging), context.getString(R.string.none)))
            }
            list.add(UIObject(context.getString(R.string.battery_status), BatteryUtils.getBatStatus(context, batStatus)))
            list.add(UIObject(context.getString(R.string.battery_health), BatteryUtils.getBatHealth(context, batHealth)))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val chargeCounterInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                list.add(UIObject(context.getString(R.string.battery_capacity), (chargeCounterInt / 1000).toString(), "mAh"))

                val avgCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
                if (avgCurrentInt != Int.MAX_VALUE && avgCurrentInt != Int.MIN_VALUE) {
                    list.add(UIObject(context.getString(R.string.battery_avg_current), avgCurrentInt.toString(), "uA"))
                }

                val actualCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                if (actualCurrentInt != Int.MAX_VALUE && actualCurrentInt != Int.MIN_VALUE) {
                    list.add(UIObject(context.getString(R.string.battery_actual_current), actualCurrentInt.toString(), "uA"))
                }

                val remainingEnergyInt = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
                if (remainingEnergyInt >= 0 && remainingEnergyInt != Long.MAX_VALUE) {
                    list.add(UIObject(context.getString(R.string.battery_remaining_energy), remainingEnergyInt.toString(), "nWh"))
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val timeToFullLong = batteryManager.computeChargeTimeRemaining()
                    if (timeToFullLong > 0 && timeToFullLong != Long.MAX_VALUE) {
                        list.add(UIObject(context.getString(R.string.battery_time_to_full), (timeToFullLong / 1000).toInt().toString(), "s"))
                    }
                }
            }
            _batteryData.value = list
        }
    }
}
