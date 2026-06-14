package com.pacmac.devinfo.storage

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.pacmac.devinfo.R
import com.pacmac.devinfo.utils.Utils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.DecimalFormat
import java.util.Locale

object StorageUtils {

    const val EXPORT_FILE_NAME = "storage_info"

    const val TYPE_DATA = 0
    const val TYPE_INTERNAL_SD = 1
    const val TYPE_EXTERNAL_SD = 2

    data class ByteValue(val value: String, val unit: String)

    data class StorageSpace(val type: Int, val total: Long, val free: Long)

    fun getTotalMemory(context: Context): ByteValue {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return byteConvertor(memoryInfo.totalMem)
    }

    fun getAvailableMemory(context: Context): ByteValue {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return byteConvertor(memoryInfo.availMem)
    }

    fun getLowMemoryStatus(context: Context): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return if (memoryInfo.lowMemory) context.getString(R.string.yes_string)
        else context.getString(R.string.no_string)
    }

    fun getRAMHardware(): String? {
        return try {
            val memoryChip = Utils.getDeviceProperty("ro.boot.hardware.ddr") ?: return null
            val chipElements = memoryChip.split(",")
            String.format(Locale.ENGLISH, "%s %s - %s", chipElements[1], chipElements[2], chipElements[0])
        } catch (e: Exception) {
            Log.e("StorageUtils", "Failed to read RAM hardware info", e)
            null
        }
    }

    internal fun getDeviceStorage(context: Context): List<StorageSpace> {
        val listStorage = mutableListOf<StorageSpace>()

        try {
            listStorage.add(StorageSpace(TYPE_DATA,
                getTotalMemoryForPath(Environment.getDataDirectory().toString()),
                getFreeMemoryForPath(Environment.getDataDirectory().toString())))
        } catch (e: Exception) {
            Log.e("StorageUtils", "Failed to read data directory storage", e)
        }

        val listPaths = try {
            getSDPaths()
        } catch (e: Exception) {
            Log.e("StorageUtils", "Failed to read SD paths", e)
            emptyList()
        }

        try {
            if (!Environment.isExternalStorageEmulated()) {
                listStorage.add(StorageSpace(TYPE_INTERNAL_SD,
                    getTotalMemoryForPath(Environment.getExternalStorageDirectory().absolutePath),
                    getFreeMemoryForPath(Environment.getExternalStorageDirectory().absolutePath)))
            }
        } catch (e: Exception) {
            Log.e("StorageUtils", "Failed to read external storage", e)
        }

        for (path in listPaths) {
            try {
                listStorage.add(StorageSpace(TYPE_EXTERNAL_SD,
                    getTotalMemoryForPath(path),
                    getFreeMemoryForPath(path)))
            } catch (e: Exception) {
                Log.e("StorageUtils", "Failed to read storage path: $path", e)
            }
        }
        return listStorage
    }

    private fun getSDPaths(): List<String> {
        val sdPathList = mutableListOf<String>()
        val mountList = File("/proc/mounts")
        if (!mountList.exists()) return sdPathList

        BufferedReader(InputStreamReader(FileInputStream(mountList), "UTF-8")).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains(" /storage/")) {
                    val sdPath = line!!.split(" ")[1]
                    if (sdPath != Environment.getExternalStorageDirectory().absolutePath
                        && !sdPath.contains("emulated")
                        && !sdPath.contains("self")
                        && sdPath !in sdPathList
                    ) {
                        sdPathList.add(sdPath)
                    }
                }
            }
        }
        return sdPathList
    }

    private fun getTotalMemoryForPath(path: String): Long {
        if (!File(path).isDirectory) return 0L
        val statFs = StatFs(path)
        return statFs.blockCountLong * statFs.blockSizeLong
    }

    private fun getFreeMemoryForPath(path: String): Long {
        if (!File(path).isDirectory) return 0L
        val statFs = StatFs(path)
        return statFs.availableBlocksLong * statFs.blockSizeLong
    }

    fun getTypeString(context: Context, type: Int): String = when (type) {
        TYPE_DATA -> context.getString(R.string.storage_internal)
        TYPE_INTERNAL_SD -> context.getString(R.string.built_in_SD)
        TYPE_EXTERNAL_SD -> context.getString(R.string.device_ext_sdcard)
        else -> context.getString(R.string.unknown)
    }

    fun byteConvertor(size: Long): ByteValue {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024
        val tb = gb * 1024
        val pb = tb * 1024
        val eb = pb * 1024
        val df = DecimalFormat("#.##")
        return when {
            size < kb -> ByteValue(df.format(size), "Byte")
            size < mb -> ByteValue(df.format(size.toDouble() / kb), "KB")
            size < gb -> ByteValue(df.format(size.toDouble() / mb), "MB")
            size < tb -> ByteValue(df.format(size.toDouble() / gb), "GB")
            size < pb -> ByteValue(df.format(size.toDouble() / tb), "TB")
            size < eb -> ByteValue(df.format(size.toDouble() / pb), "PB")
            else -> ByteValue(df.format(size.toDouble() / eb), "EB")
        }
    }
}
