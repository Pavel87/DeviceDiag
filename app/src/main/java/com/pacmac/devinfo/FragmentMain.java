package com.pacmac.devinfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    Button buildPropsButton;

    public FragmentMain() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        buildPropsButton = rootView.findViewById(R.id.buildPropsBtn);

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

        buildPropsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BuildPropertiesActivity.class));
            }
        });
    }

    private String collectMainInfoForExport() {

        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_main_info));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");

        //body
        sb.append("Serial Number:\t\t" + serialNumber.getText().toString());
        sb.append("\n");
        sb.append("Manufacturer:\t\t" + manufacturer.getText().toString());
        sb.append("\n");
        sb.append("Hardware Code Name:\t\t" + hardWare.getText().toString());
        sb.append("\n");
        sb.append("Build Number:\t\t" + buildNumber.getText().toString());
        sb.append("\n");
        sb.append("Bootloader:\t\t" + bootloader.getText().toString());
        sb.append("\n");
        sb.append("Android version:\t\tv" + androidVer.getText().toString());
        sb.append("\n\n");

        sb.append(getResources().getString(R.string.shareTextTitle1));
        return sb.toString();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            Utility.exporData(getActivity(), getResources().getString(R.string.title_activity_main_info), collectMainInfoForExport());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
