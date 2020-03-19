package com.pacmac.devinfo.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.pacmac.devinfo.UIObject;
import com.pacmac.devinfo.cellular.MobileNetworkUtil;

import java.util.Locale;

public class MainUtils {

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getSerialNumber(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return null;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return Build.getSerial();
            }
        } else {
            return Build.SERIAL;
        }
        return null;
    }

    public static String getHardware() {
        return Build.HARDWARE.toUpperCase() + " " + Build.BOARD;
    }

    public static String getBuildNumber() {
        return Build.DISPLAY;
    }

    public static String getBootloader() {
        return Build.BOOTLOADER;
    }

    public static String getRadioFirmware() {
        String radioFW = Build.getRadioVersion();
        if (radioFW != null) {
            return radioFW;
        }
        return null;
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE + "  API:" + Build.VERSION.SDK_INT;
    }

    public static String getDeviceLanguageSetting() {
        return Locale.getDefault().getDisplayLanguage();
    }

    public static String getDeviceLanguageLocale() {
        return Locale.getDefault().getDisplayCountry();
    }

    public static UIObject getSimCount(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return MobileNetworkUtil.getSIMCount(context, telephonyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
