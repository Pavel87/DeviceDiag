package com.pacmac.devinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;



public class GpsInfoLocation extends Fragment {

    private TextView gpsInfo;
    private TextView timeToFix;
    private TextView longitude, latitude;
    private TextView altitude, speed;
    private TextView accuracy, bearing;
    private TextView lastFix;
    private Button nMEAStart;

    private LocationManager locationManager;

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
        gpsInfo = (TextView) view.findViewById(R.id.gpsInfo);
        timeToFix = (TextView) view.findViewById(R.id.timeToFirstFix);
        latitude = (TextView) view.findViewById(R.id.latitude);
        longitude = (TextView) view.findViewById(R.id.longitude);
        altitude = (TextView) view.findViewById(R.id.altitude);
        speed = (TextView) view.findViewById(R.id.speed);
        accuracy = (TextView) view.findViewById(R.id.accuracy);
        bearing = (TextView) view.findViewById(R.id.bearing);
        lastFix = (TextView) view.findViewById(R.id.locUpdate);
        nMEAStart = (Button) view.findViewById(R.id.nmeaStart);

        nMEAStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), NMEAfeed.class);
                startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* if (activity instanceof GPSSatListFragInitListener) {
            mGPSSatButtonListener = (GPSSatListFragInitListener) activity;
        } else {
            throw new RuntimeException(activity.getApplicationContext().toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    /*  public interface GPSSatListFragInitListener {
        void onGPSSatListFragRequest();
    }*/


    @Override
    public void onDetach() {
        super.onDetach();
      //  mGPSSatButtonListener = null;
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


    public void displayGPSData(String longitudeS,
                               String latitudeS,
                               String altitudeS,
                               String speedS,
                               String accuracyS,
                               String bearingS,
                               String lastFix) {

        // display Location on screen
            latitude.setText((latitudeS!= null)? latitudeS : "");
            longitude.setText((longitudeS!= null)? longitudeS : "");
            altitude.setText((altitudeS != null) ? altitudeS : "");
            speed.setText((speedS!= null)? speedS : "");
            accuracy.setText((accuracyS!= null)? accuracyS : "");
            bearing.setText((bearingS!= null)? bearingS : "");
            this.lastFix.setText((lastFix!= null)? lastFix : "");
    }

    public void displayGPSStatus(String gpsInfoS, String gpsTimeToFirstFix) {
        // display Location on screen
            gpsInfo.setText((gpsInfoS!= null)? gpsInfoS : "");
            timeToFix.setText((gpsTimeToFirstFix!= null)? gpsTimeToFirstFix : "");

    }
}
