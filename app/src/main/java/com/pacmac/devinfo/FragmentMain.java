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

    private void updateShareIntent() {

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
        setShareIntent(createShareIntent(sb));
    }

    // SHARE CPU INFO VIA ACTION_SEND
    private ShareActionProvider mShareActionProvider;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        updateShareIntent();
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent(StringBuilder sb) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, Build.MODEL + "\t-\t"
                + getResources().getString(R.string.title_activity_main_info));
        return shareIntent;
    }

}
