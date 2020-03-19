package com.pacmac.devinfo.cpu;

import android.os.Build;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.utils.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CPUViewModel extends ViewModel {

    public static final String EXPORT_FILE_NAME = "cpu_info";
    private static final String BOARD_PLATFORM = "ro.board.platform";


    private MutableLiveData<List<UIObject>> cpuInfo = new MutableLiveData<>();


    public MutableLiveData<List<UIObject>> getCpuInfo() {
        new Thread(() -> loadCPUInfo()).start();
        return cpuInfo;
    }

    public List<UIObject> getCpuInfoForExport() {
        return cpuInfo.getValue();
    }

    private void loadCPUInfo() {
        List<UIObject> list = new ArrayList<>();

        list.addAll(readCPUinfo());

        list.add(new UIObject("Cores", "Active: " +
                Runtime.getRuntime().availableProcessors() + "    Total: " + getNumCores()));
        list.addAll(getCPUFrequency());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            String.format(Locale.ENGLISH, "%.2f", (readUsage() * 100));
        }
        cpuInfo.postValue(list);
    }


    private List<UIObject> readCPUinfo() {

        List<UIObject> list = new ArrayList<>();

        String processor = null;
        String chipset = null;
        String features = null;
        String architecture = null;
        String variant = null;
        String revision = null;
        String implementer = null;

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
            return list;
        }
        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        try {
            while ((line = br.readLine()) != null) {

//                Log.d("PACMAC", line);

                if (line.startsWith("Processor\t:")) {
                    int i = line.indexOf(":");
                    processor = line.substring(i + 1).trim();
                } else if (line.startsWith("Features\t:")) {
                    int i = line.indexOf(":");
                    features = line.substring(i + 1).trim();
                } else if (line.startsWith("Hardware\t:")) {
                    int i = line.indexOf(":");
                    chipset = line.substring(i + 1).trim();
                } else if (line.startsWith("CPU architecture:")) {
                    int i = line.indexOf(":");
                    architecture = line.substring(i + 1).trim();
                } else if (line.startsWith("CPU variant\t:")) {
                    int i = line.indexOf(":");
                    variant = line.substring(i + 1).trim();
                } else if (line.startsWith("CPU revision\t:")) {
                    int i = line.indexOf(":");
                    revision = line.substring(i + 1).trim();
                } else if (line.startsWith("CPU implementer\t:")) {
                    int i = line.indexOf(":");
                    implementer = line.substring(i + 1).trim();
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

        if (processor != null) {
            list.add(new UIObject("Processor", processor));
        }
        if (chipset != null) {
            list.add(new UIObject("Chipset", chipset));
        } else {
            try {
                chipset = Utility.getDeviceProperty(BOARD_PLATFORM).toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (chipset.length() == 0) {
                chipset = Build.HARDWARE;
            }
            list.add(new UIObject("Chipset", chipset));
        }
        if (features != null) {
            list.add(new UIObject("Features", features));
        }
        if (features != null) {
            list.add(new UIObject("Revision", revision));
        }
        if (features != null) {
            list.add(new UIObject("Variant", variant));
        }
        if (features != null) {
            list.add(new UIObject("Architecture", architecture));
        }
        if (features != null) {
            list.add(new UIObject("Implementer", implementer));
        }
        return list;
    }


    private List<UIObject> getCPUFrequency() {

        List<UIObject> list = new ArrayList<>();
        String maxFrequency = null;
        String currentFrequency = null;

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

        if (currentFrequency != null) {
            list.add(new UIObject("Current Frequency", currentFrequency, "GHz"));
        }
        if (maxFrequency != null) {
            list.add(new UIObject("Max Frequency", maxFrequency, "GHz"));
        }
        return list;
    }


    private int getNumCores() {

        //Private Class to display only CPU devices in the directory listing

        class CpuFilter implements FileFilter {

            @Override

            public boolean accept(File pathname) {
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
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
}
