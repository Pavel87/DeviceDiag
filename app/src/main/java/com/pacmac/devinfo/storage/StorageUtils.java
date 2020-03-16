package com.pacmac.devinfo.storage;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.pacmac.devinfo.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;

public class StorageUtils {

    public static ByteValue getTotalMemory(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return StorageUtils.byteConvertor(memoryInfo.totalMem);
    }

    public static ByteValue getAvailableMemory(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return StorageUtils.byteConvertor(memoryInfo.availMem);
    }

    public static String getLowMemoryStatus(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo.lowMemory ? "YES" : "NO";
    }

    public static String getRAMHardware() {
        try {
            String memorychip = Utility.getDeviceProperty("ro.boot.hardware.ddr");
            String[] chipelements = memorychip.split(",");
            return String.format(Locale.ENGLISH, "%s %s - %s", chipelements[1], chipelements[2], chipelements[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Gets the device free and total disk space in bytes.
     *
     * @return The free and total disk space in a Bundle form.
     */
    protected static List<StorageSpace> getDeviceStorage(Context cont) {

        List<StorageSpace> listStorage = new ArrayList<>();
        List<String> listPaths = new ArrayList<>();

        // This part will identify the total/free space in internal user accessible storage ("/data")
        try {
            listStorage.add(new StorageSpace(TYPE_DATA, getTotalMemoryForPath(Environment.getDataDirectory().toString()), getFreeMemoryForPath(Environment.getDataDirectory().toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get externalStorageDirectory needs either the WRITE_EXTERNAL_STORAGE or READ_EXTERNAL_STORAGE
        // permission. If this permission is enabled by default if the application does not request
        // either of those permissions. If the application DOES however request the write permission,
        // on Android M or later devices, they can then subsequently revoke that permission.
        // The following checks to see if the application has requested those permissions
        // If permission to read primary/secondary storage is granted we will search and read
        // primary(built in) SD and external(removable) SD card
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

    public static final int TYPE_DATA = 0;
    public static final int TYPE_INTERNAL_SD = 1;
    public static final int TYPE_EXTERNAL_SD = 2;

    public static String getTypeString(int type) {
        switch (type) {
            case TYPE_DATA:
                return "Internal Storage";
            case TYPE_INTERNAL_SD:
                return "Internal SD";
            case TYPE_EXTERNAL_SD:
                return "SDCard";
        }
        return "UNKNOWN";
    }

    private static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }


    public static ByteValue byteConvertor(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return new ByteValue(floatForm(size), "Byte");
        if (size >= Kb && size < Mb) return new ByteValue(floatForm((double) size / Kb), "KB");
        if (size >= Mb && size < Gb) return new ByteValue(floatForm((double) size / Mb), "MB");
        if (size >= Gb && size < Tb) return new ByteValue(floatForm((double) size / Gb), "GB");
        if (size >= Tb && size < Pb) return new ByteValue(floatForm((double) size / Tb), "TB");
        if (size >= Pb && size < Eb) return new ByteValue(floatForm((double) size / Pb), "PB");
        if (size >= Eb) return new ByteValue(floatForm((double) size / Eb), "EB");

        return new ByteValue("error", "");
    }

    public static class ByteValue {
        private String value;
        private String unit;

        public ByteValue(String value, String unit) {
            this.value = value;
            this.unit = unit;
        }

        public String getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }
    }
}
