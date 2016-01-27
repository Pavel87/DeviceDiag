package com.pacmac.devicediag;

import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemoryInfo extends AppCompatActivity {


    TextView ramTotal, ramAvailable, ramLow, intTotal, intAvailable, intUsed, intSDTotal, intSDAvailable, intSDUsed,
            extTotal, extAvailable, extUsed;

    //TODO DO NOT HARDCODE SD CARDS PATH - DOESN"T WORK WITH MC40
    final String INTERNAL_APP = "/data";
    final String INTERNAL_SD = "/storage/sdcard0";
    final String EXTERNAL_SD = "/storage/sdcard1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_info);

        ramTotal = (TextView) findViewById(R.id.ramTotal);
        ramAvailable = (TextView) findViewById(R.id.ramAvailable);
        ramLow = (TextView) findViewById(R.id.ramLow);
        intTotal = (TextView) findViewById(R.id.intTotal);
        intAvailable = (TextView) findViewById(R.id.intAvailable);
        intUsed = (TextView) findViewById(R.id.intUsed);
        intSDTotal = (TextView) findViewById(R.id.intSDTotal);
        intSDAvailable = (TextView) findViewById(R.id.intSDAvailable);
        intSDUsed = (TextView) findViewById(R.id.intSDUsed);
        extTotal = (TextView) findViewById(R.id.extTotal);
        extAvailable = (TextView) findViewById(R.id.extAvailable);
        extUsed = (TextView) findViewById(R.id.extUsed);


        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);


        Log.d("TAG", "total: " + memoryInfo.totalMem + " totalC: " + bytesToHuman(memoryInfo.totalMem));
        Log.d("TAG", "avail: " + memoryInfo.availMem + " availC: " + bytesToHuman(memoryInfo.availMem));
        Log.d("TAG", "total: " + memoryInfo.threshold + " totalC: " + bytesToHuman(memoryInfo.threshold));

        ramTotal.setText(bytesToHuman(memoryInfo.totalMem));
        ramAvailable.setText(bytesToHuman(memoryInfo.availMem));
        if (memoryInfo.lowMemory) {
            ramLow.setTextColor(Color.RED);
            ramLow.setText("YES!");
        } else {
            ramLow.setTextColor(getResources().getColor(R.color.connected_clr));
            ramLow.setText("NO");
        }

        intTotal.setText(bytesToHuman(TotalMemory(INTERNAL_APP)));
        intAvailable.setText(bytesToHuman(FreeMemory(INTERNAL_APP)));
        intUsed.setText(bytesToHuman(BusyMemory(INTERNAL_APP)));

        intSDTotal.setText(bytesToHuman(TotalMemory(INTERNAL_SD)));
        intSDAvailable.setText(bytesToHuman(FreeMemory(INTERNAL_SD)));
        intSDUsed.setText(bytesToHuman(BusyMemory(INTERNAL_SD)));

        extTotal.setText(bytesToHuman(TotalMemory(EXTERNAL_SD)));
        extAvailable.setText(bytesToHuman(FreeMemory(EXTERNAL_SD)));
        extUsed.setText(bytesToHuman(BusyMemory(EXTERNAL_SD)));

    }

    public long TotalMemory(String path) {
        StatFs statFs = new StatFs(path);
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
        return Total;
    }

    public long FreeMemory(String path) {
        StatFs statFs = new StatFs(path);
        long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
        return Free;
    }

    public long BusyMemory(String path) {
        StatFs statFs = new StatFs(path);
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
        long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
        long Busy = Total - Free;
        return Busy;
    }

    public static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }


    public static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " KB";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " MB";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " GB";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " TB";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " PB";
        if (size >= Eb) return floatForm((double) size / Eb) + " EB";

        return "convertion error";
    }
}
