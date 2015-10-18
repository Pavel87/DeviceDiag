package com.pacmac.devicediag;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by pacmac on 10/15/2015.
 */
public class SensorDetailFrag extends Fragment {

    private Sensor sensor;
    private SensorManager mSensorManager;
    float[] newDefault = {0, 0, 0};
    float[] values;

    // SENSOR EVENT LISTENER
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //  Log.d("TAG", sensorEvent.toString());
            values = sensorEvent.values;
            switch (sensor.getType()) {
                case 1:
                    calculateAcc();
                    break;
                case 2:
                    break; // return "Magnetic Field";
                case 3:
                    break; // return "Orientation";
                case 4:
                    break; // return "Gyroscope";
                case 5:
                    break; // return "Light";
                case 6:
                    break; // return "Pressure";
                case 7:
                    break; // return "Temperature";
                case 8:
                    break; // return "Proximity";
                case 9:
                    break; // return "Gravity";
                case 10:
                    break; // return "Linear Acceleration";
                case 11:
                    break; // return "Rotation Vector";
                case 12:
                    break; // return "Relative Humidity";
                case 13:
                    break; // return "Ambient Temperature";
                case 14:
                    break; // return "Mag. Field Uncalibrated";
                case 15:
                    break; // return "Game Rotation Vector";
                case 16:
                    break; // return "Gyroscope Uncalibrated";
                case 17:
                    break; // return "Significant Motion Trigger";
                case 18:
                    break; // return "Step Detector";
                case 19:
                    break; // return "Step Counter";
                case 20:
                    break; // return "Geomagnetic Rotation";
                case 21:
                    break; // return "Heart Rate";
                case 22:
                    break; // return "Step Counter";

            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            Log.d("TAG2", "accuracy: " + i);
        }
    };
//  END LISTENER


    // CREATE STATIC FRAGMENT INSTANCE
    public static SensorDetailFrag newInstance(int sensorType) {
        SensorDetailFrag f = new SensorDetailFrag();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("type", sensorType);
        f.setArguments(args);

        return f;
    }

    public int getSensorType() {
        return getArguments().getInt("type", 0);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.sensor_detail, null);


        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(getSensorType());

        TextView type = (TextView) v.findViewById(R.id.sensorType);
        TextView manufacturer = (TextView) v.findViewById(R.id.manufactureSensor);

        type.setText(getName(sensor.getType()));
        manufacturer.setText(sensor.getVendor());

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }


    public void calculateAcc() {

        float[] delta = {0, 0, 0};
        float[] previousVal = {0, 0, 0};

        TextView xValues = (TextView) getView().findViewById(R.id.xValues);
        TextView yValues = (TextView) getView().findViewById(R.id.yValues);
        TextView zValues = (TextView) getView().findViewById(R.id.zValues);
        Button setDefaultAcc = (Button) getView().findViewById(R.id.setDefaultAcc);
        Button resDefaultAcc = (Button) getView().findViewById(R.id.resetDefaultAcc);


        setDefaultAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDefault[0] = values[0];
                newDefault[1] = values[1];
                newDefault[2] = values[2];
            }
        });

        resDefaultAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDefault[0] = 0;
                newDefault[1] = 0;
                newDefault[2] = 0;
            }
        });

        float noise = (float) 0.4;
        delta[0] = Math.abs(previousVal[0] - values[0]);
        delta[1] = Math.abs(previousVal[1] - values[1]);
        delta[2] = Math.abs(previousVal[2] - values[2]);

//Log.d("TAG",newDefault[0] + " " +newDefault[1]+ " " + newDefault[2] );

        if (delta[0] > noise)
            xValues.setText(String.format("%.1f", values[0] - newDefault[0]));
        if (delta[1] > noise)
            yValues.setText(String.format("%.1f", values[1] - newDefault[1]));
        if (delta[2] > noise)
            zValues.setText(String.format("%.1f", values[2] - newDefault[2]));

        previousVal[0] = values[0];
        previousVal[1] = values[1];
        previousVal[2] = values[2];
    }
    // END ACC


    public String getName(int type) {

        switch (type) {
            case 1:
                return "Acccelerometer";
            case 2:
                return "Magnetic Field";
            case 3:
                return "Orientation";
            case 4:
                return "Gyroscope";
            case 5:
                return "Light";
            case 6:
                return "Pressure";
            case 7:
                return "Temperature";
            case 8:
                return "Proximity";
            case 9:
                return "Gravity";
            case 10:
                return "Linear Acceleration";
            case 11:
                return "Rotation Vector";
            case 12:
                return "Relative Humidity";
            case 13:
                return "Ambient Temperature";
            case 14:
                return "Mag. Field Uncalibrated";
            case 15:
                return "Game Rotation Vector";
            case 16:
                return "Gyroscope Uncalibrated";
            case 17:
                return "Significant Motion Trigger";
            case 18:
                return "Step Detector";
            case 19:
                return "Step Counter";
            case 20:
                return "Geomagnetic Rotation";
            case 21:
                return "Heart Rate";
            case 22:
                return "Step Counter";
        }

        return "unknown";
    }

}
