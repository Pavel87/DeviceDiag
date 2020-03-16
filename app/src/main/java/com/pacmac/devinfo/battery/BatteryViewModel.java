package com.pacmac.devinfo.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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


            list.add(new UIObject("Level", String.format(Locale.ENGLISH, "%.1f", batteryPct), "%"));
            list.add(new UIObject("Voltage", String.format(Locale.ENGLISH, "%d", batVoltage), "mV"));
            list.add(new UIObject("Temperature", String.format(Locale.ENGLISH, "%.1f", batTemp), "°C"));
            list.add(new UIObject("Is Present", batPresent ? " Present" : "Missing"));

            list.add(new UIObject("Is Present", batPresent ? " Present" : "Missing"));

            if (batPlugged == BatteryManager.BATTERY_PLUGGED_AC)
                list.add(new UIObject("Charging", "AC Power Connected"));
            else if (batPlugged == BatteryManager.BATTERY_PLUGGED_USB)
                list.add(new UIObject("Charging", "USB Connected"));
            else if (batPlugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                list.add(new UIObject("Charging", "Wireless"));
            } else {
                list.add(new UIObject("Charging", "None"));
            }

            list.add(new UIObject("Status", BatteryUtils.getBatStatus(context, batStatus)));
            list.add(new UIObject("Health", BatteryUtils.getBatHealth(context, batHealth)));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);

                // Battery capacity in microampere-hours, as an integer.
                int chargeCounterInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                list.add(new UIObject("Battery", String.valueOf(chargeCounterInt / 1000), "uAh"));


                // Average battery current in microamperes, as an integer. Positive values indicate net current
                // entering the battery from a charge source, negative values indicate net current discharging from
                // the battery. The time period over which the average is computed may depend on the fuel gauge hardware and its configuration.
                int avgCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                if (avgCurrentInt != Integer.MAX_VALUE && avgCurrentInt != Integer.MIN_VALUE) {
                    list.add(new UIObject("Average Current", String.valueOf(avgCurrentInt), "uA"));
                }

                // Instantaneous battery current in microamperes, as an integer. Positive values indicate
                // net current entering the battery from a charge source, negative values indicate net current discharging from the battery.
                int actualCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                if (actualCurrentInt != Integer.MAX_VALUE && actualCurrentInt != Integer.MIN_VALUE) {
                    list.add(new UIObject("Actual Current", String.valueOf(actualCurrentInt), "uA"));
                }

                // Battery remaining energy in nanowatt-hours, as a long integer.
                long remainingEnergyInt = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                if (remainingEnergyInt >= 0 && remainingEnergyInt != Long.MAX_VALUE) {
                    list.add(new UIObject("Remaining Energy", String.valueOf(remainingEnergyInt), "nWh"));
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // Compute an approximation for how much time (in milliseconds) remains until the battery
                    // is fully charged. Returns -1 if no time can be computed: either there is not enough current
                    // data to make a decision or the battery is currently discharging.
                    long timeToFullLong = batteryManager.computeChargeTimeRemaining();
                    if (timeToFullLong > 0 && timeToFullLong != Long.MAX_VALUE) {
                        list.add(new UIObject("Time To Full Charge", String.valueOf((int) (timeToFullLong / 1000)), "s"));
                    }
                }
            }


            batteryInfo.postValue(list);
        }
    };

}
