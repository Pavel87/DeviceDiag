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
)