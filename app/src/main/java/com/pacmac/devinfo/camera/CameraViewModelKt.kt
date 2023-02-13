package com.pacmac.devinfo.camera

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.camera.model.CameraGeneral
import com.pacmac.devinfo.camera.model.CameraSpec
import com.pacmac.devinfo.export.ExportTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModelKt @Inject constructor(private val packageManager: PackageManager) :
    ViewModel() {

    private val _cameraCount = mutableStateOf(0)
    val cameraCount: State<Int> = _cameraCount

    private val _cameraInfoGeneral = mutableStateOf(CameraGeneral())
    val cameraInfoGeneral: State<CameraGeneral> = _cameraInfoGeneral


    private val _cameraListData = mutableStateOf<List<CameraSpec>>(arrayListOf())
    val cameraListData: State<List<CameraSpec>> = _cameraListData

    init {
        viewModelScope.launch {
            loadCameraInfo()
            initializeCameras()
        }
    }

    // TODO explore ##CameraCharacteristics##
    private fun loadCameraInfo() {
        val camGeneral = CameraGeneral(
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_AUTOFOCUS
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_FLASH
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_FRONT
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_EXTERNAL
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_CAPABILITY_RAW
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_LEVEL_FULL
                )
            ) ThreeState.YES else ThreeState.NO,
            if (CameraUtilsKt.checkCameraFeature(
                    packageManager,
                    PackageManager.FEATURE_CAMERA_AR
                )
            ) ThreeState.YES else ThreeState.NO,
        )
        _cameraInfoGeneral.value = camGeneral
    }


    private suspend fun initializeCameras() {
        _cameraCount.value = Camera.getNumberOfCameras()
        val cameraList: ArrayList<CameraSpec> = arrayListOf()

        val count = _cameraCount.value

        for (i in 0 until count) {
            try {
                val camera = Camera.open(i)
                val params = camera.parameters
                val cameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(i, cameraInfo)
                camera.release()


                var maxZoomRatio: String = ""
                var sSmoothZoom = ThreeState.NO

                if (params.isZoomSupported()) {
                    maxZoomRatio = CameraUtilsKt.getMaxZoomRatio(params)
                    if (params.isSmoothZoomSupported()) sSmoothZoom = ThreeState.YES
                }

                val cameraSpec = CameraSpec(
                    picResolutions = CameraUtilsKt.getPictureResolutions(params),
                    videoResolutions = CameraUtilsKt.getVideoResolutions(params),
                    cameraInfo.orientation,
                    params.getVerticalViewAngle(),
                    params.getHorizontalViewAngle(),
                    params.getFocalLength(),
                    params.getExposureCompensationStep(),
                    Math.round(params.getExposureCompensationStep() * params.getMinExposureCompensation())
                        .toInt(),
                    Math.round(params.getExposureCompensationStep() * params.getMaxExposureCompensation())
                        .toInt(),
                    params.getJpegQuality(),
                    params.getMaxNumDetectedFaces(),
                    cameraInfo.facing,
                    params.getMaxNumFocusAreas(),
                    if (params.isVideoSnapshotSupported()) ThreeState.YES else ThreeState.NO,
                    if (params.isVideoStabilizationSupported()) ThreeState.YES else ThreeState.NO,
                    if (params.isAutoExposureLockSupported()) ThreeState.YES else ThreeState.NO,
                    if (params.isAutoWhiteBalanceLockSupported()) ThreeState.YES else ThreeState.NO,
                    params.isZoomSupported,
                    maxZoomRatio,
                    sSmoothZoom
                )

                cameraList.add(cameraSpec)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _cameraListData.value = cameraList
    }


    private var isExporting = false
    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    // REFACTOR EXPORT logic
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
