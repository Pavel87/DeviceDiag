package com.pacmac.devinfo.camera

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.camera.model.CameraGeneral
import com.pacmac.devinfo.camera.model.CameraSpec
import com.pacmac.devinfo.camera.model.Resolution
import com.pacmac.devinfo.export.ExportTask
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

private const val TAG = "CameraViewModel"

@HiltViewModel
class CameraViewModelKt @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManager: PackageManager
) : ViewModel() {

    private val _cameraCount = MutableStateFlow(0)
    val cameraCount: StateFlow<Int> = _cameraCount.asStateFlow()

    private val _cameraInfoGeneral = MutableStateFlow(CameraGeneral())
    val cameraInfoGeneral: StateFlow<CameraGeneral> = _cameraInfoGeneral.asStateFlow()

    private val _cameraListData = MutableStateFlow<List<CameraSpec>>(emptyList())
    val cameraListData: StateFlow<List<CameraSpec>> = _cameraListData.asStateFlow()

    init {
        viewModelScope.launch {
            loadCameraInfo()
            initializeCameras()
        }
    }

    private fun loadCameraInfo() {
        val camGeneral = CameraGeneral(
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_AUTOFOCUS)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_FLASH)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_FRONT)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_EXTERNAL)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_CAPABILITY_RAW)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_LEVEL_FULL)) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(packageManager, PackageManager.FEATURE_CAMERA_AR)) ThreeState.YES else ThreeState.NO,
        )
        _cameraInfoGeneral.value = camGeneral
    }

    private suspend fun initializeCameras() {
        withContext(Dispatchers.IO) {
            try {
                val cameraManager =
                    context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraIds = cameraManager.cameraIdList
                _cameraCount.value = cameraIds.size
                val cameraList = mutableListOf<CameraSpec>()

                for (cameraId in cameraIds) {
                    try {
                        val chars = cameraManager.getCameraCharacteristics(cameraId)
                        val spec = buildCameraSpec(cameraId, chars)
                        cameraList.add(spec)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to read characteristics for camera $cameraId", e)
                    }
                }
                _cameraListData.value = cameraList
            } catch (e: Exception) {
                Log.e(TAG, "Failed to access CameraManager", e)
            }
        }
    }

    private fun buildCameraSpec(cameraId: String, chars: CameraCharacteristics): CameraSpec {
        val facing = when (chars.get(CameraCharacteristics.LENS_FACING)) {
            CameraCharacteristics.LENS_FACING_FRONT -> context.getString(com.pacmac.devinfo.R.string.camera_facing_front)
            CameraCharacteristics.LENS_FACING_BACK -> context.getString(com.pacmac.devinfo.R.string.camera_facing_back)
            CameraCharacteristics.LENS_FACING_EXTERNAL -> context.getString(com.pacmac.devinfo.R.string.camera_facing_external)
            else -> "Unknown"
        }

        val hwLevel = when (chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)) {
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "LEGACY"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "LIMITED"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "FULL"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "LEVEL_3"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> "EXTERNAL"
            else -> "Unknown"
        }

        val sensorSize = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
        val sensorSizeMm = if (sensorSize != null) {
            String.format(Locale.ENGLISH, "%.2f x %.2f", sensorSize.width, sensorSize.height)
        } else {
            "N/A"
        }

        val pixelArray = chars.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
        val pixelArraySize = if (pixelArray != null) {
            "${pixelArray.width} x ${pixelArray.height}"
        } else {
            "N/A"
        }

        val megapixels = if (pixelArray != null) {
            String.format(
                Locale.ENGLISH,
                "%.1f",
                (pixelArray.width.toLong() * pixelArray.height.toLong()) / 1_000_000.0
            )
        } else {
            "N/A"
        }

        val apertureValues = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)
        val apertures = if (apertureValues != null && apertureValues.isNotEmpty()) {
            apertureValues.joinToString(", ") { String.format(Locale.ENGLISH, "f/%.1f", it) }
        } else {
            "N/A"
        }

        val focalLengthValues = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
        val focalLengths = if (focalLengthValues != null && focalLengthValues.isNotEmpty()) {
            focalLengthValues.joinToString(", ") {
                String.format(Locale.ENGLISH, "%.2f mm", it)
            }
        } else {
            "N/A"
        }

        val oisModes = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)
        val opticalStabilization = if (oisModes != null && oisModes.any {
                it == CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON
            }) {
            ThreeState.YES
        } else {
            ThreeState.NO
        }

        val eisModes = chars.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)
        val electronicStabilization = if (eisModes != null && eisModes.any {
                it == CameraMetadata.CONTROL_VIDEO_STABILIZATION_MODE_ON
            }) {
            ThreeState.YES
        } else {
            ThreeState.NO
        }

        val capabilities = chars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
        val rawSupport = if (capabilities != null && capabilities.contains(
                CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW
            )
        ) {
            ThreeState.YES
        } else {
            ThreeState.NO
        }

        val flashAvailable = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
        val flashSupported = if (flashAvailable == true) ThreeState.YES else ThreeState.NO

        val afModesArray = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
        val afModes = if (afModesArray != null && afModesArray.isNotEmpty()) {
            afModesArray.joinToString(", ") { mapAfMode(it) }
        } else {
            "N/A"
        }

        val aeRange = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)
        val aeCompensationRange = if (aeRange != null) {
            "${aeRange.lower} .. ${aeRange.upper}"
        } else {
            "N/A"
        }

        val aeStep = chars.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP)
        val aeCompensationStep = if (aeStep != null) {
            String.format(
                Locale.ENGLISH,
                "%.4f",
                aeStep.toFloat()
            )
        } else {
            "N/A"
        }

        val maxZoom = chars.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)
        val maxDigitalZoom = if (maxZoom != null) {
            String.format(Locale.ENGLISH, "%.1f", maxZoom)
        } else {
            "N/A"
        }

        val physicalIds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val ids = chars.physicalCameraIds
            if (ids.isNotEmpty()) ids.joinToString(", ") else "N/A"
        } else {
            "N/A"
        }

        val configMap = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

        val outputFormats = if (configMap != null) {
            configMap.outputFormats
                ?.map { formatToString(it) }
                ?.joinToString(", ") ?: "N/A"
        } else {
            "N/A"
        }

        val picResolutions = if (configMap != null) {
            try {
                val sizes = configMap.getOutputSizes(ImageFormat.JPEG)
                sizes?.map { Resolution(it.width, it.height) } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        val videoResolutions = if (configMap != null) {
            try {
                val sizes = configMap.getOutputSizes(MediaRecorder::class.java)
                sizes?.map { Resolution(it.width, it.height) } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        // API 36+ features
        val aePriorityMode = if (Build.VERSION.SDK_INT >= 36) {
            try {
                val modes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_PRIORITY_MODES)
                if (modes != null && modes.isNotEmpty()) {
                    modes.joinToString(", ") { mapAePriorityMode(it) }
                } else null
            } catch (e: Exception) { null }
        } else null

        val colorTempControl = if (Build.VERSION.SDK_INT >= 36) {
            try {
                val modes = chars.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_MODES)
                // CCT mode value = 3 on API 36+
                if (modes != null && modes.contains(3)) ThreeState.YES else ThreeState.NO
            } catch (e: Exception) { null }
        } else null

        val nightModeIndicator = if (Build.VERSION.SDK_INT >= 36) {
            try {
                val field = CameraCharacteristics::class.java.getField("EXTENSION_NIGHT_MODE_INDICATOR_SUPPORTED")
                @Suppress("UNCHECKED_CAST")
                val key = field.get(null) as? CameraCharacteristics.Key<Boolean>
                val supported = key?.let { chars.get(it) }
                if (supported == true) ThreeState.YES else ThreeState.NO
            } catch (e: Exception) { null }
        } else null

        val heicUltraHdr = if (Build.VERSION.SDK_INT >= 36) {
            try {
                val formats = configMap?.outputFormats
                // HEIC_ULTRAHDR = 0x41 or similar constant
                if (formats != null && formats.contains(ImageFormat.HEIC_ULTRAHDR)) {
                    ThreeState.YES
                } else {
                    ThreeState.NO
                }
            } catch (e: Exception) { null }
        } else null

        // API 37+ features
        val raw14Support = if (Build.VERSION.SDK_INT >= 37) {
            try {
                val formats = configMap?.outputFormats
                if (formats != null && formats.contains(ImageFormat.RAW14)) {
                    ThreeState.YES
                } else {
                    ThreeState.NO
                }
            } catch (e: Exception) { null }
        } else null

        val deviceType = if (Build.VERSION.SDK_INT >= 37) {
            try {
                val type = chars.get(CameraCharacteristics.INFO_DEVICE_TYPE)
                when (type) {
                    0 -> context.getString(com.pacmac.devinfo.R.string.camera_type_builtin)
                    1 -> context.getString(com.pacmac.devinfo.R.string.camera_type_external)
                    2 -> context.getString(com.pacmac.devinfo.R.string.camera_type_virtual)
                    else -> if (type != null) "Unknown ($type)" else null
                }
            } catch (e: Exception) { null }
        } else null

        return CameraSpec(
            cameraId = cameraId,
            facing = facing,
            hardwareLevel = hwLevel,
            sensorSizeMm = sensorSizeMm,
            megapixels = megapixels,
            pixelArraySize = pixelArraySize,
            apertures = apertures,
            focalLengths = focalLengths,
            opticalStabilization = opticalStabilization,
            electronicStabilization = electronicStabilization,
            rawSupport = rawSupport,
            flashSupported = flashSupported,
            afModes = afModes,
            aeCompensationRange = aeCompensationRange,
            aeCompensationStep = aeCompensationStep,
            maxDigitalZoom = maxDigitalZoom,
            physicalCameraIds = physicalIds,
            outputFormats = outputFormats,
            picResolutions = picResolutions,
            videoResolutions = videoResolutions,
            aePriorityMode = aePriorityMode,
            colorTempControl = colorTempControl,
            nightModeIndicator = nightModeIndicator,
            heicUltraHdr = heicUltraHdr,
            raw14Support = raw14Support,
            deviceType = deviceType,
        )
    }

    private fun mapAfMode(mode: Int): String = when (mode) {
        CameraMetadata.CONTROL_AF_MODE_OFF -> "OFF"
        CameraMetadata.CONTROL_AF_MODE_AUTO -> "AUTO"
        CameraMetadata.CONTROL_AF_MODE_MACRO -> "MACRO"
        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO -> "CONTINUOUS_VIDEO"
        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE -> "CONTINUOUS_PICTURE"
        CameraMetadata.CONTROL_AF_MODE_EDOF -> "EDOF"
        else -> "UNKNOWN($mode)"
    }

    private fun mapAePriorityMode(mode: Int): String = when (mode) {
        0 -> "OFF"
        1 -> "SENSOR_EXPOSURE_TIME"
        2 -> "SENSOR_SENSITIVITY"
        else -> "UNKNOWN($mode)"
    }

    private fun formatToString(format: Int): String = when (format) {
        ImageFormat.JPEG -> "JPEG"
        ImageFormat.YUV_420_888 -> "YUV_420_888"
        ImageFormat.RAW_SENSOR -> "RAW_SENSOR"
        ImageFormat.RAW10 -> "RAW10"
        ImageFormat.RAW12 -> "RAW12"
        ImageFormat.RAW_PRIVATE -> "RAW_PRIVATE"
        ImageFormat.PRIVATE -> "PRIVATE"
        ImageFormat.NV21 -> "NV21"
        ImageFormat.YV12 -> "YV12"
        ImageFormat.DEPTH16 -> "DEPTH16"
        ImageFormat.DEPTH_POINT_CLOUD -> "DEPTH_POINT_CLOUD"
        ImageFormat.HEIC -> "HEIC"
        else -> "0x${String.format(Locale.ENGLISH, "%X", format)}"
    }

    private var isExporting = false
    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    fun export(context: Context) {
        if (!isExporting) {
            isExporting = true
            ExportTask(context, CameraUtilsKt.EXPORT_FILE_NAME) {
                viewModelScope.launch {
                    isExporting = false
                    _onExportDone.emit(it)
                }
            }.execute(this)
        }
    }
}
