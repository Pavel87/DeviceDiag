package com.pacmac.devinfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    float[] values;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    // common views
    private TextView nameTxt = null;
    private TextView vendorTxt = null;
    private TextView powerTxt = null;
    private TextView maxRangeTxt = null;
    private TextView sensorReading1 = null;
    private TextView sensorReading2 = null;
    private TextView sensorReading3 = null;

    private Handler handler = null;

    private int stepCounter = 0;

    // SENSOR EVENT LISTENER
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //  values = sensorEvent.values;
            switch (sensor.getType()) {

                case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                    if (sensorEvent.values[0] > 0) {
                        sensorReading2.setText("ON Body");
                    } else {
                        sensorReading2.setText("OFF Body");
                    }
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    sensorReading2.setText(String.valueOf("Step Detected"));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sensorReading2.setText(String.valueOf(""));
                        }
                    }, 2500);
                    break; // return "Step Detector";
                case Sensor.TYPE_STEP_COUNTER:
                case SensorsInfo.TYPE_PEDOMETER:
                    double steps = sensorEvent.values[0];
                    if(stepCounter == 3) {
                        stepCounter = 0;
                    }
                    if(stepCounter == 0) {
                        sensorReading1.setText(String.format("%.0f ", steps) + getUnits(sensorEvent.sensor.getType()) + (steps < 2.0 ? "" : "s"));
                    } else if(stepCounter == 1) {
                        sensorReading2.setText(String.format("%.0f ", steps) + getUnits(sensorEvent.sensor.getType()) + (steps < 2.0 ? "" : "s"));

                    } else {
                        sensorReading3.setText(String.format("%.0f ", steps) + getUnits(sensorEvent.sensor.getType()) + (steps < 2.0 ? "" : "s"));
                    }
                    stepCounter++;
                    break; // return "Step Counter";
                case Sensor.TYPE_ORIENTATION:
                    sensorReading1.setText(String.format("Azimuth: %.0f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    sensorReading2.setText(String.format("Pitch: %.2f ", sensorEvent.values[1]) + getUnits(sensorEvent.sensor.getType()));
                    sensorReading3.setText(String.format("Roll: %.2f ", sensorEvent.values[2]) + getUnits(sensorEvent.sensor.getType()));
                    break; // return "Orientation";
                case Sensor.TYPE_LIGHT:
                case Sensor.TYPE_PRESSURE:
                case Sensor.TYPE_TEMPERATURE:
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                case SensorsInfo.TYPE_GOOGLE_TEMPERATURE_BOSH:
                    sensorReading2.setText(String.format("%.2f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    break;
                case Sensor.TYPE_PROXIMITY:
                    if (sensorEvent.values[0] == 0) {
                        sensorReading2.setText("NEAR DETECTION");
                    } else if (sensorEvent.values[0] == sensorEvent.sensor.getMaximumRange()) {
                        sensorReading2.setText("FAR DETECTION");
                    } else {
                        sensorReading2.setText(String.format("%.2f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    }
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    break; // return "Significant Motion Trigger";
                case Sensor.TYPE_HEART_RATE:
                    sensorReading2.setText(String.format("%.2f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    break; // return "Heart Rate";
                case Sensor.TYPE_STATIONARY_DETECT:
                    sensorReading2.setText(String.format("%.2f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    break;
                case Sensor.TYPE_MOTION_DETECT:
                    sensorReading2.setText(String.format("%.2f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    break;
                case Sensor.TYPE_HEART_BEAT:
                    if (sensorEvent.values[0] > 0) {
                        sensorReading2.setText("Detected");
                    } else {
                        sensorReading2.setText("No Heart Beat");
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break; // return "Geomagnetic Rotation";
                case Sensor.TYPE_POSE_6DOF:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    sensorReading1.setText(String.format("X: %.0f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                    sensorReading2.setText(String.format("Y: %.0f ", sensorEvent.values[1]) + getUnits(sensorEvent.sensor.getType()));
                    sensorReading3.setText(String.format("Z: %.0f ", sensorEvent.values[2]) + getUnits(sensorEvent.sensor.getType()));
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break; // return "Game Rotation Vector";
                case Sensor.TYPE_GYROSCOPE:
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    sensorReading1.setText(String.format("X: %.1f ", sensorEvent.values[0]));
                    sensorReading2.setText(String.format("Y: %.1f ", sensorEvent.values[1]));
                    sensorReading3.setText(String.format("Z: %.1f ", sensorEvent.values[2]));
                    break;
                case SensorsInfo.TYPE_TILT_DETECTOR:
                case SensorsInfo.TYPE_WAKE_GESTURE:
                case SensorsInfo.TYPE_PICK_UP_GESTURE:
                case SensorsInfo.TYPE_GLANCE_GESTURE:
                case SensorsInfo.TYPE_WRIST_TILT_GESTURE:
                case SensorsInfo.TYPE_DEVICE_ORIENTATION:
                case SensorsInfo.TYPE_DYNAMIC_SENSOR_META:
                case SensorsInfo.TYPE_BASIC_GESTURES:
                case SensorsInfo.TYPE_TAP:
                case SensorsInfo.TYPE_FACING:
                case SensorsInfo.TYPE_TILT:
                case SensorsInfo.TYPE_ABSOLUTE_MOTION_DETECTOR:
                case SensorsInfo.TYPE_RELATIVE_MOTION_DETECTOR:
                case SensorsInfo.TYPE_PEDESTRIAN_ACTIVITY_MONITOR:
                case SensorsInfo.TYPE_CYWEE_HAND_UP:
                case SensorsInfo.TYPE_CYWEE_PICKUP:
                case SensorsInfo.TYPE_CYWEE_FLIP:
                case SensorsInfo.TYPE_GOOGLE_SENSORS_SYNC:
                case SensorsInfo.TYPE_GOOGLE_DOUBLE_TWIST:
                case SensorsInfo.TYPE_GOOGLE_DOUBLE_TAP:
                case SensorsInfo.TYPE_MOTION_ACCEL:
                case SensorsInfo.TYPE_COARSE_MOTION_CLASSIFIER:
                    sensorReading2.setText(String.format("%.2f ", sensorEvent.values[0]) + getUnits(sensorEvent.sensor.getType()));
                default:
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
        handler = new Handler();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(getSensorType());
        if (sensor == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sensor = mSensorManager.getDefaultSensor(getSensorType(), true);
        }

        nameTxt = v.findViewById(R.id.sensorName);
        vendorTxt = v.findViewById(R.id.vendorName);
        powerTxt = v.findViewById(R.id.powerRequirements);
        maxRangeTxt = v.findViewById(R.id.maxRange);
        sensorReading1 = v.findViewById(R.id.sensorReading1);
        sensorReading2 = v.findViewById(R.id.sensorReading2);
        sensorReading3 = v.findViewById(R.id.sensorReading3);

        if (sensor != null) {
            String name = sensor.getName();
            String vendor = sensor.getVendor();
            float power = sensor.getPower();
            float maxRange = sensor.getMaximumRange();

            nameTxt.setText(name);
            vendorTxt.setText(vendor);
            powerTxt.setText(power + " mA");
            maxRangeTxt.setText(String.format("%.2f ", maxRange) + getUnits(sensor.getType()));
            sensorReading2.setText("...");
        } else {
            nameTxt.setText("TYPE: " + getSensorType());
            vendorTxt.setText("Unknown");
            powerTxt.setText("Unknown");
            maxRangeTxt.setText(String.format(""));
            sensorReading2.setText("...");
        }
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
        if (sensor != null) {
            mSensorManager.registerListener(sensorEventListener, sensor, 5 * 1000 * 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensor != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
    }




    // GYRO START

    private void calculateGyro(SensorEvent event) {

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

    public String getUnits(int type) {
        switch (type) {
            case Sensor.TYPE_LIGHT:
                return "lux";
            case Sensor.TYPE_PRESSURE:
                return "hPa";
            case Sensor.TYPE_PROXIMITY:
                return "cm";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "%";
            case Sensor.TYPE_TEMPERATURE:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case SensorsInfo.TYPE_GOOGLE_TEMPERATURE_BOSH:
                return "°C";
            case Sensor.TYPE_HEART_RATE:
                return "bps";
            case Sensor.TYPE_STEP_COUNTER:
            case SensorsInfo.TYPE_PEDOMETER:
                return "step";
            case Sensor.TYPE_GRAVITY:
                return "m/s^2";
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "uT";

            case Sensor.TYPE_STEP_DETECTOR:
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_ROTATION_VECTOR:
            case Sensor.TYPE_ORIENTATION:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_SIGNIFICANT_MOTION:
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
            case Sensor.TYPE_POSE_6DOF:
            case Sensor.TYPE_STATIONARY_DETECT:
            case Sensor.TYPE_MOTION_DETECT:
            case Sensor.TYPE_HEART_BEAT:
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT: // no units
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
            case SensorsInfo.TYPE_TILT_DETECTOR:
            case SensorsInfo.TYPE_WAKE_GESTURE:
            case SensorsInfo.TYPE_PICK_UP_GESTURE:
            case SensorsInfo.TYPE_GLANCE_GESTURE:
            case SensorsInfo.TYPE_WRIST_TILT_GESTURE:
            case SensorsInfo.TYPE_DEVICE_ORIENTATION:
            case SensorsInfo.TYPE_DYNAMIC_SENSOR_META:
            case SensorsInfo.TYPE_BASIC_GESTURES:
            case SensorsInfo.TYPE_TAP:
            case SensorsInfo.TYPE_FACING:
            case SensorsInfo.TYPE_TILT:
            case SensorsInfo.TYPE_ABSOLUTE_MOTION_DETECTOR:
            case SensorsInfo.TYPE_RELATIVE_MOTION_DETECTOR:
            case SensorsInfo.TYPE_CYWEE_HAND_UP:
            case SensorsInfo.TYPE_CYWEE_PICKUP:
            case SensorsInfo.TYPE_CYWEE_FLIP:
            case SensorsInfo.TYPE_GOOGLE_SENSORS_SYNC:
            case SensorsInfo.TYPE_GOOGLE_DOUBLE_TWIST:
            case SensorsInfo.TYPE_GOOGLE_DOUBLE_TAP:
        }
        return "";
    }

}
