package com.pacmac.devinfo.gps.models

import com.pacmac.devinfo.gps.Status

data class GPSStatusModel(
    var gpsStatus: Status,
    var firstFixTime: Int = -1,
    var satelliteCount: Int = -1,
    var satellites: ArrayList<Satellite>? = null
)
