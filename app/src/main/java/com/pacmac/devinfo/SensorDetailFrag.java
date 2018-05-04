package com.pacmac.devinfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sqrt;


/**
 * Created by pacmac on 10/15/2015.
 */
public class SensorDetailFrag extends Fragment {

    private Sensor sensor;
    private SensorManager mSensorManager;


    private static final float EPSILON = 0.05f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    float[] newDefault = {0, 0, 0};
    float[] values;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    // common views
    private TextView typeTxt = null;
    private TextView nameTxt = null;
    private TextView vendorTxt = null;
    private TextView powerTxt = null;
    private TextView maxRangeTxt = null;
    private TextView sensorReading = null;



    private TextView xValues = null;
    private TextView yValues = null;
    private TextView zValues = null;

    // SENSOR EVENT LISTENER
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
          //  values = sensorEvent.values;
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    //calculateAcc();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    break; // return "Magnetic Field";
                case Sensor.TYPE_ORIENTATION:
                    break; // return "Orientation";
                case Sensor.TYPE_GYROSCOPE:
                    calculateGyro(sensorEvent);
                    break; // return "Gyroscope";
                case Sensor.TYPE_LIGHT:
                    sensorReading.setText(String.format("%.2f " , sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    break;
                case Sensor.TYPE_PRESSURE:
                    sensorReading.setText(String.format("%.2f " , sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                break;
                case Sensor.TYPE_TEMPERATURE:
                    sensorReading.setText(String.format("%.2f " , sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    break;
                case Sensor.TYPE_PROXIMITY:
                    if(sensorEvent.values[0] == 0) {
                        sensorReading.setText("NEAR DETECTION");
                    } else if(sensorEvent.values[0] == sensorEvent.sensor.getMaximumRange()) {
                        sensorReading.setText("FAR DETECTION");
                    } else {
                        sensorReading.setText(String.format("%.2f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    break; // return "Rotation Vector";
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    break; // return "Relative Humidity";
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensorReading.setText(String.format("%.2f " , sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    break; // return "Mag. Field Uncalibrated";
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    break; // return "Game Rotation Vector";
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    break; // return "Gyroscope Uncalibrated";
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    break; // return "Significant Motion Trigger";
                case Sensor.TYPE_STEP_DETECTOR:
                    break; // return "Step Detector";
                case Sensor.TYPE_STEP_COUNTER:
                    break; // return "Step Counter";
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    break; // return "Geomagnetic Rotation";
                case Sensor.TYPE_HEART_RATE:
                    break; // return "Heart Rate";
                case Sensor.TYPE_POSE_6DOF:
                    break;
                case Sensor.TYPE_STATIONARY_DETECT:
                    break;
                case Sensor.TYPE_MOTION_DETECT:
                    break;
                case Sensor.TYPE_HEART_BEAT:
                    break;
                case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                    break;
                case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
           // Log.d("TAG2", "accuracy: " + i);
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

        View v = inflater.inflate(R.layout.sensor_detail_default, null);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(getSensorType());

        typeTxt = v.findViewById(R.id.sensorType);
        nameTxt = v.findViewById(R.id.sensorName);
        vendorTxt = v.findViewById(R.id.vendorName);
        powerTxt = v.findViewById(R.id.powerRequirements);
        maxRangeTxt = v.findViewById(R.id.maxRange);
        sensorReading = v.findViewById(R.id.sensorReading);

        String type = getName(sensor.getType());
        String name = sensor.getName();
        String vendor = sensor.getVendor();
        float power = sensor.getPower();
        float maxRange = sensor.getMaximumRange();

        typeTxt.setText(type);
        nameTxt.setText(name);
        vendorTxt.setText(vendor);
        powerTxt.setText(power + " mA");
        maxRangeTxt.setText(String.format("%.2f ", maxRange) + getUnits(sensor.getType()));
        sensorReading.setText("Waiting...");


//        xValues = (TextView) v.findViewById(R.id.xValues);
//        yValues = (TextView) v.findViewById(R.id.yValues);
//        zValues = (TextView) v.findViewById(R.id.zValues);
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
        mSensorManager.registerListener(sensorEventListener, sensor, 5*1000*1000);

    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }





    public void calculateAcc() {
        float[] delta = {0, 0, 0};
        float[] previousVal = {0, 0, 0};

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


    // GYRO START

    private void calculateGyro(SensorEvent event){

        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float) sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float) StrictMath.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;

      }


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

        return "Unknown";
    }

    public String getUnits(int type) {
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                //calculateAcc();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_ORIENTATION:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_LIGHT:
                return "lux";
            case Sensor.TYPE_PRESSURE:
                return "hPa";
            case Sensor.TYPE_TEMPERATURE:
                return "°C";
            case Sensor.TYPE_PROXIMITY:
                return "cm";
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_ROTATION_VECTOR:
            case Sensor.TYPE_RELATIVE_HUMIDITY:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "°C";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_SIGNIFICANT_MOTION:
            case Sensor.TYPE_STEP_DETECTOR:
            case Sensor.TYPE_STEP_COUNTER:
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
            case Sensor.TYPE_HEART_RATE:
            case Sensor.TYPE_POSE_6DOF:
            case Sensor.TYPE_STATIONARY_DETECT:
            case Sensor.TYPE_MOTION_DETECT:
            case Sensor.TYPE_HEART_BEAT:
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:


        }
        return "";
    }

}
