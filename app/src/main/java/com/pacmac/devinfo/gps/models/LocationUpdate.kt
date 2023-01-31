package com.pacmac.devinfo.gps.models

import java.util.Calendar
import java.util.Locale

data class LocationUpdate(
    val updateTime: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val accuracy: Float,
    val bearing: Float,
) {
    fun getUpdateTime(): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = updateTime

        val hour = cal[Calendar.HOUR_OF_DAY]
        val minute = cal[Calendar.MINUTE]
        val second = cal[Calendar.SECOND]
        return String.format(
            Locale.ENGLISH,
            "%d:%02d:%02d",
            hour,
            minute,
            second
        )
    }
}
