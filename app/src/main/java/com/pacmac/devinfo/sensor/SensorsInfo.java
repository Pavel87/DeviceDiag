package com.pacmac.devinfo.sensor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;

import com.pacmac.devinfo.R;


/**
 * Created by pacmac on 7/1/2015.
 */
public class SensorsInfo extends AppCompatActivity implements SensorListFragment.OnFragmentInteractionListener {

    private static final String FRAGMENT = "fragment_sensor_detail";
    private static final String SENSOR_TYPE = "sensor_type";

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public static final int TYPE_TILT_DETECTOR = 22;
    public static final int TYPE_WAKE_GESTURE = 23;
    public static final int TYPE_GLANCE_GESTURE = 24;
    public static final int TYPE_PICK_UP_GESTURE = 25;
    public static final int TYPE_WRIST_TILT_GESTURE = 26;
    public static final int TYPE_DEVICE_ORIENTATION = 27;
    public static final int TYPE_DYNAMIC_SENSOR_META = 32;
    // QTI sensors
    public static final int TYPE_BASIC_GESTURES = 33171000;
    public static final int TYPE_TAP = 33171001;
    public static final int TYPE_FACING = 33171002;
    public static final int TYPE_TILT = 33171003;
    public static final int TYPE_ABSOLUTE_MOTION_DETECTOR = 33171006;
    public static final int TYPE_RELATIVE_MOTION_DETECTOR = 33171007;
    public static final int TYPE_PEDOMETER = 33171009;
    public static final int TYPE_PEDESTRIAN_ACTIVITY_MONITOR = 33171010;
    public static final int TYPE_MOTION_ACCEL = 33171011;
    public static final int TYPE_COARSE_MOTION_CLASSIFIER = 33171012;
    // Cywee
    public static final int TYPE_CYWEE_HAND_UP = 33171016;
    public static final int TYPE_CYWEE_PICKUP = 33171018;
    public static final int TYPE_CYWEE_FLIP = 33171019;
    // Google
    public static final int TYPE_GOOGLE_TEMPERATURE_BOSH = 65536;
    public static final int TYPE_GOOGLE_SENSORS_SYNC = 65537;
    public static final int TYPE_GOOGLE_DOUBLE_TWIST = 65538;
    public static final int TYPE_GOOGLE_DOUBLE_TAP = 65539;

    private boolean isDetailFragment = false;
    private int sensorType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_base);


        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(FRAGMENT, false)) {
//                Recover the last sensor detail before reconfiguration
                Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.sensorFragLayout, fragment, FRAGMENT);
                fragmentTransaction.commit();
                isDetailFragment = true;
            }
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.sensorFragLayout, new SensorListFragment());
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onFragmentInteraction(int sensorType) {
        Log.d("TAG", "sensor type: " + sensorType);
        showSensorDetailFrag(sensorType);
        return;
    }

    @Override
    protected void onDestroy() {
        fragmentManager = null;
        fragmentTransaction = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            isDetailFragment = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FRAGMENT, isDetailFragment);
        outState.putInt(SENSOR_TYPE, sensorType);
        super.onSaveInstanceState(outState);
    }

    private void showSensorDetailFrag(int sensorType) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sensorFragLayout, SensorDetailFrag.newInstance(sensorType), FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        isDetailFragment = true;
        this.sensorType = sensorType;
    }
}
