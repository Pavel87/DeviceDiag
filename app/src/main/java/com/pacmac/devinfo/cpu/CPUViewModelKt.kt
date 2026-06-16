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
        list.add(getCpuHeadroom(context))
        list.addAll(getPerCoreInfo(context))
        _cpuInfo.value = list
    }

    @android.annotation.SuppressLint("WrongConstant")
    private fun getCpuHeadroom(context: Context): UIObject {
        if (Build.VERSION.SDK_INT >= 36) {
            return try {
                val perfHintManager = context.getSystemService(Context.PERFORMANCE_HINT_SERVICE)
                        as? android.os.PerformanceHintManager
                if (perfHintManager != null) {
                    val method = perfHintManager.javaClass.getMethod("getCpuHeadroom")
                    val headroom = method.invoke(perfHintManager) as Float
                    if (headroom.isNaN() || headroom < 0) {
                        UIObject(context.getString(R.string.cpu_headroom), "N/A")
                    } else {
                        UIObject(
                            context.getString(R.string.cpu_headroom),
                            String.format(Locale.ENGLISH, "%.1f%%", headroom * 100)
                        )
                    }
                } else {
                    UIObject(context.getString(R.string.cpu_headroom), "N/A")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get CPU headroom", e)
                UIObject(context.getString(R.string.cpu_headroom), "N/A")
            }
        }
        return UIObject(context.getString(R.string.cpu_headroom), "N/A")
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

    /**
     * Reads a single line from a sysfs file. Returns null if the file does not exist or cannot be read.
     */
    private fun readSysfsLine(path: String): String? {
        val file = File(path)
        if (!file.exists()) return null
        return try {
            file.bufferedReader().use { it.readLine()?.trim() }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read sysfs file: $path", e)
            null
        }
    }

    /**
     * Reads a frequency value from sysfs (in kHz) and converts to GHz string formatted to 3 decimals.
     */
    private fun readFreqFileGHz(path: String): String? {
        return readSysfsLine(path)?.toFloatOrNull()?.div(1_000_000f)?.let { "%.3f".format(it) }
    }

    /**
     * Checks whether a given CPU core is online.
     * cpu0 is always online and has no 'online' file on most kernels.
     */
    private fun isCoreOnline(coreIndex: Int): Boolean {
        if (coreIndex == 0) return true
        val value = readSysfsLine("/sys/devices/system/cpu/cpu$coreIndex/online")
        return value == "1"
    }

    /**
     * Detects cluster topology by grouping cores by their max frequency.
     * Returns a human-readable string like "2x2.840GHz + 2x2.420GHz + 4x1.800GHz".
     */
    private fun detectClusterTopology(numCores: Int): String? {
        val maxFreqs = mutableListOf<Long>()
        for (i in 0 until numCores) {
            val freq = readSysfsLine("/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_max_freq")
                ?.toLongOrNull()
            maxFreqs.add(freq ?: -1L)
        }

        // Filter out cores where we couldn't read the frequency
        val validFreqs = maxFreqs.filter { it > 0 }
        if (validFreqs.isEmpty()) return null

        // Group by frequency and sort descending (big cores first)
        val groups = validFreqs.groupBy { it }
            .entries
            .sortedByDescending { it.key }

        val topologyParts = groups.map { (freqKHz, cores) ->
            val ghz = freqKHz / 1_000_000f
            "${cores.size}x${"%.2f".format(ghz)}GHz"
        }

        return topologyParts.joinToString(" + ")
    }

    /**
     * Builds per-core information: cluster topology and per-core freq/governor/status details.
     */
    private fun getPerCoreInfo(context: Context): List<UIObject> {
        val list = mutableListOf<UIObject>()
        val numCores = getNumCores()

        // Section header
        list.add(UIObject(context.getString(R.string.cpu_per_core_info), "", ListType.TITLE))

        // Cluster topology
        detectClusterTopology(numCores)?.let { topology ->
            list.add(UIObject(context.getString(R.string.cpu_cluster_topology), topology))
        }

        // Per-core details
        for (i in 0 until numCores) {
            list.add(
                UIObject(
                    String.format(context.getString(R.string.cpu_core_label), i),
                    "",
                    ListType.TITLE
                )
            )

            val online = isCoreOnline(i)
            list.add(
                UIObject(
                    context.getString(R.string.cpu_core_status),
                    context.getString(if (online) R.string.cpu_core_online else R.string.cpu_core_offline)
                )
            )

            if (online) {
                val basePath = "/sys/devices/system/cpu/cpu$i/cpufreq"

                readFreqFileGHz("$basePath/scaling_cur_freq")?.let {
                    list.add(UIObject(context.getString(R.string.cpu_core_freq), it, "GHz"))
                }

                readFreqFileGHz("$basePath/cpuinfo_min_freq")?.let {
                    list.add(UIObject(context.getString(R.string.cpu_core_min_freq), it, "GHz"))
                }

                readFreqFileGHz("$basePath/cpuinfo_max_freq")?.let {
                    list.add(UIObject(context.getString(R.string.cpu_core_max_freq), it, "GHz"))
                }

                readSysfsLine("$basePath/scaling_governor")?.let {
                    list.add(UIObject(context.getString(R.string.cpu_core_governor), it))
                }
            }
        }

        return list
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
