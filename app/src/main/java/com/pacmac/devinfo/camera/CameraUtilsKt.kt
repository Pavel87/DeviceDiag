package com.pacmac.devinfo.camera

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ResolutionUIObject
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.camera.model.CameraGeneral
import com.pacmac.devinfo.camera.model.CameraSpec
import java.util.Locale

object CameraUtilsKt {

    val EXPORT_FILE_NAME = "camera_info"

    fun getFormattedGeneralInfo(
        context: Context,
        cameraGeneral: CameraGeneral,
        isForExport: Boolean = false
    ): List<UIObject> {
        val list: ArrayList<UIObject> = arrayListOf()

        if (isForExport) {
            list.add(
                UIObject(
                    context.getString(R.string.title_activity_camera_info),
                    "",
                    ListType.TITLE
                )
            )
            list.add(UIObject("", "", ListType.TITLE))

            list.add(UIObject(context.getString(R.string.camera_general_info), "", ListType.TITLE))
            list.add(
                UIObject(
                    context.getString(R.string.param),
                    context.getString(R.string.value),
                    ListType.TITLE
                )
            )
        }

        list.add(
            UIObject(
                context.getString(R.string.camera_autofocus),
                cameraGeneral.autoFocus,
                ListType.ICON
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.camera_flash),
                cameraGeneral.hasFlash,
                ListType.ICON
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.camera_front_facing_feature),
                cameraGeneral.hasFrontFacingCamera,
                ListType.ICON
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.camera_external_support),
                cameraGeneral.supportsExternalCamera,
                ListType.ICON
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.camera_post_processing),
                cameraGeneral.hasManualPostProcessing,
                ListType.ICON
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.camera_manual_sensor),
                cameraGeneral.hasManualSensor,
                ListType.ICON
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.camera_capability_raw),
                cameraGeneral.hasCapabilityRaw,
                ListType.ICON
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.camera_full_hw_capability_level),
                cameraGeneral.hasFullHWCapabilityLevel,
                ListType.ICON
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            list.add(
                UIObject(
                    context.getString(R.string.camera_ar_support),
                    cameraGeneral.supportsAR,
                    ListType.ICON
                )
            )
        }
        return list
    }

    fun checkCameraFeature(packageManager: PackageManager, feature: String): Boolean {
        return packageManager.hasSystemFeature(feature)
    }

    fun getCameraSpecParams(
        context: Context,
        spec: CameraSpec,
        isForExport: Boolean = false,
        cameraID: Int = -1
    ): List<UIObject> {
        val uList: ArrayList<UIObject> = arrayListOf()

        if (isForExport) {
            uList.add(UIObject("", "", ListType.TITLE))
            uList.add(
                UIObject(
                    String.format(
                        Locale.ENGLISH,
                        context.getString(R.string.camera_id_title),
                        cameraID + 1
                    ), "", ListType.TITLE
                )
            )
            uList.add(
                UIObject(
                    context.getString(R.string.param),
                    context.getString(R.string.value),
                    context.getString(R.string.unit)
                )
            )
        }

        // Facing
        uList.add(UIObject(context.getString(R.string.camera_facing), spec.facing))

        // Hardware Level
        uList.add(UIObject(context.getString(R.string.camera_hw_level), spec.hardwareLevel))

        // Sensor Size
        uList.add(
            UIObject(
                context.getString(R.string.camera_sensor_size),
                spec.sensorSizeMm,
                "mm"
            )
        )

        // Megapixels
        uList.add(
            UIObject(
                context.getString(R.string.camera_megapixels),
                spec.megapixels,
                "MP"
            )
        )

        // Sensor Pixel Array
        uList.add(
            UIObject(
                context.getString(R.string.camera_sensor_pixel_array),
                spec.pixelArraySize
            )
        )

        // Apertures
        uList.add(UIObject(context.getString(R.string.camera_apertures), spec.apertures))

        // Focal Lengths
        uList.add(UIObject(context.getString(R.string.camera_focal_lengths), spec.focalLengths))

        // OIS
        uList.add(
            UIObject(
                context.getString(R.string.camera_ois),
                spec.opticalStabilization,
                ListType.ICON
            )
        )

        // EIS
        uList.add(
            UIObject(
                context.getString(R.string.camera_eis),
                spec.electronicStabilization,
                ListType.ICON
            )
        )

        // RAW support
        uList.add(
            UIObject(
                context.getString(R.string.camera_raw_support),
                spec.rawSupport,
                ListType.ICON
            )
        )

        // Flash
        uList.add(
            UIObject(
                context.getString(R.string.camera_flash_supported),
                spec.flashSupported,
                ListType.ICON
            )
        )

        // AF Modes
        uList.add(UIObject(context.getString(R.string.camera_af_modes), spec.afModes))

        // AE Compensation Range
        uList.add(UIObject(context.getString(R.string.camera_ae_range), spec.aeCompensationRange))

        // AE Compensation Step
        uList.add(UIObject(context.getString(R.string.camera_ae_step), spec.aeCompensationStep))

        // Max Digital Zoom
        uList.add(
            UIObject(
                context.getString(R.string.camera_max_digital_zoom),
                spec.maxDigitalZoom,
                "x"
            )
        )

        // Physical Camera IDs (logical multi-camera)
        uList.add(
            UIObject(context.getString(R.string.camera_physical_cameras), spec.physicalCameraIds)
        )

        // Output Formats
        uList.add(
            UIObject(context.getString(R.string.camera_output_formats), spec.outputFormats)
        )

        // API 36+ Camera features
        spec.aePriorityMode?.let {
            uList.add(UIObject(context.getString(R.string.camera_ae_priority_mode), it))
        }
        spec.colorTempControl?.let {
            uList.add(UIObject(context.getString(R.string.camera_color_temp_control), it, ListType.ICON))
        }
        spec.nightModeIndicator?.let {
            uList.add(UIObject(context.getString(R.string.camera_night_mode_indicator), it, ListType.ICON))
        }
        spec.heicUltraHdr?.let {
            uList.add(UIObject(context.getString(R.string.camera_heic_ultrahdr), it, ListType.ICON))
        }

        // API 37+ Camera features
        spec.raw14Support?.let {
            uList.add(UIObject(context.getString(R.string.camera_raw14), it, ListType.ICON))
        }
        spec.deviceType?.let {
            uList.add(UIObject(context.getString(R.string.camera_device_type), it))
        }

        // Resolutions
        if (isForExport.not()) {
            uList.add(
                ResolutionUIObject(
                    context.getString(R.string.camera_supported_image_size),
                    spec.picResolutions
                )
            )
            uList.add(
                ResolutionUIObject(
                    context.getString(R.string.camera_supported_video_size),
                    spec.videoResolutions
                )
            )
        } else {
            if (spec.picResolutions.isNotEmpty()) {
                uList.add(
                    UIObject(
                        context.getString(R.string.camera_supported_image_size),
                        "",
                        ListType.TITLE
                    )
                )
                uList.add(
                    UIObject(
                        context.getString(R.string.width),
                        context.getString(R.string.height),
                        ListType.TITLE
                    )
                )
                spec.picResolutions.forEach {
                    uList.add(UIObject(it.width.toString(), it.height.toString()))
                }
            }
            if (spec.videoResolutions.isNotEmpty()) {
                uList.add(
                    UIObject(
                        context.getString(R.string.camera_supported_video_size),
                        "",
                        ListType.TITLE
                    )
                )
                uList.add(
                    UIObject(
                        context.getString(R.string.width),
                        context.getString(R.string.height),
                        ListType.TITLE
                    )
                )
                spec.videoResolutions.forEach {
                    uList.add(UIObject(it.width.toString(), it.height.toString()))
                }
            }
        }
        return uList
    }
}
