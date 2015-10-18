package com.pacmac.devicediag;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by pacmac on 7/14/2015.
 */


public class SensorAdapter extends ArrayAdapter<Sensor> {

    private Context context;
    private String name;

    public SensorAdapter(Context context, List<Sensor> sensorType) {
        super(context, 0, sensorType);
        this.context = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Sensor getItem(int i) {
        return super.getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {

        Sensor currentSensor = getItem(index);
        getSensor(currentSensor);


        Viewhold viewHolder;
        if (view == null) {
            viewHolder = new Viewhold();

            view = LayoutInflater.from(context).inflate(R.layout.sensor_item, null);
            viewHolder.id = (TextView) view.findViewById(R.id.idSensor);
            viewHolder.name = (TextView) view.findViewById(R.id.nameSensor);
            viewHolder.manufacturer = (TextView) view.findViewById(R.id.manSensor);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Viewhold) view.getTag();
        }

        viewHolder.id.setText(index+1 + "");
        viewHolder.name.setText(name);
        viewHolder.manufacturer.setText(currentSensor.getVendor());
        return view;
    }


    public static class Viewhold {

        TextView id;
        TextView name;
        TextView manufacturer;

    }

    public void getSensor(Sensor sensor) {

       name= getName(sensor.getType());

    }


    public String getName(int type) {

        switch(type){
            case 1: return "Acccelerometer";
            case 2: return "Magnetic Field";
            case 3: return "Orientation";
            case 4: return "Gyroscope";
            case 5: return "Light";
            case 6: return "Pressure";
            case 7: return "Temperature";
            case 8: return "Proximity";
            case 9: return "Gravity";
            case 10: return "Linear Acceleration";
            case 11: return "Rotation Vector";
            case 12: return "Relative Humidity";
            case 13: return "Ambient Temperature";
            case 14: return "Mag. Field Uncalibrated";
            case 15: return "Game Rotation Vector";
            case 16: return "Gyroscope Uncalibrated";
            case 17: return "Significant Motion Trigger";
            case 18: return "Step Detector";
            case 19: return "Step Counter";
            case 20: return "Geomagnetic Rotation";
            case 21: return "Heart Rate";
            case 22: return "Step Counter";
        }

        return "unknown";
    }

}
