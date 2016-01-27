package com.pacmac.devicediag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by pacmac on 6/10/2015.
 */
public class GPSInfo extends ActionBarActivity implements LocationListener {

    private TextView gpsInfo;
    private TextView timeToFix;
    private TextView locationText;
    private TextView altSpeedText;
    private TextView accuracyBearingText;

    private TextView nMEAStart;
    private ListView list;

    private GpsStatus gpsStatus;
    private LocationManager locationManager;
    private GpsStatus.Listener gpsStatusListener;

    private Iterable<GpsSatellite> gpsSatelities;
    private SateliteAdapter mAdapter;
    private ArrayList<Satelites> satelites = new ArrayList<Satelites>();

    private boolean gpsEnabled = false;
    private boolean gpsHasFix = false;
    private boolean gpsHasStarted = false;
    private boolean gpsHasStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_info);

        //TextViews
        gpsInfo = (TextView) findViewById(R.id.gpsInfo);
        timeToFix = (TextView) findViewById(R.id.timeToFirstFix);
        locationText = (TextView) findViewById(R.id.location);
        altSpeedText = (TextView) findViewById(R.id.altSpeed);
        accuracyBearingText = (TextView) findViewById(R.id.accuracy);
        nMEAStart = (TextView) findViewById(R.id.nmeaStart);

        //list + adapter inicialization
        list = (ListView) findViewById(R.id.sateliteList);
        mAdapter = new SateliteAdapter(getApplicationContext(), satelites);
        View header = LayoutInflater.from(getApplicationContext()).inflate(R.layout.satelites_header, null);
        list.addHeaderView(header);
        list.setAdapter(mAdapter);


        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {


            locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);

            // check if GPS provider is enabled
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

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


            // definition of listeners
            gpsStatusListener = new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {

                    gpsStatus = locationManager.getGpsStatus(null);
                /*if (gpsStatus != null) {
                    mAdapter.clear();
                    gpsSatelities = gpsStatus.getSatellites();
                    Iterator<GpsSatellite> satelliteIterator = gpsSatelities.iterator();
                    int i = 1;
                    while (satelliteIterator.hasNext()) {
                        GpsSatellite satellite = satelliteIterator.next();
                        if (satellite.usedInFix()) {
                            Log.d("TAG", "sat:" + (i) + ": PNR:" + satellite.getPrn() + ",SNR:" + String.format("%.02f",satellite.getSnr()) + ",azimuth:" + satellite.getAzimuth() + ",elevation:" + satellite.getElevation() + "\n\n");
                            satelites.add(new Satelites(i, satellite.getSnr(), satellite.getPrn(), satellite.getAzimuth(), satellite.getElevation()));
                            // satelites.add(new Satelites(1, 33, 12.2, 182.1, 3.1));
                        }
                        i++;
                    }
                    mAdapter.notifyDataSetChanged();


                }*/
                    switch (event) {
                        case GpsStatus.GPS_EVENT_FIRST_FIX:
                            gpsHasFix = true;
                            gpsEnabled = true;
                            timeToFix.setText(gpsStatus.getTimeToFirstFix() + "ms");
                            Log.d("TAG", "gps event first fix");
                            break;

                        case GpsStatus.GPS_EVENT_STARTED:
                            gpsHasStarted = true;
                            gpsInfo.setText("gps event started");
                            Log.d("TAG", "gps event started");
                            break;

                        case GpsStatus.GPS_EVENT_STOPPED:
                            gpsHasStopped = true;
                            gpsInfo.setText("gps event stopped");
                            Log.d("TAG", "gps event stopped");
                            break;

                        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                            gpsInfo.setText("Satelite Status Changed");
                            Log.d("TAG", "satelite status changed");

                            mAdapter.clear();
                            gpsSatelities = gpsStatus.getSatellites();
                            Iterator<GpsSatellite> satelliteIterator = gpsSatelities.iterator();
                            int i = 1;
                            while (satelliteIterator.hasNext()) {
                                GpsSatellite satellite = satelliteIterator.next();
                                if (satellite.usedInFix()) {
                                    Log.d("TAG", "sat:" + (i) + ": PNR:" + satellite.getPrn() + ",SNR:" + String.format("%.02f", satellite.getSnr()) + ",azimuth:" + satellite.getAzimuth() + ",elevation:" + satellite.getElevation() + "\n\n");
                                    satelites.add(new Satelites(i, satellite.getSnr(), satellite.getPrn(), satellite.getAzimuth(), satellite.getElevation()));
                                    // satelites.add(new Satelites(1, 33, 12.2, 182.1, 3.1));
                                    i++;
                                }

                            }
                            mAdapter.notifyDataSetChanged();


                            break;


                    }
                }

            };

            nMEAStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), NMEAfeed.class);
                    startActivity(intent);
                }
            });

        } else
            Toast.makeText(getApplicationContext(), "GPS is not available on this device", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
            locationManager.addGpsStatusListener(gpsStatusListener);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            locationManager.removeGpsStatusListener(gpsStatusListener);
            locationManager.removeUpdates(this);
        }
        super.onPause();
    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d("TAG", "location: " + location.toString());

       /* Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(location.getTime());

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);*/

        locationText.setText(location.getLatitude() + "°/" + location.getLongitude() + "°");
        altSpeedText.setText(String.format("%.01f", location.getAltitude()) + "m/" + String.format("%.02f", location.getSpeed()) + "m/s");
        accuracyBearingText.setText(String.format("%.01f", location.getAccuracy()) + "m/" + String.format("%.02f", location.getBearing()) + "°");
        //timeText.setText(hour+"h"+minute+"m"+seconds+"s");
        Log.d("TAG", "satelites: " + location.getExtras().getInt("satelites"));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("TAG", "location status changed");
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
