package com.pacmac.devinfo.gps.models

import com.pacmac.devinfo.gps.Status

data class GPSMainInfoModel(
    val gpsStatus: Status,
    val firstFix: Int,
    val visibleSatellites: Int,
    val gnssYearOfHardware: Int,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val accuracy: Float,
    val bearing: Float,
    val verticalAccuracy: Float? = null,
    val speedAccuracy: Float? = null,
    val bearingAccuracy: Float? = null,
    val mslAltitude: Double? = null,
    val mslAltitudeAccuracy: Float? = null,
    val isMock: Boolean? = null,
    val gnssHardwareModelName: String? = null,
)
