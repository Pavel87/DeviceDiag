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
        fragmentTransaction.add(R.id.sensorFragLayout, SensorDetailFrag.newInstance(sensorType));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
