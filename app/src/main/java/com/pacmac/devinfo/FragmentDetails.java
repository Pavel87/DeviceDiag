package com.pacmac.devinfo;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by pacmac on 5/26/2015.
 */
public class FragmentDetails extends Fragment {

    private GridView gridView;

    boolean isLocPermissionEnabled = true;
    boolean isPhonePermissionEnabled = true;

    private Integer[] mThumbIds = {
            R.drawable.cpuimg, R.drawable.ramimg,
            R.drawable.batimg, R.drawable.camimg,
            R.drawable.gpsimg, R.drawable.simimg,
            R.drawable.sensorimg, R.drawable.displayimg,
            R.drawable.wifiimg, R.drawable.aboutimg
    };

    public FragmentDetails() {
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            AdView mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("430BFEE479F599FBF9E3BD6716680F8C").build();
            mAdView.loadAd(adRequest);
        }

        gridView = view.findViewById(R.id.gridViewMain);
        gridView.setAdapter(new DetailAdapter(getActivity().getApplicationContext(), mThumbIds));
        gridView.setOnItemClickListener((adapterView, gridViewItem, position, l) -> {
            switch (position) {
                case 0:
                    Intent i = new Intent(getActivity(), CPUInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 1:
                    i = new Intent(getActivity(), MemoryInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 2:
                    i = new Intent(getActivity(), BatteryInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 3:
                    i = new Intent(getActivity(), CameraInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 4:
                    if (Utility.hasGPS(getContext())) {
                        i = new Intent(getActivity(), GPSInfo.class);
                        if (i.resolveActivity(getActivity().getPackageManager()) != null)
                            startActivity(i);
                    } else {
                        Toast.makeText(getContext(), "GPS is not available on this device.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:
                    isLocPermissionEnabled = Utility.checkPermission(getContext(), Utility.ACCESS_FINE_LOCATION);
                    isPhonePermissionEnabled = Utility.checkPermission(getContext(), Utility.PHONE_PERMISSION);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!isPhonePermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(), getResources().getString(R.string.phone_permission_msg), new String[]{Utility.PHONE_PERMISSION});
                            return;
                        }
                        if (!isLocPermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(), getResources().getString(R.string.location_permission_msg), Utility.getLocationPermissions());
                            return;
                        }
                    }
                    i = new Intent(getActivity(), SIMInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 6:
                    i = new Intent(getActivity(), SensorsInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;

                case 7:
                    i = new Intent(getActivity(), DisplayInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 8:
                    i = new Intent(getActivity(), NetworkInfo.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 9:
                    i = new Intent(getActivity(), AboutActivity.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_details, container, false);
    }
}
