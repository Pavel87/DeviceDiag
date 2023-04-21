package com.pacmac.devinfo.cpu

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CPUViewModelKt @Inject constructor() : ViewModel() {

    val EXPORT_FILE_NAME = "cpu_info"
    private val BOARD_PLATFORM = "ro.board.platform"

    private var isActive = false

    private val cpuInfo = mutableStateOf<List<UIObject>>(arrayListOf())

    fun getCPUInfo(): State<List<UIObject>> {
        return cpuInfo
    }

    private fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (isActive) {
            emit(Unit)
            delay(period)
        }
    }

    fun observeCPUInfo(context: Context) {
        if (isActive) return
        isActive = true
        tickerFlow(3.seconds)
            .onEach {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        loadCPUInfo(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun stopObserver() {
        isActive = false
    }

    fun getCpuInfoForExport(context: Context): List<UIObject> {
        val list: MutableList<UIObject> = ArrayList()
        list.add(
            UIObject(
                context.getString(R.string.title_activity_cpu_info),
                "",
                ListType.TITLE
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.cpu_param),
                context.getString(R.string.value),
                ListType.TITLE
            )
        )
        list.addAll(cpuInfo.value)
        return list
    }

    suspend fun loadCPUInfo(context: Context) {
        val list: MutableList<UIObject> = ArrayList()
        list.addAll(readCPUinfo(context)!!)
        list.add(
            UIObject(
                context.getString(R.string.cpu_core_available),
                Runtime.getRuntime().availableProcessors().toString()
            )
        )
        list.add(UIObject(context.getString(R.string.cpu_core_active), getNumCores().toString()))
        list.addAll(getCPUFrequency(context)!!)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            String.format(Locale.ENGLISH, "%.2f", readUsage() * 100)
        }
        cpuInfo.value = list
    }


    private fun readCPUinfo(context: Context): List<UIObject>? {
        val list: MutableList<UIObject> = ArrayList()
        var processor: String? = null
        var chipset: String? = null
        var features: String? = null
        var architecture: String? = null
        var variant: String? = null
        var revision: String? = null
        var implementer: String? = null

        //use to get current directory
        val fin = File("/proc/cpuinfo")
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(fin)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (fis == null || !fin.exists() || !fin.canRead()) {
            Log.e("CPUInfo", "Cannot access CPUINFO.")
            return list
        }
        //Construct BufferedReader from InputStreamReader
        val br = BufferedReader(InputStreamReader(fis))
        var line: String? = null
        try {
            while (br.readLine().also { line = it } != null) {

//                Log.d("PACMAC", line);
                if (line!!.startsWith("Processor\t:")) {
                    val i = line!!.indexOf(":")
                    processor = line!!.substring(i + 1).trim { it <= ' ' }
                } else if (line!!.startsWith("Features\t:")) {
                    val i = line!!.indexOf(":")
                    features = line!!.substring(i + 1).trim { it <= ' ' }
                } else if (line!!.startsWith("Hardware\t:")) {
                    val i = line!!.indexOf(":")
                    chipset = line!!.substring(i + 1).trim { it <= ' ' }
                } else if (line!!.startsWith("CPU architecture:")) {
                    val i = line!!.indexOf(":")
                    architecture = line!!.substring(i + 1).trim { it <= ' ' }
                } else if (line!!.startsWith("CPU variant\t:")) {
                    val i = line!!.indexOf(":")
                    variant = line!!.substring(i + 1).trim { it <= ' ' }
                } else if (line!!.startsWith("CPU revision\t:")) {
                    val i = line!!.indexOf(":")
                    revision = line!!.substring(i + 1).trim { it <= ' ' }
                } else if (line!!.startsWith("CPU implementer\t:")) {
                    val i = line!!.indexOf(":")
                    implementer = line!!.substring(i + 1).trim { it <= ' ' }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (processor != null) {
            list.add(UIObject(context.getString(R.string.cpu_processor), processor))
        }
        if (chipset != null) {
            list.add(UIObject(context.getString(R.string.cpu_chipset), chipset))
        } else {
            try {
                chipset = Utils.getDeviceProperty(BOARD_PLATFORM)?.uppercase(Locale.getDefault())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (chipset?.length == 0) {
                chipset = Build.HARDWARE
            }
            list.add(UIObject(context.getString(R.string.cpu_chipset), chipset))
        }
        if (features != null) {
            list.add(UIObject(context.getString(R.string.cpu_features), features))
        }
        if (features != null) {
            list.add(UIObject(context.getString(R.string.cpu_revision), revision))
        }
        if (features != null) {
            list.add(UIObject(context.getString(R.string.cpu_variant), variant))
        }
        if (features != null) {
            list.add(UIObject(context.getString(R.string.cpu_architecture), architecture))
        }
        if (features != null) {
            list.add(UIObject(context.getString(R.string.cpu_implementer), implementer))
        }
        return list
    }


    private fun getCPUFrequency(context: Context): List<UIObject>? {
        val list: MutableList<UIObject> = ArrayList()
        var maxFrequency: String? = null
        var currentFrequency: String? = null

        //read max freq CPU
        var fin = File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
        var fis: FileInputStream? = null
        var line: String? = null
        var br: BufferedReader
        var temp = 0f
        if (fin.exists()) {
            try {
                fis = FileInputStream(fin)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            if (fis != null) {
                //Construct BufferedReader from InputStreamReader
                br = BufferedReader(InputStreamReader(fis))
                try {
                    while (br.readLine().also { line = it } != null) {
                        temp = line!!.toFloat() / 1000000
                        maxFrequency = "" + temp
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        /// read the current CPU freq

        //use to get current directory
        fin = File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")
        if (fin.exists()) {
            fis = null
            try {
                fis = FileInputStream(fin)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            if (fis != null) {
                //Construct BufferedReader from InputStreamReader
                br = BufferedReader(InputStreamReader(fis))
                try {
                    while (br.readLine().also { line = it } != null) {
                        temp = line!!.toFloat() / 1000000
                        currentFrequency = "" + String.format("%.3f", temp)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        if (currentFrequency != null) {
            list.add(
                UIObject(
                    context.getString(R.string.cpu_current_freq),
                    currentFrequency,
                    "GHz"
                )
            )
        }
        if (maxFrequency != null) {
            list.add(UIObject(context.getString(R.string.cpu_max_frequency), maxFrequency, "GHz"))
        }
        return list
    }


    private fun getNumCores(): Int {

        //Private Class to display only CPU devices in the directory listing
        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean {
                return if (Pattern.matches("cpu[0-9]+", pathname.name)) {
                    true
                } else false
            }
        }
        return try {
            //Get directory containing CPU info
            val dir = File("/sys/devices/system/cpu/")
            val files = dir.listFiles(CpuFilter())
            files.size
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }

    private fun readUsage(): Float {
        try {
            val reader = RandomAccessFile("/proc/stat", "r")
            var load = reader.readLine()
            var toks = load.split(" +".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray() // Split on one or more spaces
            val idle1 = toks[4].toLong()
            val cpu1 =
                toks[2].toLong() + toks[3].toLong() + toks[5].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()
            try {
                Thread.sleep(360)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            reader.seek(0)
            load = reader.readLine()
            reader.close()
            toks = load.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val idle2 = toks[4].toLong()
            val cpu2 =
                toks[2].toLong() + toks[3].toLong() + toks[5].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()
            return (cpu2 - cpu1).toFloat() / (cpu2 + idle2 - (cpu1 + idle1))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return 0f
    }
}