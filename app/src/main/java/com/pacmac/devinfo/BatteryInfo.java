package com.pacmac.devinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
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
                batteryVoltage.setText(batVoltage + "mV");
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
                updateShareIntent();
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryUpdates, intentFilter);
        updateShareIntent();
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
    private ShareActionProvider mShareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_share, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        updateShareIntent();
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent(StringBuilder sb) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, Build.MODEL + "\t-\t"
                + getResources().getString(R.string.title_activity_battery_info));
        return shareIntent;
    }


    private void updateShareIntent() {

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
        setShareIntent(createShareIntent(sb));
    }


}
