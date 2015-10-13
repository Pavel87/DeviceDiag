package com.pacmac.devicediag;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pacmac on 7/1/2015.
 */
public class SensorsInfo extends AppCompatActivity {

    private TextView sensorList;
    private ListView list;
    private SensorManager sensorManager;
    private SensorAdapter sensorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors_layout);
        //sensorList = (TextView) findViewById(R.id.sensorList);
        list = (ListView) findViewById(R.id.listSensors);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorAdapter = new SensorAdapter(getApplicationContext(),deviceSensors);
        list.setAdapter(sensorAdapter);

       // sensorList.setText("");
      //  for (int i = 0; i < deviceSensors.size(); i++) {

      //      sensorList.append(deviceSensors.get(i).getType() +" " + deviceSensors.get(i).getName()+" " +deviceSensors.get(i).getVendor()+"\n");
       // }

    }
}
