package com.pacmac.devinfo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by pacmac on 7/1/2015.
 */
public class SensorsInfo extends AppCompatActivity implements SensorListFragment.OnFragmentInteractionListener{

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_base);



        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.sensorFragLayout, new SensorListFragment());
        //fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(int sensorType) {

        Log.d("TAG", "sensor type: " + sensorType);

        showSensorDetailFrag(sensorType);
        return;
//        switch (sensorType){
//            case 991:
//                showSensorDetailFrag(sensorType);
//                break;
//            case 994:
//                showSensorDetailFrag(sensorType);
//                break;
//            default:
//                Toast.makeText(getApplicationContext(), "... Sensor detail will be in next update.", Toast.LENGTH_SHORT).show();
//                break;
//        }

    }

    @Override
    protected void onDestroy() {
        fragmentManager = null;
        fragmentTransaction = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }

    }

    private void showSensorDetailFrag(int sensorType){
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sensorFragLayout, SensorDetailFrag.newInstance(sensorType));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
