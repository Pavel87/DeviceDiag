package com.pacmac.devinfo.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.PermissionChecker
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.UpToDateEnum
import java.io.BufferedReader
import java.io.InputStreamReader

object Utils {

    const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    const val PHONE_PERMISSION = Manifest.permission.READ_PHONE_STATE
    const val PHONE_NUMBER_PERMISSION = "android.permission.READ_PHONE_NUMBERS"
    const val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    /**
     * This method will check if permission is granted at runtime
     */

    fun checkPermission(context: Context, permission: String?): Boolean {
        val status = context.checkCallingOrSelfPermission(permission!!)
        return status == PermissionChecker.PERMISSION_GRANTED
    }


    fun createUIObject(context: Context, value: String?, stringRes: Int): UIObject {
        value ?: return UIObject(
            context.resources.getString(stringRes),
            context.getString(R.string.not_available_info)
        )
        return UIObject(context.resources.getString(stringRes), value)
    }

    fun createUIObject(context: Context, value: Boolean?, stringRes: Int): UIObject {
        value ?: return UIObject(
            context.resources.getString(stringRes),
            context.getString(R.string.not_available_info)
        )
        return UIObject(
            context.resources.getString(stringRes),
            if (value) context.resources.getString(R.string.yes_string) else context.resources.getString(
                R.string.no_string
            )
        )
    }


    fun openGooglePlayListing(packageName: String, context: Context) {
        val appPackage = packageName
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackage"))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")
            )
            context.startActivity(intent)
        }
    }

    suspend fun getBuildPropsList(): List<Pair<String, String?>> {
        val list: MutableList<Pair<String, String?>> = ArrayList()
        try {
            val process = Runtime.getRuntime().exec("getprop")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var i: Int
            val buffer = CharArray(4096)
            val sb = StringBuilder()
            while (reader.read(buffer).also { i = it } > 0) {
                val line = String(buffer, 0, i)
                sb.append(line)
            }
            val props = sb.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (propRaw in props) {
                val propRawSplitted = propRaw.split(": ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val key = propRawSplitted[0].substring(1, propRawSplitted[0].length - 1)
                val value =
                    if (propRawSplitted[1].length > 2) propRawSplitted[1].substring(
                        1,
                        propRawSplitted[1].length - 1
                    ) else null
                list.add(key.uppercase() to value?.uppercase())
            }
        } catch (e: Exception) {
            // This can happen if timeout triggers
            e.printStackTrace()
        }
        return list
    }

    fun getUIObjectsFromBuildProps(context: Context, props: List<Pair<String, String?>>) =
        props.map {
            it.second?.let { value ->
                it.first to value
            } ?: (it.first to context.resources.getString(R.string.not_available_info))
        }.map {
            UIObject(it.first, it.second)
        }

    fun openWalletAppPlayStore(context: Context) {
        val appPackage = "com.pacmac.mybudget"
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackage"))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")
            )
            context.startActivity(intent)
        }
    }

    fun launchPlayStore(context: Context) {
        val appPackage = context.packageName
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackage"))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")
            )
            context.startActivity(intent)
        }
    }

    fun hasVersionIncreased(
        installedVersion: String,
        serverAppVersionString: String
    ): UpToDateEnum {
        val installedVersions = installedVersion.split(".").map { it.toInt() }
        val serverVersions = serverAppVersionString.split(".").map { it.toInt() }

        for (i in serverVersions.indices) {
            if (installedVersions[i] < serverVersions[i]) {
                return UpToDateEnum.NO
            } else if (installedVersions[i] > serverVersions[i]) {
                return UpToDateEnum.YES
            }
        }
        return UpToDateEnum.YES
    }

    fun extractVersionNameFromHTML(input: String): String {
        val regex = """<p class="p1"><span class="s1">(\d+\.\d+\.\d+)</span></p>""".toRegex()
        val matchResult = regex.find(input)
        return matchResult?.groups?.get(1)?.value ?: "0.0.0"
    }

    fun hasGPS(context: Context): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
    }

    @Throws(java.lang.Exception::class)
    fun getDeviceProperty(key: String?): String? {
        var result = ""
        val systemPropClass = Class.forName("android.os.SystemProperties")
        val parameter: Array<Class<*>?> = arrayOfNulls(1)
        parameter[0] = String::class.java
        val getString = systemPropClass.getMethod("get", *parameter)
        val obParameter = arrayOfNulls<Any>(1)
        obParameter[0] = key
        val output: Any?
        if (getString != null) {
            output = getString.invoke(systemPropClass, *obParameter)
            if (output != null) {
                result = output.toString()
            }
        }
        return result
    }
}