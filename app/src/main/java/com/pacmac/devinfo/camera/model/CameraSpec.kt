package com.pacmac.devinfo.camera.model

import com.pacmac.devinfo.ThreeState

data class CameraSpec(
    val picResolutions: List<Resolution>,
    val videoResolutions: List<Resolution>,
    val camOrientation: Int,
    val vertAngle: Float,
    val horizontalAngle: Float,
    val focalLen: Float,
    val step: Float,
    val minExposure: Int,
    val maxExposure: Int,
    val jpegQuality: Int,
    val faces: Int,
    val camPosition: Int,
    val maxFocusAreas: Int,
    val isVideoSnapshotSupported: ThreeState = ThreeState.MAYBE,
    val isVideoStabilizationSupported: ThreeState = ThreeState.MAYBE,
    val isAutoExposureLockSupported: ThreeState = ThreeState.MAYBE,
    val isAutoWhiteBalanceLockSupported: ThreeState = ThreeState.MAYBE,
    val isZoomSupported:Boolean,
    val maxZoomRatio: String,
    val sSmoothZoom: ThreeState = ThreeState.NO,
    val sMinMaxEv: String = "$minExposure/$maxExposure",
)
