package com.pacmac.devinfo.gps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pacmac.devinfo.ExportActivity;
import com.pacmac.devinfo.R;
import com.pacmac.devinfo.RunnableShowTitle;
import com.pacmac.devinfo.utils.ExportTask;
import com.pacmac.devinfo.utils.ExportUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GPSInfo extends AppCompatActivity implements LocationListener, ExportTask.OnExportTaskFinished {

    private boolean enabled = false;
    private GPSViewModel viewModel;
    private boolean isExporting = false;
    private boolean isListeningGPSUpdates = false;

    private LocationManager locationManager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        viewModel = new ViewModelProvider(this).get(GPSViewModel.class);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        setSupportActionBar(findViewById(R.id.toolbar));
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        viewModel.setEnabled(enabled);
        if (!enabled) {
            showAlertOnDisabled();
        }
        // show dialog if GPS disabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            viewModel.setGnssHardwareYear(String.valueOf(locationManager.getGnssYearOfHardware()));
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        try {
            enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (enabled) {
                if (!isListeningGPSUpdates) {
                    isListeningGPSUpdates = true;
                    if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
                        locationManager.addGpsStatusListener(gpsStatusListener);
                    }
                }
            }
        } catch (Exception e) {
            enabled = false;
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "GPS is not available.", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            if (enabled) {
                if (isListeningGPSUpdates) {
                    if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
                        locationManager.removeGpsStatusListener(gpsStatusListener);
                        locationManager.removeUpdates(this);
                    }
                    isListeningGPSUpdates = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    public void showAlertOnDisabled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS Location Service is turned off. Do you want to turn GPS Location on?")
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share) {
            if (!isExporting) {
                isExporting = true;

                if (tabs.getSelectedTabPosition() == 2) {
                    new ExportTask(getApplicationContext(), GPSViewModel.EXPORT_NMEA_FILE_NAME, this).execute(viewModel);
                } else {
                    new ExportTask(getApplicationContext(), GPSViewModel.EXPORT_FILE_NAME, this).execute(viewModel);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onExportTaskFinished(String filePath) {
        isExporting = false;
        if (filePath != null) {
            Intent intent = new Intent(getApplicationContext(), ExportActivity.class);
            intent.putExtra(ExportUtils.EXPORT_FILE, filePath);
            startActivity(intent);
        }
    }


    @Override
    public void onLocationChanged(final Location location) {

        new Thread(() -> {

            boolean isConnected = false;
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo networkInfo;

            // check WIFI state and if present in device
            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
                networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                isConnected = networkInfo.isConnectedOrConnecting();
            } else if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                isConnected = networkInfo.isConnectedOrConnecting();
            }

            if (!isConnected) {
                return;
            }

            // TODO don't do this on every loc update - check for distance to update
            // Geocoder will resolve addreess
            Geocoder geocoder = new Geocoder(getApplicationContext());
            if (geocoder.isPresent()) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses == null || addresses.size() == 0) return;
                    String street = addresses.get(0).getThoroughfare();
                    String numHouse = addresses.get(0).getSubThoroughfare();
                    String city = addresses.get(0).getSubAdminArea();
                    String postalCode = addresses.get(0).getPostalCode();

                    street = street == null ? "" : street;
                    numHouse = numHouse == null ? "" : numHouse;
                    city = city == null ? "" : city;
                    postalCode = postalCode == null ? "" : postalCode;


                    //display address in ACTION BAR
                    runOnUiThread(new RunnableShowTitle(street, numHouse, city, postalCode) {
                        @Override
                        public void run() {
                            getSupportActionBar().setTitle(getStreet() + " " + getNumHouse());
                            getSupportActionBar().setSubtitle(getCity() + " " + getPostalCode());
                        }
                    });


                } catch (IOException ex) {
                    ex.printStackTrace(); // will throw if no connection to server
                }
            }


        }).start();


        int speedInt = (int) (location.getSpeed() * 3.6f);
        long timeRaw = location.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeRaw);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);


        viewModel.setUpdateTime(String.format(Locale.ENGLISH, "%d:%d:%d", hour, minute, second));
        viewModel.setLatitude(String.valueOf(location.getLatitude()));
        viewModel.setLongitude(String.valueOf(location.getLongitude()));
        viewModel.setAltitude(String.valueOf(location.getAltitude()));
        viewModel.setSpeed(String.valueOf(speedInt));
        viewModel.setAccuracy(String.format(Locale.ENGLISH, "%.01f", location.getAccuracy()));
        viewModel.setBearing(String.format(Locale.ENGLISH, "%.02f", location.getBearing()));

        viewModel.getMainGPSData();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
    }


    // GPS STATUS Listener
    @SuppressLint("MissingPermission")
    private GpsStatus.Listener gpsStatusListener = event -> {

        try {
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);

            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    if (gpsStatus != null) {
                        viewModel.setFirstFix(gpsStatus.getTimeToFirstFix());
                    }
                    viewModel.setGpsState("First Fix");
                    break;

                case GpsStatus.GPS_EVENT_STARTED:
                    viewModel.setGpsState("Starting");
                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                    viewModel.setGpsState("Inactive");
                    break;

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    viewModel.setGpsState("Active");
                    Iterator<GpsSatellite> satelliteIterator = gpsStatus.getSatellites().iterator();
                    List<Satellites> satellitesList = new ArrayList<>();
                    int i = 1;

                    while (satelliteIterator.hasNext()) {
                        GpsSatellite satellite = satelliteIterator.next();
                        if (satellite.usedInFix()) {
                            satellitesList.add(new Satellites(i, satellite.getSnr(), satellite.getPrn(), satellite.getAzimuth(), satellite.getElevation()));
                            //
                            i++;
                        }
                    }

                    viewModel.setVisibleSatellites(String.valueOf(satellitesList.size()));
                    viewModel.updateSatellites(satellitesList);
            }
            viewModel.getMainGPSData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}