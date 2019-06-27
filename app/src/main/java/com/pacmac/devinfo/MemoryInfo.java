package com.pacmac.devinfo;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MemoryInfo extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable timer;
    private TextView ramHardware, ramTotal, ramAvailable, ramLow;
    private TextView storageTotal, storageAvailable, storageUsed;
    private ScrollView mainScrollView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    boolean isPermissionEnabled = true;
    private static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    List<StorageSpace> listStorage = new ArrayList<>();

    public static final int TYPE_DATA = 0;
    public static final int TYPE_INTERNAL_SD = 1;
    public static final int TYPE_EXTERNAL_SD = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_info);

        ramHardware = findViewById(R.id.ramHardware);
        ramTotal = findViewById(R.id.ramTotal);
        ramAvailable = findViewById(R.id.ramAvailable);
        ramLow = findViewById(R.id.ramLow);

        storageTotal = findViewById(R.id.totalStorage);
        storageAvailable = findViewById(R.id.availableStorage);
        storageUsed = findViewById(R.id.usedStorage);

        mainScrollView = findViewById(R.id.mainScrollView);

        mRecyclerView = findViewById(R.id.storageRecyclerView);
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new StorageAdapter(listStorage);
        mRecyclerView.setAdapter(mAdapter);


        if (listStorage.size() < 2) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        // Check if user disabled CAMERA permission at some point
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), STORAGE_PERMISSION);
        }
        // GET STORAGE
        if (!isPermissionEnabled) {
            Utility.displayExplanationForPermission(this, getResources().getString(R.string.storage_permission_msg), STORAGE_PERMISSION);
        } else {
            getAndDisplayStorage(getApplicationContext());
            ((StorageAdapter) mAdapter).updateData(listStorage);
        }

        try {
           String memorychip = Utility.getDeviceProperty("ro.boot.hardware.ddr");
           String[] chipelements = memorychip.split(",");
           ramHardware.setText(chipelements[1] + " " + chipelements[2] + " - " + chipelements[0]);
           findViewById(R.id.ramchipView).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // get and display RAM usage
        getAndDisplayRAM();
    }

    private void getAndDisplayRAM() {

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        ramTotal.setText(Utility.bytesToHuman(memoryInfo.totalMem));
        ramAvailable.setText(Utility.bytesToHuman(memoryInfo.availMem));
        if (memoryInfo.lowMemory) {
            ramLow.setTextColor(Color.RED);
            ramLow.setText("YES!");
        } else {
            ramLow.setTextColor(getResources().getColor(R.color.connected_clr));
            ramLow.setText("NO");
        }
    }



    private void getAndDisplayStorage(final Context context) {
        //retrieve STORAGE OPTIONS
        listStorage = getDeviceStorage(context);

        if (listStorage != null && listStorage.size() > 0) {

            long total = 0;
            long free = 0;
            for (StorageSpace storage : listStorage) {
                total += storage.getTotal();
                free += storage.getFree();
            }
            storageTotal.setText(Utility.bytesToHuman(total));
            storageAvailable.setText(Utility.bytesToHuman(free));
            storageUsed.setText(Utility.bytesToHuman(total-free));
            if (listStorage.size() < 2) {
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
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

        mainScrollView.post(new Runnable() {
            @Override
            public void run() {
                mainScrollView.fullScroll(View.FOCUS_UP);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timer);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // GET STORAGE
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isPermissionEnabled = Utility.checkPermission(getApplicationContext(), STORAGE_PERMISSION);
        }
        if (isPermissionEnabled) {
            getAndDisplayStorage(getApplicationContext());
            ((StorageAdapter) mAdapter).updateData(listStorage);
            mainScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mainScrollView.fullScroll(View.FOCUS_UP);
                }
            });
        }
    }

    /**
     * Gets the device free and total disk space in bytes.
     *
     * @return The free and total disk space in a Bundle form.
     */
    protected static  List<StorageSpace> getDeviceStorage(Context cont) {

        List<StorageSpace> listStorage = new ArrayList<>();
        List<String> listPaths = new ArrayList<>();

        // This part will identify the total/free space in internal user accessible storage ("/data")
        try {
            listStorage.add(new StorageSpace(TYPE_DATA, getTotalMemoryForPath(Environment.getDataDirectory().toString()), getFreeMemoryForPath(Environment.getDataDirectory().toString())));
        } catch (Exception e){
            e.printStackTrace();
        }

        // Get externalStorageDirectory needs either the WRITE_EXTERNAL_STORAGE or READ_EXTERNAL_STORAGE
        // permission. If this permission is enabled by default if the application does not request
        // either of those permissions. If the application DOES however request the write permission,
        // on Android M or later devices, they can then subsequently revoke that permission.
        // The following checks to see if the application has requested those permissions
        // If permission to read primary/secondary storage is granted we will search and read
        // primary(built in) SD and external(removable) SD card
        if(Utility.checkPermission(cont, STORAGE_PERMISSION)) {
            try {
                // getSDPaths is searching for secondary(removable) storages and put its paths in list
                listPaths = getSDPaths();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                // This piece below asses whether the primary storage is part of shared memory
                // If primary storage is not part of shared memory then we read its size
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (!Environment.isExternalStorageEmulated()) {
                        listStorage.add(new StorageSpace(TYPE_INTERNAL_SD, getTotalMemoryForPath(Environment.getExternalStorageDirectory().getAbsolutePath()),
                                getFreeMemoryForPath(Environment.getExternalStorageDirectory().getAbsolutePath())));
                    }
                } else {
                    if (!Environment.getExternalStorageDirectory().getAbsoluteFile().toString().contains("emulated")) {
                        listStorage.add(new StorageSpace(TYPE_INTERNAL_SD, getTotalMemoryForPath(Environment.getExternalStorageDirectory().getAbsolutePath()),
                                getFreeMemoryForPath(Environment.getExternalStorageDirectory().getAbsolutePath())));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Iteration through list of available secondary(removable) storage
            if (listPaths != null && listPaths.size() != 0) {

                for (String path : listPaths) {
                    try {
                        //Sometimes the path might be inaccessible even it is added to the list
                        listStorage.add(new StorageSpace(TYPE_EXTERNAL_SD, getTotalMemoryForPath(path),
                                getFreeMemoryForPath(path)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return listStorage;
    }

    /**
     * Gets paths as String for removable storage
     *
     * @return list of strings containing storage card paths (secondary storage)
     */
    private static List<String> getSDPaths() throws Exception {
        List<String> sdPathList = new ArrayList<>();
        File mountList = new File("/proc/mounts");
        BufferedReader reader = null;
        try {
            if (mountList.exists()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(mountList), "UTF-8"));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(" /storage/")) {
                        String lineSplits[] = line.split(" ");
                        String sdPath = lineSplits[1];
                        if (!sdPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath()) && !sdPath.contains("emulated") && !sdPath.contains("self")) {
                            // make sure we add only unique paths to the list
                            if (!sdPathList.contains(sdPath))
                                sdPathList.add(sdPath);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return sdPathList;
    }

    /**
     * Method measure total space of given storage
     *
     * @param path is the path to root of storage card
     * @return total size of given storage
     */
    private static long getTotalMemoryForPath(String path) {
        long total = 0L;
        if (new File(path).isDirectory()) {
            StatFs statFs = new StatFs(path);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                total = (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
            } else {
                // API 17 has to use depracated methods
                total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
            }
        }
        return total;
    }
    /**
     * Method measure free space of given storage
     *
     * @param path is the path to root of storage card
     * @return available space of given storage
     */
    private static long getFreeMemoryForPath(String path) {
        long free = 0L;
        if (new File(path).isDirectory()) {
            StatFs statFs = new StatFs(path);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                free = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
            } else {
                // API 17 has to use depracated methods
                free = ((long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
            }
        }
        return free;
    }

    static class StorageSpace {
        private int type;
        private long total;
        private long free;

        public StorageSpace(int type, long total, long free) {
            this.type = type;
            this.total = total;
            this.free = free;
        }

        public int getType() {
            return type;
        }

        public long getTotal() {
            return total;
        }

        public long getFree() {
            return free;
        }
    }


    public static String getTypeString(int type) {
        switch(type) {
            case TYPE_DATA: return "Internal Storage";
            case TYPE_INTERNAL_SD: return "Internal SD";
            case TYPE_EXTERNAL_SD: return "SDCard";
        }
        return "UNKNOWN";
    }
}
