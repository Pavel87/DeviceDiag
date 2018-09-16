package com.pacmac.devinfo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by pacmac on 5/26/2015.
 */
public class FragmentMain extends Fragment {

    TextView modelName;
    TextView serialNumber;
    TextView manufacturer;
    TextView hardWare;
    TextView buildNumber;
    TextView androidVer;
    TextView bootloader;
    TextView radio;

    public FragmentMain() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_diag_main, container, false);

        modelName = (TextView) rootView.findViewById(R.id.modelName);
        serialNumber = (TextView) rootView.findViewById(R.id.serialNumber);
        manufacturer = (TextView) rootView.findViewById(R.id.manufacturer);
        hardWare = (TextView) rootView.findViewById(R.id.hardware);
        buildNumber = (TextView) rootView.findViewById(R.id.buildNumber);
        androidVer = (TextView) rootView.findViewById(R.id.androidVer);
        bootloader = rootView.findViewById(R.id.bootloader);
        radio = rootView.findViewById(R.id.radio);

        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String radioFW = Build.getRadioVersion();
        if (radioFW != null) {
            radio.setText(radioFW);
        } else {
            radio.setText("Not Available");
        }


        modelName.setText(Build.MODEL);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                serialNumber.setText(Build.getSerial());
            }
        } else {
            serialNumber.setText(Build.SERIAL);
        }

        manufacturer.setText(Build.MANUFACTURER);

        hardWare.setText(Build.HARDWARE.toUpperCase() + " " + Build.BOARD);
        buildNumber.setText(Build.DISPLAY);
        bootloader.setText(Build.BOOTLOADER);
        androidVer.setText(Build.VERSION.RELEASE + "  API:" + Build.VERSION.SDK_INT);
    }


}
