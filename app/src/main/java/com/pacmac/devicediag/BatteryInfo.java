package com.pacmac.devicediag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class BatteryInfo extends ActionBarActivity {

    private IntentFilter intentFilter;
    private BroadcastReceiver batteryUpdates;

    private TextView batteryLevel;
    private TextView batteryTemperature;
    private TextView batteryPlugged;
    private TextView batteryPresent;
    private TextView batteryVoltage;
    private TextView batteryTechnology;
    private TextView batteryStatus;
    private TextView batteryHealth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_info);
        batteryLevel = (TextView) findViewById(R.id.level);
        batteryPlugged = (TextView) findViewById(R.id.plugged);
        batteryPresent = (TextView) findViewById(R.id.present);
        batteryTechnology = (TextView) findViewById(R.id.technology);
        batteryVoltage = (TextView) findViewById(R.id.voltage);
        batteryTemperature = (TextView) findViewById(R.id.temp);
        batteryStatus = (TextView) findViewById(R.id.batStatus);
        batteryHealth = (TextView) findViewById(R.id.health);

        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);


        batteryUpdates = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = 100* level / (float)scale;

                int batTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                int batVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                int batPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean batPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
                int batStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int batHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,-1);
                String batTech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);


                //displaying:
                batteryLevel.setText(batteryPct+"%");
                batteryTemperature.setText("" + batTemp);
                batteryVoltage.setText(batVoltage+"mV");
                batteryStatus.setText(getBatStatus(batStatus));
                batteryHealth.setText(getBatHealth(batHealth));

                if (batPlugged == BatteryManager.BATTERY_PLUGGED_AC)
                    batteryPlugged.setText("AC Connected");
                else if (batPlugged == BatteryManager.BATTERY_PLUGGED_USB)
                    batteryPlugged.setText("USB Connected");
                else if(batPlugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                    batteryPlugged.setText("Wireless Charger");
                }
                else{
                    batteryPlugged.setText("None");
                }
                if (batPresent)
                    batteryPresent.setText("Battery Present");
                else {
                    batteryPresent.setText("Battery Is Missing");
                }
                batteryTechnology.setText(batTech);
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryUpdates, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryUpdates);
    }


    private String getBatStatus(int i) {

        switch (i) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not Charging";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                return "Unknown";
            case -1:
                return "Unknown";
        }
        return "error";
    }

    private String getBatHealth(int value) {
        switch (value) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Cold";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheated";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return "Unknown";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Unspecified Failure";
        }
        return "Uknown";
    }


}
