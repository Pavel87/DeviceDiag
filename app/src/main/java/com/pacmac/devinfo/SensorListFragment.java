package com.pacmac.devinfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class SensorListFragment extends Fragment {

    private ListView list;
    private SensorManager sensorManager;
    private SensorAdapter sensorAdapter;
    private OnFragmentInteractionListener mListener;


    public SensorListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorAdapter = new SensorAdapter(getActivity().getApplicationContext(), deviceSensors);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.sensors_layout, container, false);
        list = v.findViewById(R.id.listSensors);
        list.setAdapter(sensorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sensor currentSensor = (Sensor) adapterView.getItemAtPosition(i);
                mListener.onFragmentInteraction(currentSensor.getType());
            }
        });
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int sensorType);
    }

    private String collectSensorInfoForExport() {

        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_sensor_list));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");

        //body
        for (int i = 0; i < sensorAdapter.getSensors().size(); i++) {
            sb.append(i + 1);
            sb.append(", ");
            sb.append(sensorAdapter.getSensors().get(i).getVendor());
            sb.append(", ");
            sb.append(sensorAdapter.getSensors().get(i).getName().toUpperCase());
            sb.append(", ");
            sb.append(sensorAdapter.getSensors().get(i).getPower()).append("mA");
            sb.append("\n");
        }
        sb.append("\n\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        return sb.toString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            Utility.exporData(getActivity(), getResources().getString(R.string.title_activity_sensor_list), collectSensorInfoForExport());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
