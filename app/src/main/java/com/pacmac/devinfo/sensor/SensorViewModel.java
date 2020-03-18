package com.pacmac.devinfo.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;

import java.util.ArrayList;
import java.util.List;

public class SensorViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "sensor_info";

    private MutableLiveData<List<Sensor>> sensorList = new MutableLiveData<>();


    public MutableLiveData<List<Sensor>> getSensorList(Context context) {
        loadSensorInfo(context);
        return sensorList;
    }

    public List<UIObject> getSensorListForExport() {

        if (sensorList.getValue() != null) {
            List<UIObject> list = new ArrayList<>();
            for (Sensor sensor : sensorList.getValue()) {
                list.add(new UIObject(sensor.getName(), sensor.getVendor()));
            }
            return list;
        }
        return null;
    }

    private void loadSensorInfo(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            sensorList.postValue(deviceSensors);
        }
    }
}
