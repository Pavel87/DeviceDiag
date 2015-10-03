package com.pacmac.devicediag;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Created by pacmac on 5/28/2015.
 */
public class CPUInfo extends ActionBarActivity {

    TextView activeCores;
    TextView processor;
    TextView hardWareCpu;
    TextView featuresCPU;
    TextView cpuMaxFrequency;
    TextView cpuCurrentFreq;

    private String cpu = "unknown";
    private String features = "unknown";
    private String hardware = "unknown";
    private String currentFrequency= "unknown";;
    private String maxFrequency= "unknown";;


    public int addNumbers(int a, int b) {

        if (a>=50){
            throw new NumberFormatException("first number has to be less than 50");
        }
        return a+b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpuinfo);

        activeCores = (TextView) findViewById(R.id.activeCores);
        processor = (TextView) findViewById(R.id.processor);
        featuresCPU = (TextView) findViewById(R.id.features);
        hardWareCpu = (TextView) findViewById(R.id.cpuHardware);
        cpuMaxFrequency= (TextView) findViewById(R.id.cpuMaxFrequency);
        cpuCurrentFreq = (TextView) findViewById(R.id.cpuCurrentFrequency);
        updateView();

    }


    private void updateView() {

        readCPUinfo();
        readCPUFreq();
        activeCores.setText("Active: " + Runtime.getRuntime().availableProcessors() + "\nTotal: " + getNumCores());
        processor.setText(cpu);
        hardWareCpu.setText(hardware);
        featuresCPU.setText(features);
        cpuMaxFrequency.setText(maxFrequency + " GHz");
        cpuCurrentFreq.setText(currentFrequency + " GHz");
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
        try {
            fis = new FileInputStream(fin);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        float temp = 0;
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                temp = Float.parseFloat(line) / 1000000;
                maxFrequency = "" +temp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /// read the current CPU freq

        //use to get current directory
        fin = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        fis = null;
        try {
            fis = new FileInputStream(fin);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Construct BufferedReader from InputStreamReader
        br = new BufferedReader(new InputStreamReader(fis));

        line = null;
        try {
            while ((line = br.readLine()) != null) {
                temp = Float.parseFloat(line) / 1000000;
                currentFrequency = "" +String.format("%.3f", temp);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sim, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
