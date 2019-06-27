package com.pacmac.devinfo;

import android.hardware.Sensor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pacmac on 7/14/2015.
 */


public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.MyViewHolder> {

    private List<Sensor> sensors;
    View.OnClickListener onClickListener;

    public SensorAdapter(View.OnClickListener onClickListener, List<Sensor> sensors) {
        this.sensors = sensors;
        this.onClickListener = onClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView id;
        TextView name;
        TextView manufacturer;


        public MyViewHolder(View view, View.OnClickListener onClickListener) {
            super(view);
            view.setTag(this);
            id = view.findViewById(R.id.idSensor);
            name = view.findViewById(R.id.nameSensor);
            manufacturer = view.findViewById(R.id.manSensor);
            view.setOnClickListener(onClickListener);
        }
    }

    @Override
    public SensorAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_item, parent, false);
        SensorAdapter.MyViewHolder vh = new SensorAdapter.MyViewHolder(v, this.onClickListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SensorAdapter.MyViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.id.setText((position + 1) + "");
        viewHolder.name.setText(sensors.get(position).getName());
        viewHolder.manufacturer.setText(sensors.get(position).getVendor());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (sensors == null) {
            return 0;
        }
        return sensors.size();
    }

    public Sensor getItem(int position) {
        return sensors.get(position);
    }

    public List<Sensor> getSensors(){
        return sensors;
    }
}
