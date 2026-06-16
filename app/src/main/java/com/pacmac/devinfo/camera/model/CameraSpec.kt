package com.pacmac.devinfo.camera.model

import com.pacmac.devinfo.ThreeState

data class CameraSpec(
    val cameraId: String,
    val facing: String,
    val hardwareLevel: String,
    val sensorSizeMm: String, // e.g. "5.64 x 4.23"
    val megapixels: String, // e.g. "12.2"
    val pixelArraySize: String, // e.g. "4032 x 3024"
    val apertures: String, // e.g. "f/1.8, f/2.4"
    val focalLengths: String, // e.g. "4.44, 7.10"
    val opticalStabilization: ThreeState = ThreeState.NO,
    val electronicStabilization: ThreeState = ThreeState.NO,
    val rawSupport: ThreeState = ThreeState.NO,
    val flashSupported: ThreeState = ThreeState.NO,
    val afModes: String,
    val aeCompensationRange: String,
    val aeCompensationStep: String,
    val maxDigitalZoom: String,
    val physicalCameraIds: String, // comma-separated for logical multi-camera
    val outputFormats: String,
    val picResolutions: List<Resolution>,
    val videoResolutions: List<Resolution>,
    // API 36+
    val aePriorityMode: String? = null,
    val colorTempControl: ThreeState? = null,
    val nightModeIndicator: ThreeState? = null,
    val heicUltraHdr: ThreeState? = null,
    // API 37+
    val raw14Support: ThreeState? = null,
    val deviceType: String? = null,
)
