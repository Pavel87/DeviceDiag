package com.pacmac.devinfo.gps

import android.content.Context
import android.location.GnssCapabilities
import android.os.Build
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
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

        model.gnssHardwareModelName?.let {
            list.add(
                UIObject(
                    context.getString(R.string.gnss_hardware_model),
                    it
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
                    if (bearing.isNotEmpty()) "°" else ""
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.gps_bearing), "--"))
        }

        model.verticalAccuracy?.let {
            list.add(
                UIObject(
                    context.getString(R.string.gps_vertical_accuracy),
                    roundTo1Decimal(it),
                    "m"
                )
            )
        }

        model.speedAccuracy?.let {
            list.add(
                UIObject(
                    context.getString(R.string.gps_speed_accuracy),
                    roundTo1Decimal(it),
                    "m/s"
                )
            )
        }

        model.bearingAccuracy?.let {
            list.add(
                UIObject(
                    context.getString(R.string.gps_bearing_accuracy),
                    roundTo1Decimal(it),
                    "°"
                )
            )
        }

        model.mslAltitude?.let {
            list.add(
                UIObject(
                    context.getString(R.string.gps_msl_altitude),
                    roundTo2Decimals(it.toFloat()),
                    "m"
                )
            )
        }

        model.mslAltitudeAccuracy?.let {
            list.add(
                UIObject(
                    context.getString(R.string.gps_msl_altitude_accuracy),
                    roundTo1Decimal(it),
                    "m"
                )
            )
        }

        model.isMock?.let {
            list.add(
                UIObject(
                    context.getString(R.string.gps_mock_location),
                    if (it) ThreeState.YES else ThreeState.NO,
                    ListType.ICON
                )
            )
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

    fun getGnssCapabilitiesList(
        context: Context,
        capabilities: GnssCapabilities?,
        signalTypes: List<String>,
        antennaFrequencies: List<Double>
    ): List<UIObject> {
        if (capabilities == null && signalTypes.isEmpty() && antennaFrequencies.isEmpty()) {
            return emptyList()
        }
        val list = ArrayList<UIObject>()

        if (capabilities != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.add(UIObject(context.getString(R.string.gnss_capabilities), "", ListType.TITLE))

            list.add(capabilityItem(context, R.string.gnss_has_measurements, capabilities.hasMeasurements()))
            list.add(capabilityItem(context, R.string.gnss_has_nav_messages, capabilities.hasNavigationMessages()))
            list.add(capabilityItem(context, R.string.gnss_has_low_power_mode, capabilities.hasLowPowerMode()))
            list.add(capabilityItem(context, R.string.gnss_has_geofencing, capabilities.hasGeofencing()))
            list.add(capabilityItem(context, R.string.gnss_has_satellite_blocklist, capabilities.hasSatelliteBlocklist()))
            list.add(capabilityItem(context, R.string.gnss_has_satellite_pvt, capabilities.hasSatellitePvt()))
            list.add(capabilityItem(context, R.string.gnss_has_antenna_info, capabilities.hasAntennaInfo()))
            list.add(capabilityItem(context, R.string.gnss_has_scheduling, capabilities.hasScheduling()))
            list.add(capabilityItem(context, R.string.gnss_has_single_shot_fix, capabilities.hasSingleShotFix()))
            list.add(capabilityItem(context, R.string.gnss_has_on_demand_time, capabilities.hasOnDemandTime()))
            list.add(capabilityItem(context, R.string.gnss_has_msa, capabilities.hasMsa()))
            list.add(capabilityItem(context, R.string.gnss_has_msb, capabilities.hasMsb()))
            list.add(capabilityItem(context, R.string.gnss_has_measurement_corrections, capabilities.hasMeasurementCorrections()))
            list.add(capabilityItem(context, R.string.gnss_has_corrections_los, capabilities.hasMeasurementCorrectionsLosSats()))
            list.add(capabilityItem(context, R.string.gnss_has_corrections_excess_path, capabilities.hasMeasurementCorrectionsExcessPathLength()))
            list.add(capabilityItem(context, R.string.gnss_has_corrections_reflecting, capabilities.hasMeasurementCorrectionsReflectingPlane()))

            if (Build.VERSION.SDK_INT >= 33) {
                list.add(capabilityItem(context, R.string.gnss_has_corrections_driving, capabilities.hasMeasurementCorrectionsForDriving()))
                list.add(capabilityItem(context, R.string.gnss_has_correlation_vectors, capabilities.hasMeasurementCorrelationVectors()))
            }

            list.add(capabilityItem(context, R.string.gnss_power_total, capabilities.hasPowerTotal()))
            list.add(capabilityItem(context, R.string.gnss_power_singleband_tracking, capabilities.hasPowerSinglebandTracking()))
            list.add(capabilityItem(context, R.string.gnss_power_singleband_acquisition, capabilities.hasPowerSinglebandAcquisition()))
            list.add(capabilityItem(context, R.string.gnss_power_multiband_tracking, capabilities.hasPowerMultibandTracking()))
            list.add(capabilityItem(context, R.string.gnss_power_multiband_acquisition, capabilities.hasPowerMultibandAcquisition()))
            list.add(capabilityItem(context, R.string.gnss_power_other_modes, capabilities.hasPowerOtherModes()))

            val adrState = capabilities.hasAccumulatedDeltaRange()
            list.add(
                UIObject(
                    context.getString(R.string.gnss_accumulated_delta_range),
                    when (adrState) {
                        GnssCapabilities.CAPABILITY_SUPPORTED -> context.getString(R.string.gnss_adr_supported)
                        GnssCapabilities.CAPABILITY_UNSUPPORTED -> context.getString(R.string.gnss_adr_unsupported)
                        else -> context.getString(R.string.gnss_adr_unknown)
                    }
                )
            )

            if (Build.VERSION.SDK_INT >= 37) {
                list.add(capabilityItem(context, R.string.gnss_engine_restart_power_mode, capabilities.hasGnssEngineRestartAfterPowerModeChange()))
            }
        }

        if (signalTypes.isNotEmpty()) {
            list.add(UIObject(context.getString(R.string.gnss_signal_types), "", ListType.TITLE))
            signalTypes.forEach { signalType ->
                list.add(UIObject(signalType, ""))
            }
        }

        if (antennaFrequencies.isNotEmpty()) {
            list.add(UIObject(context.getString(R.string.gnss_antenna_info), "", ListType.TITLE))
            antennaFrequencies.forEachIndexed { index, freq ->
                list.add(
                    UIObject(
                        "${context.getString(R.string.gnss_antenna_carrier_freq)} ${index + 1}",
                        String.format(Locale.ENGLISH, "%.1f", freq),
                        "MHz"
                    )
                )
            }
        }

        return list
    }

    private fun capabilityItem(context: Context, nameResId: Int, supported: Boolean): UIObject {
        return UIObject(
            context.getString(nameResId),
            if (supported) ThreeState.YES else ThreeState.NO,
            ListType.ICON
        )
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