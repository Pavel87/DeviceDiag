package com.pacmac.devinfo.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.pacmac.devinfo.BuildConfig
import com.pacmac.devinfo.cellular.MobileNetworkUtilKt
import java.util.Locale

object MainUtilsKt {

    val EXPORT_FILE_NAME = "main_device_info";
    const val MAIN_PREF_FILE = "de_vi_ce"
    private const val VERSION_KEY = "version_key"

    fun hasReadPhoneStatePermission(context: Context): Boolean =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }

    fun getModel(): String {
        return Build.MODEL
    }

    @SuppressLint("MissingPermission")
    fun getSerialNumber(hasPhonePermission: Boolean): String? {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return null
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            if (hasPhonePermission) {
                return Build.getSerial()
            }
        } else {
            return Build.SERIAL
        }
        return null
    }

    fun getHardware(): String {
        return Build.HARDWARE.uppercase(Locale.getDefault()) + " " + Build.BOARD
    }

    fun getBuildNumber(): String {
        return Build.DISPLAY
    }

    fun getBootloader(): String {
        return Build.BOOTLOADER
    }

    fun getRadioFirmware(): String? {
        return Build.getRadioVersion()
    }

    fun getOsVersion(): String {
        return "${Build.VERSION.RELEASE}  API:${Build.VERSION.SDK_INT}"
    }

    fun getDeviceLanguageSetting(): String {
        return Locale.getDefault().displayLanguage
    }

    fun getDeviceLanguageLocale(): String {
        return Locale.getDefault().displayCountry
    }

    fun getSimCount(telephonyManager: TelephonyManager) =
        MobileNetworkUtilKt.getSIMCount(telephonyManager)


    fun getappVersionCode(packageManager: PackageManager): Int {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            (packageManager.getPackageInfo(BuildConfig.APPLICATION_ID, 0)
                .longVersionCode and 0x0000FFFFL).toInt()
        } else {

            packageManager.getPackageInfo(BuildConfig.APPLICATION_ID, 0).versionCode
        }
    }

    fun getPhoneNumbers(
        telephonyManager: TelephonyManager,
        subscriptionManager: SubscriptionManager,
        slotCount: Int,
        hasPhonePermission: Boolean
    ): ArrayList<String> {
        val phoneList = arrayListOf<String>()
        if (hasPhonePermission.not()) return phoneList

        for (i: Int in 0 until slotCount) {
            val phoneNumber = MobileNetworkUtilKt.getLine1Number(
                telephonyManager,
                subscriptionManager,
                i,
                slotCount > 1,
                true
            )
            phoneNumber?.let { phoneList.add(it) }
        }
        return phoneList
    }
}