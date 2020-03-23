package com.pacmac.devinfo.sensor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;

import com.pacmac.devinfo.R;


/**
 * Created by pacmac on 7/1/2015.
 */
public class SensorsInfo extends AppCompatActivity implements SensorListFragment.OnFragmentInteractionListener {

    private static final String FRAGMENT = "fragment_sensor_detail";

    SensorViewModel viewModel;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_base);

        viewModel = new ViewModelProvider(this).get(SensorViewModel.class);

        fragmentManager = getSupportFragmentManager();

        if (viewModel.isDetailFragment()) {
            Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.sensorFragLayout, fragment, FRAGMENT);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.sensorFragLayout, new SensorListFragment());
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
        viewModel.setDetailFragment(false);
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void showSensorDetailFrag(int sensorType) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sensorFragLayout, SensorDetailFrag.newInstance(sensorType), FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        viewModel.setDetailFragment(true);
    }
}
