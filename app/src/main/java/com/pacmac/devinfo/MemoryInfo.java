package com.pacmac.devinfo;

import android.Manifest;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class MemoryInfo extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable timer;
    private TextView ramHardware, ramTotal, ramAvailable, ramLow;
    private TextView storageTotal, storageAvailable, storageUsed;
    private Spinner storageSpinner;

    boolean isPermissionEnabled = true;
    private static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_info);

        ramHardware = (TextView) findViewById(R.id.ramHardware);
        ramTotal = (TextView) findViewById(R.id.ramTotal);
        ramAvailable = (TextView) findViewById(R.id.ramAvailable);
        ramLow = (TextView) findViewById(R.id.ramLow);
        storageTotal = (TextView) findViewById(R.id.storageTotal);
        storageAvailable = (TextView) findViewById(R.id.storageAvailable);
        storageUsed = (TextView) findViewById(R.id.storageUsed);
        storageSpinner = (Spinner) findViewById(R.id.storageSpinner);

        // Check if user disabled CAMERA permission at some point
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), STORAGE_PERMISSION);
        }

        if (!isPermissionEnabled) {
            Utility.displayExplanationForPermission(this, getResources().getString(R.string.storage_permission_msg), STORAGE_PERMISSION);
        }

        try {
           String memorychip = Utility.getDeviceProperty("ro.boot.hardware.ddr");
           String[] chipelements = memorychip.split(",");
           findViewById(R.id.ramchipView).setVisibility(View.VISIBLE);
           ramHardware.setText(chipelements[1] + " " + chipelements[2] + " - " + chipelements[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //retrieve STORAGE OPTIONS
        final List<String> listStorage;
        List<CharSequence> listStorageNames = new ArrayList<>();
        listStorage = getSDPaths();
        Collections.reverse(listStorage);
        for (String storage : listStorage) {

            if (storage.contains("/dat")) {
                listStorageNames.add("INTERNAL");
            } else if (storage.contains("/sdcard")) {
                int start = storage.indexOf("sdcard");
                listStorageNames.add(storage.substring(start).toUpperCase());
            } else {
                listStorageNames.add(storage);
            }
        }

        // HANDLE SPINNER
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, listStorageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storageSpinner.setAdapter(adapter);


        storageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                displayStorage(listStorage.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // get and display RAM usage
        getAndDisplayRAM();

        //display internal storage "/data" by default
        displayStorage(listStorage.get(0));

    }

    private void getAndDisplayRAM() {

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        ramTotal.setText(bytesToHuman(memoryInfo.totalMem));
        ramAvailable.setText(bytesToHuman(memoryInfo.availMem));
        if (memoryInfo.lowMemory) {
            ramLow.setTextColor(Color.RED);
            ramLow.setText("YES!");
        } else {
            ramLow.setTextColor(getResources().getColor(R.color.connected_clr));
            ramLow.setText("NO");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 5000);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        getAndDisplayRAM();
                    }
                });
            }
        };
        mHandler.postDelayed(timer, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timer);
    }


    private List<String> getSDPaths() {
        List<String> sdPathList = new ArrayList<>();
        try {
            File mountList = new File("/proc/mounts");

            if (mountList.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mountList)));
                String line= null;

                while ((line = reader.readLine()) != null) {

                    if (line.contains("/dev/block/vold/") || line.contains("/dev/fuse")) {

                        String lineSplits[] = line.split(" ");
                        String sdPath = lineSplits[1];
                        if (sdPath.contains("storage/sdcard")) {
                            sdPathList.add(sdPath);
                        }
                    }
                }
            }

            //TODO figure out which is primary storage i.e. using getExternalStorageDirectory
        } catch (Exception e) {
            e.printStackTrace();
        }
        sdPathList.add("/data");
        return sdPathList;
    }


    private void displayStorage(String filePath) {

        storageTotal.setText(bytesToHuman(TotalMemory(filePath)));
        storageAvailable.setText(bytesToHuman(FreeMemory(filePath)));
        storageUsed.setText(bytesToHuman(BusyMemory(filePath)));

        return;
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

        if (size < Kb) return floatForm(size) + " Byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " KB";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " MB";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " GB";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " TB";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " PB";
        if (size >= Eb) return floatForm((double) size / Eb) + " EB";

        return "convertion error";
    }

}
