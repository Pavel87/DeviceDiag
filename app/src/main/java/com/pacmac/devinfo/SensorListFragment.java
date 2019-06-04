package com.pacmac.devinfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
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

    Handler shareIntentHandler;
    Runnable runnableShareIntent;

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

    @Override
    public void onResume() {
        super.onResume();
        runnableShareIntent = new Runnable() {
            @Override
            public void run() {
                updateShareIntent();
            }
        };
        shareIntentHandler = new Handler();
        shareIntentHandler.postDelayed(runnableShareIntent, 700);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (shareIntentHandler != null) {
                shareIntentHandler.removeCallbacks(runnableShareIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateShareIntent() {

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
        Utility.setShareIntent(mShareActionProvider, Utility.createShareIntent(getResources().getString(R.string.title_activity_sensor_list), sb));
    }

    // SHARE CPU INFO VIA ACTION_SEND
    private ShareActionProvider mShareActionProvider;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        updateShareIntent();
    }
}
