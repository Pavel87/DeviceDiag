package com.pacmac.devinfo.main;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pacmac.devinfo.AboutActivity;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.battery.BatteryInfoKt;
import com.pacmac.devinfo.camera.CameraInfo;
import com.pacmac.devinfo.cellular.CellularInfo;
import com.pacmac.devinfo.cpu.CPUInfoKt;
import com.pacmac.devinfo.display.DisplayInfoKt;
import com.pacmac.devinfo.gps.ui.GPSInfoKt;
import com.pacmac.devinfo.sensor.SensorInfoKt;
import com.pacmac.devinfo.storage.StorageInfoKt;
import com.pacmac.devinfo.utils.Utility;
import com.pacmac.devinfo.wifi.NetworkInfoKt;

/**
 * Created by pacmac on 5/26/2015.
 */
public class FragmentDashboard extends Fragment {

    private GridView gridView;

    boolean isLocPermissionEnabled = true;
    boolean isPhonePermissionEnabled = true;
    boolean isPhoneNumberPermissionEnabled = true;
    boolean isStoragePermissionEnabled = true;
    boolean isCameraPermissionEnabled = true;

    private Integer[] mThumbIds = {
            R.drawable.cpuimg, R.drawable.ramimg,
            R.drawable.batimg, R.drawable.camimg,
            R.drawable.gpsimg, R.drawable.simimg,
            R.drawable.sensorimg, R.drawable.displayimg,
            R.drawable.wifiimg, R.drawable.aboutimg
    };

    public FragmentDashboard() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Utility.showBannerAdView(getActivity(), view, getContext(), R.string.banner_id_2);

        int gridColumns = 2;
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            gridColumns = 3;
        }

        gridView = view.findViewById(R.id.gridViewMain);
        gridView.setNumColumns(gridColumns);
        gridView.setAdapter(new DetailAdapter(getActivity().getApplicationContext(), mThumbIds));
        gridView.setOnItemClickListener((adapterView, gridViewItem, position, l) -> {
            switch (position) {
                case 0:
                    Intent i = new Intent(getActivity(), CPUInfoKt.class);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(i);
                    break;
                case 1:
                    isStoragePermissionEnabled = Utility.checkPermission(getContext(), Utility.STORAGE_PERMISSION);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!isStoragePermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(),
                                    getResources().getString(R.string.storage_permission_msg), new String[]{Utility.STORAGE_PERMISSION});
                            return;
                        }
                    }
                    i = new Intent(getActivity(), StorageInfoKt.class);
                        startActivity(i);
                    break;
                case 2:
                    i = new Intent(getActivity(), BatteryInfoKt.class);
                        startActivity(i);
                    break;
                case 3:
                    isCameraPermissionEnabled = Utility.checkPermission(getContext(), Utility.CAMERA_PERMISSION);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!isCameraPermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(),
                                    getResources().getString(R.string.cam_permission_msg), new String[]{Utility.CAMERA_PERMISSION});
                            return;
                        }
                    }

                    i = new Intent(getActivity(), CameraInfo.class);
                        startActivity(i);
                    break;
                case 4:
                    if (Utility.hasGPS(getContext())) {
                        isLocPermissionEnabled = Utility.checkPermission(getContext(), Utility.ACCESS_FINE_LOCATION);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                            if (!isLocPermissionEnabled) {
                                Utility.displayExplanationForPermission(getActivity(), getResources().getString(R.string.location_permission_msg), Utility.getLocationPermissions());
                                return;
                            }
                        }
                        i = new Intent(getActivity(), GPSInfoKt.class);
                            startActivity(i);
                    } else {
                        Toast.makeText(getContext(), R.string.gps_not_available_in_device, Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:
                    isLocPermissionEnabled = Utility.checkPermission(getContext(), Utility.ACCESS_FINE_LOCATION);
                    isPhonePermissionEnabled = Utility.checkPermission(getContext(), Utility.PHONE_PERMISSION);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        isPhoneNumberPermissionEnabled = Utility.checkPermission(getContext(), Utility.PHONE_NUMBER_PERMISSION);
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!isPhonePermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(), getResources().getString(R.string.phone_permission_msg), new String[]{Utility.PHONE_PERMISSION});
                            return;
                        }
                        if (!isLocPermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(), getResources().getString(R.string.location_permission_msg), Utility.getLocationPermissions());
                            return;
                        }

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !isPhoneNumberPermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(), getResources().getString(R.string.phone_number_permission_msg), new String[]{Utility.PHONE_NUMBER_PERMISSION});
                            return;
                        }
                    }
                    i = new Intent(getActivity(), CellularInfo.class);
                    startActivity(i);
                    break;
                case 6:
                    i = new Intent(getActivity(), SensorInfoKt.class);
                        startActivity(i);
                    break;

                case 7:
                    i = new Intent(getActivity(), DisplayInfoKt.class);
                        startActivity(i);
                    break;
                case 8:
                    isLocPermissionEnabled = Utility.checkPermission(getContext(), Utility.ACCESS_FINE_LOCATION);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!isLocPermissionEnabled) {
                            Utility.displayExplanationForPermission(getActivity(), getResources().getString(R.string.location_permission_msg), Utility.getLocationPermissions());
                            return;
                        }
                    }
                    i = new Intent(getActivity(), NetworkInfoKt.class);
                        startActivity(i);
                    break;
                case 9:
                    i = new Intent(getActivity(), AboutActivity.class);
                        startActivity(i);
                    break;
            }
        });
    }
}
