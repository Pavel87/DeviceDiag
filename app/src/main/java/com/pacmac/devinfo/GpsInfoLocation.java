package com.pacmac.devinfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


public class GpsInfoLocation extends Fragment {

    private TextView gpsInfo;
    private TextView gnssHardwareYear;
    private LinearLayout gnssHWYearView;
    private TextView timeToFix;
    private TextView longitude, latitude;
    private TextView altitude, speed;
    private TextView accuracy, bearing, satellitesCount;
    private TextView lastFix;
    private Button nMEAStart;

    private LocationManager locationManager;
    GPSModel gpsViewModel;

    public GpsInfoLocation() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gps_info, container, false);

        //TextViews
        gpsInfo = view.findViewById(R.id.gpsInfo);
        gnssHardwareYear = view.findViewById(R.id.gnssHardwareYear);
        gnssHWYearView = view.findViewById(R.id.gnssHWYearView);
        timeToFix = view.findViewById(R.id.timeToFirstFix);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        altitude = view.findViewById(R.id.altitude);
        speed = view.findViewById(R.id.speed);
        accuracy = view.findViewById(R.id.accuracy);
        bearing = view.findViewById(R.id.bearing);
        satellitesCount = view.findViewById(R.id.satellitesCount);
        lastFix = view.findViewById(R.id.locUpdate);
        nMEAStart = view.findViewById(R.id.nmeaStart);

        nMEAStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), NMEAfeed.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "NMEA fead is available in Android Nougat and newer.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gpsViewModel = new ViewModelProvider(getActivity()).get(GPSModel.class);

        gpsViewModel.getUpToDateLocationInfo().observe(this, new Observer<GPSModel.GPSLocationInfoObject>() {
            @Override
            public void onChanged(@Nullable GPSModel.GPSLocationInfoObject gpsLocationInfoObject) {
                latitude.setText((gpsLocationInfoObject.getLatitudeS() != null) ? gpsLocationInfoObject.getLatitudeS() : "");
                longitude.setText((gpsLocationInfoObject.getLongitudeS() != null) ? gpsLocationInfoObject.getLongitudeS() : "");
                altitude.setText((gpsLocationInfoObject.getAltitudeS() != null) ? gpsLocationInfoObject.getAltitudeS() : "");
                speed.setText((gpsLocationInfoObject.getSpeedS() != null) ? gpsLocationInfoObject.getSpeedS() : "");
                accuracy.setText((gpsLocationInfoObject.getAccuracyS() != null) ? gpsLocationInfoObject.getAccuracyS() : "");
                bearing.setText((gpsLocationInfoObject.getBearingS() != null) ? gpsLocationInfoObject.getBearingS() : "");
                lastFix.setText((gpsLocationInfoObject.getLastFix() != null) ? gpsLocationInfoObject.getLastFix() : "");
                gpsInfo.setText((gpsLocationInfoObject.getGpsInfo() != null) ? gpsLocationInfoObject.getGpsInfo() : "");
                timeToFix.setText((gpsLocationInfoObject.getTimeToFix() != null) ? gpsLocationInfoObject.getTimeToFix() : "");
                satellitesCount.setText(String.format(Locale.ENGLISH, "%d", gpsLocationInfoObject.getSatelliteCount()));

                if (gpsLocationInfoObject.getGnssYearOfHardware() > 0) {
                    gnssHWYearView.setVisibility(View.VISIBLE);
                    gnssHardwareYear.setText(String.format(Locale.ENGLISH, "%d", gpsLocationInfoObject.getGnssYearOfHardware()));
                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {

            locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);

            // check if GPS provider is enabled
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder
                        .setMessage("GPS Location Service is turned off. Do you want to turn GPS Location on?")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog gpsAlertDialog = builder.create();
                gpsAlertDialog.show();
            }

        }
    }

    public void showAlertOnDisabled() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage("GPS Location Service is turned off. Do you want to turn GPS Location on?")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog gpsAlertDialog = builder.create();
        gpsAlertDialog.show();
    }
}
