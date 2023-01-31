package com.pacmac.devinfo.gps

import android.content.Context
import android.os.Build
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.gps.models.GPSMainInfoModel
import com.pacmac.devinfo.gps.models.NMEALog
import com.pacmac.devinfo.gps.models.Satellite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {



    fun getNMEALofForExport(nmeaLog: List<NMEALog>): String {
        return nmeaLog.joinToString(
            separator = "\n",
            transform = { it.timeDate + " " + it.message })
    }

    fun getGPSUpdateTimeForExport(context: Context, time: String): UIObject {
        return UIObject(context.getString(R.string.gps_location_update_time), time)
    }

    fun getSatellitesForExport(context: Context, satellites: List<Satellite>): List<UIObject> {
        val list: ArrayList<UIObject> = ArrayList()
        if (satellites.isNotEmpty()) {
            list.add(UIObject("", "", ListType.TITLE))
            list.add(UIObject(context.getString(R.string.gps_satellites), "", ListType.TITLE))
            list.add(UIObject("ID", context.getString(R.string.gps_sat_header), ListType.TITLE))
            var i = 1
            for (satellite in satellites) {
                list.add(UIObject(i.toString(), satellite.toString(), ListType.TITLE))
                i++
            }
        }
        return list
    }

    fun getMainGPSInfoList(context: Context, model: GPSMainInfoModel): List<UIObject> {
        val list: ArrayList<UIObject> = ArrayList()
        list.add(
            UIObject(
                context.getString(R.string.gps_status),
                getStatusString(context, model.gpsStatus)
            )
        )

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1 && model.gnssYearOfHardware != -1) {
            list.add(
                UIObject(
                    context.getString(R.string.gnss_hardware_year),
                    model.gnssYearOfHardware.toString()
                )
            )
        }

        list.add(
            UIObject(
                context.resources.getString(R.string.gps_first_fix),
                if (model.firstFix != -1) getFirstFix(model.firstFix) else context.getString(R.string.gps_first_fix_acquiring),
                if (model.firstFix != -1) getFirstFixUnit(model.firstFix) else ""
            )
        )

        if (model.latitude != Double.MAX_VALUE) {
            list.add(
                UIObject(
                    context.getString(R.string.gps_latitude),
                    roundTo4decimals(model.latitude)
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_latitude), "--"))
        }
        if (model.longitude != Double.MAX_VALUE) {
            list.add(
                UIObject(
                    context.getString(R.string.gps_longitude), roundTo4decimals(model.longitude)
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_longitude), "--"))
        }

        if (model.altitude != Double.MAX_VALUE) {
            list.add(
                UIObject(
                    context.getString(R.string.gps_altitude),
                    roundTo2Decimals(model.altitude.toFloat())
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_altitude), "--"))
        }

        if (model.speed != Float.MAX_VALUE) {
            val speed = getSpeed(model.speed)
            list.add(
                UIObject(
                    context.getString(R.string.gps_speed),
                    speed.toString(),
                    if (speed.toString().isNotEmpty()) "km/h" else ""
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_speed), "--"))
        }


        if (model.accuracy != Float.MAX_VALUE) {
            val accuracy = roundTo1Decimal(model.accuracy)
            list.add(
                UIObject(
                    context.getString(R.string.gps_accuracy),
                    roundTo1Decimal(model.accuracy),
                    if (accuracy.isNotEmpty()) "m" else ""
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_accuracy), "--"))
        }

        if (model.bearing != Float.MAX_VALUE) {
            val bearing = roundTo2Decimals(model.bearing)
            list.add(
                UIObject(
                    context.getString(R.string.gps_bearing),
                    bearing,
                    if (bearing.isNotEmpty()) "m" else ""
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_bearing), "--"))
        }

        if (model.visibleSatellites != -1) {
            list.add(
                UIObject(
                    context.getString(R.string.gps_visible_satellites),
                    model.visibleSatellites.toString()
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_visible_satellites), "0"))
        }

        return list
    }

    private fun getStatusString(context: Context, status: Status): String {
        val resId = when (status) {
            Status.INACTIVE -> R.string.gps_inactive
            Status.STARTING -> R.string.gps_starting
            Status.FIRST_FIX -> R.string.gps_first_fix
            Status.ACTIVE -> R.string.gps_active
            else -> R.string.unknown
        }
        return context.getString(resId)
    }

    fun roundTo0Decimals(value: Float): String = String.format(Locale.ENGLISH, "%.0f", value)
    fun roundTo1Decimal(value: Float): String = String.format(Locale.ENGLISH, "%.01f", value)
    fun roundTo2Decimals(value: Float): String = String.format(Locale.ENGLISH, "%.02f", value)
    private fun getSpeed(value: Float): Int = (value * 3.6f).toInt()
    private fun roundTo4decimals(value: Double): String =
        String.format(Locale.ENGLISH, "%.04f", value)

    private fun getFirstFixUnit(firstFix: Int): String {
        return if (firstFix > 1000) {
            "s"
        } else "ms"
    }

    private fun getFirstFix(firstFix: Int): String? {
        return if (firstFix > 1000) {
            String.format(Locale.ENGLISH, "%.1f", firstFix / 1000.0)
        } else firstFix.toString()
    }

    fun formatTimeForNMEA(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss.SSS ", Locale.getDefault())
        return simpleDateFormat.format(Date(timestamp))
    }
}