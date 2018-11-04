package com.pacmac.devinfo;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by pacmac on 7/14/2015.
 */


public class SensorAdapter extends ArrayAdapter<Sensor> {

    private Context context;
    private String name;
    private List<Sensor> sensors;

    public SensorAdapter(Context context, List<Sensor> sensors) {
        super(context, 0, sensors);
        this.context = context;
        this.sensors = sensors;
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

        Viewhold viewHolder;
        if (view == null) {
            viewHolder = new Viewhold();

            view = LayoutInflater.from(context).inflate(R.layout.sensor_item, null);
            viewHolder.id = view.findViewById(R.id.idSensor);
            viewHolder.name = view.findViewById(R.id.nameSensor);
            viewHolder.manufacturer = view.findViewById(R.id.manSensor);
            view.setTag(viewHolder);
        } else {
            viewHolder = (Viewhold) view.getTag();
        }

        viewHolder.id.setText(index+1 + "");
        viewHolder.name.setText(currentSensor.getName());
        viewHolder.manufacturer.setText(currentSensor.getVendor());
        return view;
    }

    public static class Viewhold {

        TextView id;
        TextView name;
        TextView manufacturer;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }
}
