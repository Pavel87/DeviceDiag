package com.pacmac.devinfo.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.R;
import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.BATTERY_SERVICE;

public class BatteryViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "battery_info";
    private boolean isRegistered = false;

    private MutableLiveData<List<UIObject>> batteryInfo = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getBatteryInfo() {
        return batteryInfo;
    }
    public List<UIObject> getBatteryInfoForExport() {
        return batteryInfo.getValue();
    }


    protected void registerReceiver(Context context) {
        if (!isRegistered) {
            isRegistered = true;
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            context.registerReceiver(batteryReceiver, intentFilter);
        }

    }

    protected void unRegisterReceiver(Context context) {
        if (isRegistered) {
            context.unregisterReceiver(batteryReceiver);
            isRegistered = false;
        }
    }


    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = 100 * level / (float) scale;

            float batTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0f;
            int batVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            int batPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean batPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
            int batStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int batHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            String batTech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);


            List<UIObject> list = new ArrayList<>();


            list.add(new UIObject(context.getString(R.string.battery_level), String.format(Locale.ENGLISH, "%.1f", batteryPct), "%"));
            list.add(new UIObject(context.getString(R.string.battery_voltage), String.format(Locale.ENGLISH, "%d", batVoltage), "mV"));
            list.add(new UIObject(context.getString(R.string.battery_temperature), String.format(Locale.ENGLISH, "%.1f", batTemp), "°C"));
            list.add(new UIObject(context.getString(R.string.battery_is_present), batPresent ? context.getString(R.string.battery_present) : context.getString(R.string.battery_missing)));


            if (batPlugged == BatteryManager.BATTERY_PLUGGED_AC)
                list.add(new UIObject(context.getString(R.string.charging), context.getString(R.string.battery_ac_power)));
            else if (batPlugged == BatteryManager.BATTERY_PLUGGED_USB)
                list.add(new UIObject(context.getString(R.string.charging), context.getString(R.string.battery_usb_connected)));
            else if (batPlugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                list.add(new UIObject(context.getString(R.string.charging), context.getString(R.string.battery_wireless_charging)));
            } else {
                list.add(new UIObject(context.getString(R.string.charging), context.getString(R.string.none)));
            }

            list.add(new UIObject(context.getString(R.string.battery_status), BatteryUtils.getBatStatus(context, batStatus)));
            list.add(new UIObject(context.getString(R.string.battery_health), BatteryUtils.getBatHealth(context, batHealth)));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);

                // Battery capacity in microampere-hours, as an integer.
                int chargeCounterInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                list.add(new UIObject(context.getString(R.string.battery_capacity), String.valueOf(chargeCounterInt / 1000), "mAh"));


                // Average battery current in microamperes, as an integer. Positive values indicate net current
                // entering the battery from a charge source, negative values indicate net current discharging from
                // the battery. The time period over which the average is computed may depend on the fuel gauge hardware and its configuration.
                int avgCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                if (avgCurrentInt != Integer.MAX_VALUE && avgCurrentInt != Integer.MIN_VALUE) {
                    list.add(new UIObject(context.getString(R.string.battery_avg_current), String.valueOf(avgCurrentInt), "uA"));
                }

                // Instantaneous battery current in microamperes, as an integer. Positive values indicate
                // net current entering the battery from a charge source, negative values indicate net current discharging from the battery.
                int actualCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                if (actualCurrentInt != Integer.MAX_VALUE && actualCurrentInt != Integer.MIN_VALUE) {
                    list.add(new UIObject(context.getString(R.string.battery_actual_current), String.valueOf(actualCurrentInt), "uA"));
                }

                // Battery remaining energy in nanowatt-hours, as a long integer.
                long remainingEnergyInt = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                if (remainingEnergyInt >= 0 && remainingEnergyInt != Long.MAX_VALUE) {
                    list.add(new UIObject(context.getString(R.string.battery_remaining_energy), String.valueOf(remainingEnergyInt), "nWh"));
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // Compute an approximation for how much time (in milliseconds) remains until the battery
                    // is fully charged. Returns -1 if no time can be computed: either there is not enough current
                    // data to make a decision or the battery is currently discharging.
                    long timeToFullLong = batteryManager.computeChargeTimeRemaining();
                    if (timeToFullLong > 0 && timeToFullLong != Long.MAX_VALUE) {
                        list.add(new UIObject(context.getString(R.string.battery_time_to_full), String.valueOf((int) (timeToFullLong / 1000)), "s"));
                    }
                }
            }


            batteryInfo.postValue(list);
        }
    };

}
