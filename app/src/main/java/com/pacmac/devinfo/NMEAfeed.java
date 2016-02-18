package com.pacmac.devinfo;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import android.os.Handler;
import android.widget.Toast;


public class NMEAfeed extends ActionBarActivity implements GpsStatus.NmeaListener, LocationListener {

    private TextView nmeaUpdate;
    private Button nmeaRollButton;
    private CheckBox saveCheckBox;
    private ScrollView mScrollView;

    private final Handler mHandler = new Handler();
    private Runnable timer;

    private String html = "";
    private final int MAX_LOG_LINES = 128;
    private int count = 1;
    boolean isSaveToSDCard = false;

    private final int LOG_SIZE = 256000; //256kB
    private int sizeCounter = 0;

    LocationManager locationManager;

    private boolean isNMEAListenerOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmeafeed);

        nmeaUpdate = (TextView) findViewById(R.id.nmeaUpdate);
        nmeaRollButton = (Button) findViewById(R.id.nmeaRollButton);
        saveCheckBox = (CheckBox) findViewById(R.id.saveTofile);
        mScrollView = (ScrollView) findViewById(R.id.mScrollView);

        saveCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if (checked) {
                    //Log.d("TAG", "checked");
                    isSaveToSDCard = true;
                    Toast.makeText(getApplicationContext(), "Log path: " + Environment.getExternalStorageDirectory() + "/NMEAlog.txt", Toast.LENGTH_SHORT).show();

                } else {
                   // Log.d("TAG", "disabled");
                    isSaveToSDCard = false;
                }
            }
        });

        locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);


        nmeaRollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNMEAListenerOn) {
                    mHandler.removeCallbacks(timer);
                    isNMEAListenerOn = false;
                    locationManager.removeNmeaListener(NMEAfeed.this);
                    locationManager.removeUpdates(NMEAfeed.this);
                    nmeaRollButton.setText("Start");
                    saveCheckBox.setEnabled(true);
                } else {
                    nmeaUpdate.setText("Fetching data from GPS...");
                    saveCheckBox.setEnabled(false);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) NMEAfeed.this);
                    locationManager.addNmeaListener(NMEAfeed.this);
                    isNMEAListenerOn = true;
                    nmeaRollButton.setText("Stop");

                    timer = new Runnable() {
                        @Override
                        public void run() {
                            mHandler.postDelayed(this, 200);

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    nmeaUpdate.setText(Html.fromHtml(html));

                                    mScrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mScrollView.smoothScrollTo(0, nmeaUpdate.getBottom());
                                        }
                                    });
                                }
                            });

                        }
                    };
                    mHandler.postDelayed(timer, 5000);
                }
            }
        });


    }

    private void deleteLogFile() {
        if (!isNMEAListenerOn) {
            if (isExternalStorageWritable()) {
                File directory = Environment.getExternalStorageDirectory();
                File file = new File(directory + "/NMEAlog.txt");


                if (file.exists()) {

                    file.delete();
                    Toast.makeText(getApplicationContext(), "Log deleted from SDcard", Toast.LENGTH_SHORT).show();
                }

            }
            nmeaUpdate.setText("Press the Start button in order to get NMEA data.");
        }
        else {
            Toast.makeText(getApplicationContext(),"Stop logging before deleting log.", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveToFile(Spanned logData) {

        if (isExternalStorageWritable()) {
            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory + "/NMEAlog.txt");


            if (!file.exists()) {
                try {
                    file.createNewFile();
                    //Log.d("TAG", "" + file.toString());
                } catch (IOException ioException) {
                    Log.e("TAG", "Can't create new file");
                }

                try {
                    BufferedWriter bWriter = new BufferedWriter(new FileWriter(file, true));
                    bWriter.append("=============NMEA FEED===============");
                    bWriter.newLine();
                    bWriter.append("============Log Started==============");
                    bWriter.newLine();
                    bWriter.flush();
                    bWriter.close();
                } catch (IOException ex) {
                    Log.e("TAG", "IO exception can't write to NMEAlog.txt");
                }
            }

            if (sizeCounter <= LOG_SIZE) {
                sizeCounter += logData.length();


                try {
                    BufferedWriter bWriter = new BufferedWriter(new FileWriter(file, true));
                    bWriter.append(logData);
                    bWriter.flush();
                    bWriter.close();
                  // Log.d("TAG", "Log saved and closed");

                } catch (IOException ioExc) {
                    Log.e("TAG", "IO exception can't write to NMEAlog.txt");
                }
            } else {
                //Log.d("TAG", "log is full - deleting old and creating new one");
                sizeCounter = 0;
                file.delete();  // deleting old log and will create a new file in next step

                try {
                    file.createNewFile();
                } catch (IOException ioException) {
                    Log.e("TAG", "Can't create new file");
                }
                try {
                    BufferedWriter bWriter = new BufferedWriter(new FileWriter(file, true));
                    bWriter.append("=============NMEA FEED===============");
                    bWriter.newLine();
                    bWriter.append("============Log Started==============");
                    bWriter.newLine();
                    bWriter.append(logData);
                    bWriter.flush();
                    bWriter.close();
                } catch (IOException ex) {
                    Log.e("TAG", "IO exception can't write to NMEAlog.txt");
                }
            }
        } else
            Log.d("TAG", "Primary SD card is not mounted");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        locationManager.removeNmeaListener(this);
        locationManager.removeUpdates(this);
        super.onPause();
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    public void onNmeaReceived(long l, String s) {


        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(l);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int milis = cal.get(Calendar.MILLISECOND);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);

        if (isSaveToSDCard) {
            count++;
            if (count >= MAX_LOG_LINES) {
                count = 1;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveToFile(Html.fromHtml(html));

                    }
                }).start();
            }
        }

        if (html.split("<br>").length > MAX_LOG_LINES) {
            html = html.substring(html.indexOf("<br>") + 4);
        }

        String timeDate = hour + ":" + minute + ":" +second+":"+ milis + " " + day + "/" + month + ": ";
        html = html + "<font color=\"" + getResources().getColor(android.support.v7.appcompat.R.color.abc_primary_text_material_dark) + "\">" + "<b>" + timeDate + "</b>" + "</font>" + s + "<br>";
    }

    @Override
    public void onLocationChanged(Location location) {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nmea_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteLogFile();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
