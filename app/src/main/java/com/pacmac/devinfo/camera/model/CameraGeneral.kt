package com.pacmac.devinfo.camera.model

import com.pacmac.devinfo.ThreeState

data class CameraGeneral(
    val autoFocus: ThreeState = ThreeState.MAYBE,
    val hasFlash: ThreeState = ThreeState.MAYBE,
    val hasFrontFacingCamera: ThreeState = ThreeState.MAYBE,
    val supportsExternalCamera: ThreeState = ThreeState.MAYBE,
    val hasManualPostProcessing: ThreeState = ThreeState.MAYBE,
    val hasManualSensor: ThreeState = ThreeState.MAYBE,
    val hasCapabilityRaw: ThreeState = ThreeState.MAYBE,
    val hasFullHWCapabilityLevel: ThreeState = ThreeState.MAYBE,
    val supportsAR: ThreeState = ThreeState.MAYBE,
)
