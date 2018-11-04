package com.pacmac.devinfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

/**
 * Created by pacmac on 5/28/2015.
 */
public class CPUInfo extends AppCompatActivity {

    private static final String BOARD_PLATFORM = "ro.board.platform";

    TextView activeCores;
    TextView processor;
    TextView hardWareCpu;
    TextView featuresCPU;
    TextView cpuMaxFrequency;
    TextView cpuCurrentFreq;

    private String cpu = "unknown";
    private String features = "unknown";
    private String hardware = "unknown";
    private String currentFrequency = "unknown";
    private String maxFrequency = "unknown";


    private GraphView graph;
    private LineGraphSeries<DataPoint> seriesLive;
    private double graphLastXValue = 0d;
    private final Handler mHandler = new Handler();
    private Runnable timer;
    private float usage = 0;
    private ShareActionProvider mShareActionProvider;
    private boolean isInfoCollected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpuinfo);


        activeCores = (TextView) findViewById(R.id.activeCores);
        processor = (TextView) findViewById(R.id.processor);
        featuresCPU = (TextView) findViewById(R.id.features);
        hardWareCpu = (TextView) findViewById(R.id.cpuHardware);
        cpuMaxFrequency = (TextView) findViewById(R.id.cpuMaxFrequency);
        cpuCurrentFreq = (TextView) findViewById(R.id.cpuCurrentFrequency);
        View cpuChart = findViewById(R.id.cpuChart);

        updateView();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //graph settings
            graph = (GraphView) findViewById(R.id.graph1);

            seriesLive = new LineGraphSeries<DataPoint>();
            seriesLive.setColor(getResources().getColor(R.color.tabs));
            seriesLive.setDrawBackground(true);
            seriesLive.setBackgroundColor(getResources().getColor(R.color.graph_filling));
            seriesLive.setThickness(2);
            seriesLive.setTitle("CPU Usage");
            graph.addSeries(seriesLive);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(10);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(100);
            graph.getViewport().setBackgroundColor(Color.WHITE);

            graph.getGridLabelRenderer().setGridColor(Color.WHITE);
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            graph.getGridLabelRenderer().setNumVerticalLabels(5);
            graph.getGridLabelRenderer().setNumHorizontalLabels(4);
            graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
            graph.getGridLabelRenderer().reloadStyles();
        } else {
            cpuChart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                updateGraph();
                if (!isInfoCollected) {
                    updateShareIntent();        // WILL UPDATE SHARE INTENT ONLY ONCE AS DATA IS STATIC
                }
                mHandler.postDelayed(this, 300);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }
        };
        mHandler.postDelayed(timer, 300);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timer);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void updateGraph() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    usage = readUsage() * 100;
                    graphLastXValue += 0.1d;
                    seriesLive.appendData(new DataPoint(graphLastXValue, usage), true, 120);
                }
            }).run();
        }

    }


    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
                e.printStackTrace();
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }


    private void updateView() {
        readCPUinfo();
        readCPUFreq();
        activeCores.setText("Active: " + Runtime.getRuntime().availableProcessors() + "\tTotal: " + getNumCores());
        processor.setText(cpu);
        featuresCPU.setText(features);
        cpuMaxFrequency.setText(maxFrequency + " GHz");
        cpuCurrentFreq.setText(currentFrequency + " GHz");
        if (hardware.equals("unknown")) {
            try {
                hardware = Utility.getDeviceProperty(BOARD_PLATFORM).toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hardWareCpu.setText(hardware);
    }

    /*
    Gets the number of cores available in this device, across all processors.
    Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
    @return The number of cores, or 1 if failed to get result*/

    private int getNumCores() {

        //Private Class to display only CPU devices in the directory listing

        class CpuFilter implements FileFilter {

            @Override

            public boolean accept(File pathname) {

                //Check if filename is "cpu", followed by a single digit number

                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {

                    return true;

                }

                return false;
            }
        }

        try {

            //Get directory containing CPU info

            File dir = new File("/sys/devices/system/cpu/");

            //Filter to only list the devices we care about

            File[] files = dir.listFiles(new CpuFilter());


            //Return the number of cores (virtual CPU devices)

            return files.length;

        } catch (Exception e) {

            //Print exception

            Log.d("DeviceDiag", "CPU Count: Failed.");

            e.printStackTrace();

            //Default to return 1 core

            return 1;

        }

    }


    public void readCPUinfo() {


        //use to get current directory
        File fin = new File("/proc/cpuinfo");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fin);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fis == null || !fin.exists() || !fin.canRead()) {
            Log.e("CPUInfo", "Cannot access CPUINFO.");
            return;
        }
        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        try {
            while ((line = br.readLine()) != null) {

                if (line.startsWith("Processor\t:")) {
                    cpu = line;
                    int i = line.indexOf(":");
                    cpu = line.substring(i + 1).trim();
                } else if (line.startsWith("Features\t:")) {
                    features = line;
                    int i = line.indexOf(":");
                    features = line.substring(i + 1).trim();

                } else if (line.startsWith("Hardware\t:")) {
                    hardware = line;
                    int i = line.indexOf(":");
                    hardware = line.substring(i + 1).trim();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readCPUFreq() {

        //read max freq CPU
        File fin = new File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        FileInputStream fis = null;
        String line = null;
        BufferedReader br;
        float temp = 0;
        if (fin.exists()) {
            try {
                fis = new FileInputStream(fin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (fis != null) {
                //Construct BufferedReader from InputStreamReader
                br = new BufferedReader(new InputStreamReader(fis));

                try {
                    while ((line = br.readLine()) != null) {
                        temp = Float.parseFloat(line) / 1000000;
                        maxFrequency = "" + temp;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /// read the current CPU freq

        //use to get current directory
        fin = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        if (fin.exists()) {
            fis = null;
            try {
                fis = new FileInputStream(fin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fis != null) {
                //Construct BufferedReader from InputStreamReader
                br = new BufferedReader(new InputStreamReader(fis));
                try {
                    while ((line = br.readLine()) != null) {
                        temp = Float.parseFloat(line) / 1000000;
                        currentFrequency = "" + String.format("%.3f", temp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // SHARE CPU INFO VIA ACTION_SEND
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_share, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shareTextEmpty));
        return shareIntent;
    }

    private Intent createShareIntent(StringBuilder sb) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, Build.MODEL + "\t-\t"
                + getResources().getString(R.string.title_activity_cpu_info));
        return shareIntent;
    }


    private void updateShareIntent() {

        isInfoCollected = true;
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n");
        sb.append(Build.MODEL + "\t-\t" + getResources().getString(R.string.title_activity_cpu_info));
        sb.append("\n");
        sb.append(getResources().getString(R.string.shareTextTitle1));
        sb.append("\n\n");

        //body
        sb.append("Processor:\t\t" + cpu);
        sb.append("\n");
        sb.append("Chip:\t\t" + hardware);
        sb.append("\n");
        sb.append("CPU CORES:\t\t" + getNumCores());
        sb.append("\n");
        sb.append("CPU Max Frequency:\t\t" + maxFrequency + " GHz");
        sb.append("\n");
        sb.append("CPU Features:\t\t" + features);
        sb.append("\n\n");

        sb.append(getResources().getString(R.string.shareTextTitle1));
        setShareIntent(createShareIntent(sb));
    }


}
