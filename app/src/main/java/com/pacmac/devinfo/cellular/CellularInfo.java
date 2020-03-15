package com.pacmac.devinfo.cellular;

import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pacmac.devinfo.R;

public class CellularInfo extends AppCompatActivity {


    private PSL psl;
    private TelephonyManager telephonyManager;
    private int pslListen = PhoneStateListener.LISTEN_NONE;

    private CellularViewModel cellularViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cellular_info);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        cellularViewModel = new ViewModelProvider(this).get(CellularViewModel.class);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        psl = new PSL(cellularViewModel, getApplicationContext());

        pslListen = pslListen
                | PhoneStateListener.LISTEN_DATA_ACTIVITY
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                | PhoneStateListener.LISTEN_CELL_INFO
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_ACTIVE_DATA_SUBSCRIPTION_ID_CHANGE
//                | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_SERVICE_STATE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        telephonyManager.listen(psl, pslListen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        telephonyManager.listen(psl, PhoneStateListener.LISTEN_NONE);
    }
}