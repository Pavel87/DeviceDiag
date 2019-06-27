package com.pacmac.devinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class BatteryInfo extends AppCompatActivity {

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
    private TextView chargeCounter;
    private TextView currentAvg;
    private TextView currentActual;
    private TextView batteryEnergyCounter;
    private TextView timeToFull;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_info);
        batteryLevel = findViewById(R.id.level);
        batteryPlugged = findViewById(R.id.plugged);
        batteryPresent = findViewById(R.id.present);
        batteryTechnology = findViewById(R.id.technology);
        batteryVoltage = findViewById(R.id.voltage);
        batteryTemperature = findViewById(R.id.temp);
        batteryStatus = findViewById(R.id.batStatus);
        batteryHealth = findViewById(R.id.health);
        chargeCounter = findViewById(R.id.chargeCounter);
        currentAvg = findViewById(R.id.currentAvg);
        currentActual = findViewById(R.id.currentActual);
        batteryEnergyCounter = findViewById(R.id.batteryEnergyCounter);
        timeToFull = findViewById(R.id.timeToFull);


        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryUpdates = new BroadcastReceiver() {
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


                //displaying:
                batteryLevel.setText(batteryPct + "%");
                batteryTemperature.setText(batTemp + "°C");
                batteryVoltage.setText(batVoltage + " mV");
                batteryStatus.setText(getBatStatus(batStatus));
                batteryHealth.setText(getBatHealth(batHealth));

                if (batPlugged == BatteryManager.BATTERY_PLUGGED_AC)
                    batteryPlugged.setText("AC Connected");
                else if (batPlugged == BatteryManager.BATTERY_PLUGGED_USB)
                    batteryPlugged.setText("USB Connected");
                else if (batPlugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                    batteryPlugged.setText("Wireless Charger");
                } else {
                    batteryPlugged.setText("None");
                }
                if (batPresent)
                    batteryPresent.setText("Battery Present");
                else {
                    batteryPresent.setText("Battery Is Missing");
                }
                batteryTechnology.setText(batTech);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    findViewById(R.id.viewForAPI21).setVisibility(View.VISIBLE);
                    BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);

                    // Battery capacity in microampere-hours, as an integer.
                    int chargeCounterInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                    chargeCounter.setText(String.valueOf(chargeCounterInt/1000) + " uAH");

                    // Average battery current in microamperes, as an integer. Positive values indicate net current
                    // entering the battery from a charge source, negative values indicate net current discharging from
                    // the battery. The time period over which the average is computed may depend on the fuel gauge hardware and its configuration.
                    int avgCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                    if (avgCurrentInt != Integer.MAX_VALUE) {
                    currentAvg.setText(String.valueOf(avgCurrentInt) + " uA");
                    } else {

                    }

                    // Instantaneous battery current in microamperes, as an integer. Positive values indicate
                    // net current entering the battery from a charge source, negative values indicate net current discharging from the battery.
                    int actualCurrentInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                    currentActual.setText(String.valueOf(actualCurrentInt) + " uA");

                    // Battery remaining energy in nanowatt-hours, as a long integer.
                    long remainingEnergyInt = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                    batteryEnergyCounter.setText((remainingEnergyInt >= 0) ? String.valueOf(remainingEnergyInt) + " nWh" : getResources().getString(R.string.not_available_info));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        findViewById(R.id.timeToFullView).setVisibility(View.VISIBLE);
                        // Compute an approximation for how much time (in milliseconds) remains until the battery
                        // is fully charged. Returns -1 if no time can be computed: either there is not enough current
                        // data to make a decision or the battery is currently discharging.
                        long timeToFullLong = batteryManager.computeChargeTimeRemaining();
                        timeToFull.setText((timeToFullLong >= 0) ? timeToFullLong + " ms" : getResources().getString(R.string.not_available_info));
                    }
                }
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


    // SHARE CPU INFO VIA ACTION_SEND
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            Utility.exporData(BatteryInfo.this, getResources().getString(R.string.title_activity_battery_info), updateExportMessage());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private String updateExportMessage() {

        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_battery_info));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");

        //body
        sb.append("Battery Level:\t\t" + batteryLevel.getText().toString());
        sb.append("\n");
        sb.append("Battery Voltage:\t\t" + batteryVoltage.getText().toString());
        sb.append("\n");
        sb.append("Battery Temperature:\t\t" + batteryTemperature.getText().toString());
        sb.append("\n");
        sb.append("Battery Present:\t\t" + batteryPresent.getText().toString());
        sb.append("\n");
        sb.append("Power Source:\t\t" + batteryPlugged.getText().toString());
        sb.append("\n");
        sb.append("Battery Status:\t\t" + batteryStatus.getText().toString());
        sb.append("\n");
        sb.append("Battery Health:\t\t" + batteryHealth.getText().toString());
        sb.append("\n");
        sb.append("Battery Technology:\t\t" + batteryTechnology.getText().toString());
        sb.append("\n\n");

        sb.append(getResources().getString(R.string.shareTextTitle1));
        return sb.toString();
    }


}
