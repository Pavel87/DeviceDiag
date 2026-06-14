package com.pacmac.devinfo.cpu

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "CPUViewModel"

@HiltViewModel
class CPUViewModelKt @Inject constructor() : ViewModel() {

    val EXPORT_FILE_NAME = "cpu_info"
    private val BOARD_PLATFORM = "ro.board.platform"

    private var isActive = false
    private val _cpuInfo = MutableStateFlow<List<UIObject>>(emptyList())
    val cpuInfo: StateFlow<List<UIObject>> = _cpuInfo.asStateFlow()

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
                        Log.e(TAG, "Failed to load CPU info", e)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun stopObserver() {
        isActive = false
    }

    fun getCpuInfoForExport(context: Context): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.title_activity_cpu_info), "", ListType.TITLE))
        add(UIObject(context.getString(R.string.cpu_param), context.getString(R.string.value), ListType.TITLE))
        addAll(cpuInfo.value)
    }

    suspend fun loadCPUInfo(context: Context) {
        val list = mutableListOf<UIObject>()
        list.addAll(readCPUinfo(context))
        list.add(UIObject(context.getString(R.string.cpu_core_available), Runtime.getRuntime().availableProcessors().toString()))
        list.add(UIObject(context.getString(R.string.cpu_core_active), getNumCores().toString()))
        list.addAll(getCPUFrequency(context))
        _cpuInfo.value = list
    }

    private fun readCPUinfo(context: Context): List<UIObject> {
        val list = mutableListOf<UIObject>()
        var processor: String? = null
        var chipset: String? = null
        var features: String? = null
        var architecture: String? = null
        var variant: String? = null
        var revision: String? = null
        var implementer: String? = null

        val fin = File("/proc/cpuinfo")
        val fis = try {
            FileInputStream(fin)
        } catch (e: IOException) {
            Log.e(TAG, "Cannot open /proc/cpuinfo", e)
            null
        }
        if (fis == null || !fin.exists() || !fin.canRead()) {
            Log.e(TAG, "Cannot access CPUINFO.")
            return list
        }

        try {
            BufferedReader(InputStreamReader(fis)).use { br ->
                br.forEachLine { line ->
                    fun valueAfterColon() = line.substring(line.indexOf(':') + 1).trim()
                    when {
                        line.startsWith("Processor\t:") -> processor = valueAfterColon()
                        line.startsWith("Features\t:") -> features = valueAfterColon()
                        line.startsWith("Hardware\t:") -> chipset = valueAfterColon()
                        line.startsWith("CPU architecture:") -> architecture = valueAfterColon()
                        line.startsWith("CPU variant\t:") -> variant = valueAfterColon()
                        line.startsWith("CPU revision\t:") -> revision = valueAfterColon()
                        line.startsWith("CPU implementer\t:") -> implementer = valueAfterColon()
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read /proc/cpuinfo", e)
        }

        processor?.let { list.add(UIObject(context.getString(R.string.cpu_processor), it)) }

        if (chipset != null) {
            list.add(UIObject(context.getString(R.string.cpu_chipset), chipset))
        } else {
            val resolved = try {
                Utils.getDeviceProperty(BOARD_PLATFORM)?.uppercase(Locale.getDefault())
                    ?.ifEmpty { Build.HARDWARE }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read board platform property", e)
                Build.HARDWARE
            }
            list.add(UIObject(context.getString(R.string.cpu_chipset), resolved))
        }

        if (features != null) {
            list.add(UIObject(context.getString(R.string.cpu_features), features))
            list.add(UIObject(context.getString(R.string.cpu_revision), revision))
            list.add(UIObject(context.getString(R.string.cpu_variant), variant))
            list.add(UIObject(context.getString(R.string.cpu_architecture), architecture))
            list.add(UIObject(context.getString(R.string.cpu_implementer), implementer))
        }
        return list
    }

    private fun getCPUFrequency(context: Context): List<UIObject> {
        val list = mutableListOf<UIObject>()

        fun readFreqFile(path: String): String? {
            val file = File(path)
            if (!file.exists()) return null
            return try {
                file.bufferedReader().use { br ->
                    br.readLine()?.toFloat()?.div(1_000_000f)?.let { "%.3f".format(it) }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to read freq file: $path", e)
                null
            }
        }

        readFreqFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")
            ?.let { list.add(UIObject(context.getString(R.string.cpu_current_freq), it, "GHz")) }

        readFreqFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
            ?.let { list.add(UIObject(context.getString(R.string.cpu_max_frequency), it, "GHz")) }

        return list
    }

    private fun getNumCores(): Int = try {
        File("/sys/devices/system/cpu/").listFiles { f ->
            Pattern.matches("cpu[0-9]+", f.name)
        }?.size ?: 1
    } catch (e: Exception) {
        Log.e(TAG, "Failed to count CPU cores", e)
        1
    }

    private suspend fun readUsage(): Float = try {
        val reader = RandomAccessFile("/proc/stat", "r")
        var load = reader.readLine()
        var toks = load.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val idle1 = toks[4].toLong()
        val cpu1 = toks[2].toLong() + toks[3].toLong() + toks[5].toLong() +
            toks[6].toLong() + toks[7].toLong() + toks[8].toLong()
        delay(360)
        reader.seek(0)
        load = reader.readLine()
        reader.close()
        toks = load.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val idle2 = toks[4].toLong()
        val cpu2 = toks[2].toLong() + toks[3].toLong() + toks[5].toLong() +
            toks[6].toLong() + toks[7].toLong() + toks[8].toLong()
        (cpu2 - cpu1).toFloat() / (cpu2 + idle2 - (cpu1 + idle1))
    } catch (e: IOException) {
        Log.e(TAG, "Failed to read CPU usage", e)
        0f
    }
}
