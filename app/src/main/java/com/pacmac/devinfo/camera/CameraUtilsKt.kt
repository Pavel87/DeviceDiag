package com.pacmac.devinfo.camera

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ResolutionUIObject
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.camera.model.CameraGeneral
import com.pacmac.devinfo.camera.model.CameraSpec
import com.pacmac.devinfo.camera.model.Resolution
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

        val position = if (spec.camPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            context.getString(R.string.camera_front_facing)
        } else context.getString(R.string.camera_rear_facing)
        uList.add(UIObject(context.getString(R.string.camera_position), position))
        uList.add(
            UIObject(
                context.getString(R.string.camera_vertical_view_angle),
                String.format(Locale.ENGLISH, "%.02f", spec.vertAngle),
                "°"
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_horizontal_view_angle), String.format(
                    Locale.ENGLISH, "%.02f", spec.horizontalAngle
                ), "°"
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_focal_length),
                String.format(Locale.ENGLISH, "%.02f", spec.focalLen),
                "mm"
            )
        )
        val sMinMaxEv = if (spec.minExposure != 0 || spec.maxExposure != 0) {
            spec.sMinMaxEv
        } else {
            context.resources.getString(R.string.not_available_info)
        }
        uList.add(
            UIObject(
                context.getString(R.string.camera_ev_min_max),
                String.format(Locale.ENGLISH, "%s", sMinMaxEv)
            )
        )
        if (spec.isZoomSupported) {
            uList.add(UIObject(context.getString(R.string.camera_max_zoom), spec.maxZoomRatio, "x"))
        } else {
            uList.add(
                UIObject(
                    context.getString(R.string.camera_max_zoom),
                    context.resources.getString(R.string.no_string)
                )
            )
        }

        uList.add(
            UIObject(
                context.getString(R.string.camera_smooth_zoom),
                if (spec.sSmoothZoom == ThreeState.YES) context.resources.getString(R.string.yes_string) else context.resources.getString(
                    R.string.no_string
                )
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_orientation),
                spec.camOrientation.toString(),
                "°"
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_face_detection),
                if (spec.faces != 0) spec.faces.toString() else context.resources.getString(R.string.not_supported),
                "max"
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_focus_area),
                spec.maxFocusAreas.toString(),
                "max"
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_video_snapshot),
                spec.isVideoSnapshotSupported,
                ListType.ICON
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_video_stabilization),
                spec.isVideoStabilizationSupported,
                ListType.ICON
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_auto_exposure),
                spec.isAutoExposureLockSupported,
                ListType.ICON
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_auto_white_balance),
                spec.isAutoWhiteBalanceLockSupported,
                ListType.ICON
            )
        )
        uList.add(
            UIObject(
                context.getString(R.string.camera_jpeg_quality),
                spec.jpegQuality.toString(),
                "%"
            )
        )

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
                    uList.add(
                        UIObject(
                            it.width.toString(),
                            it.height.toString()
                        )
                    )
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
                    uList.add(
                        UIObject(
                            it.width.toString(),
                            it.height.toString()
                        )
                    )
                }
            }
        }
        return uList
    }


    fun getMaxZoomRatio(parameters: Camera.Parameters): String {
        val zoomRatList = parameters.zoomRatios
        val zoom = zoomRatList[zoomRatList.size - 1]
        return String.format(Locale.ENGLISH, "%.1f", zoom / 100.0)
    }


    fun getPictureResolutions(parameters: Camera.Parameters): List<Resolution> {
        val picResList: ArrayList<Resolution> = arrayListOf()
        val picSizeList = parameters.supportedPictureSizes
        if (picSizeList != null) {
            for (size in picSizeList) {
                picResList.add(Resolution(size.width, size.height))
            }
        }
        return picResList
    }

    fun getVideoResolutions(parameters: Camera.Parameters): List<Resolution> {
        val videoResList: ArrayList<Resolution> = arrayListOf()
        val vidSizeList = parameters.supportedVideoSizes
        if (vidSizeList != null) {
            for (size in vidSizeList) {
                videoResList.add(Resolution(size.width, size.height))
            }
        }
        return videoResList
    }
}