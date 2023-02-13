package com.pacmac.devinfo.gps

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.gps.models.GPSMainInfoModel
import com.pacmac.devinfo.gps.models.ScreenType
import com.pacmac.devinfo.gps.models.NMEALog
import com.pacmac.devinfo.gps.models.Satellite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GPSViewModelKt @Inject constructor(private val locationRepository: LocationRepository) :
    ViewModel() {

    companion object {
        val EXPORT_FILE_NAME = "gps_info"
        val EXPORT_NMEA_FILE_NAME = "NMEA.txt"
    }

    private val gpsDataDefault = GPSMainInfoModel(
        Status.UNKNOWN,
        -1,
        -1,
        locationRepository.getGnssYearOfHardware(),
        Double.MAX_VALUE,
        Double.MAX_VALUE,
        Double.MAX_VALUE,
        Float.MAX_VALUE,
        Float.MAX_VALUE,
        Float.MAX_VALUE
    )

    private var isNMEALogRunning = false

    private var isExporting = false
    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    // REFACTOR EXPORT logic
    fun export(context: Context, screenType: ScreenType) {
        if (!isExporting) {
            isExporting = true

            if (screenType == ScreenType.NMEA) {
                if (nmeaLog.value.isNotEmpty()) {
                    ExportTask(context, EXPORT_NMEA_FILE_NAME) {
                        viewModelScope.launch {
                            isExporting = false
                            _onExportDone.emit(it)
                        }
                    }
                        .execute(this)
                }
            } else {
                ExportTask(context, EXPORT_FILE_NAME) {
                    viewModelScope.launch {
                        isExporting = false
                        _onExportDone.emit(it)
                    }
                }.execute(this)
            }
        }
    }

    private val _gpsInfo = mutableStateOf(gpsDataDefault)
    fun getMainGPSData(): State<GPSMainInfoModel> = _gpsInfo

    private val _updateTimeLive = mutableStateOf("--:--:--")
    fun getUpdateTimeLive(): State<String> = _updateTimeLive

    private val _satellites = mutableStateOf<List<Satellite>>(arrayListOf())
    val satellites: State<List<Satellite>> = _satellites

    private val _nmeaLog = mutableStateOf<List<NMEALog>>(emptyList())
    val nmeaLog: State<List<NMEALog>> = _nmeaLog

    private val _address = mutableStateOf("" to "")
    val address: State<Pair<String, String>> = _address

    fun isGPSEnabled(): Boolean = locationRepository.isGPSEnabled()

    fun unsubscribeToGPSUpdates() {
        areGPSUpdatesActive = false
    }

    private var areGPSUpdatesActive = false

    fun subscribeToGPSUpdates() {
        if (isGPSEnabled()) {
            areGPSUpdatesActive = true
            observeLocationUpdates()
            observeGPSStatus()
        }
    }

    fun subscribeToNMEALog() {
        if (isGPSEnabled()) {
            isNMEALogRunning = true
            viewModelScope.launch {
                locationRepository.subscribeToNMEAMessage()
                    .takeWhile { isNMEALogRunning }
                    .onEach {
                        _nmeaLog.value = it.toList()
                    }.collect()
            }
        }
    }

    fun unsubscribeToNMEALog() {
        isNMEALogRunning = false
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val address = withContext(Dispatchers.IO) {
                locationRepository.fetchCurrentAddress(
                    latitude,
                    longitude
                )
            }
            _address.value = address
        }
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            locationRepository.subscribeToLocationUpdates()
                .takeWhile { areGPSUpdatesActive }
                .onEach { locationUpdate ->
                    println("PACMAC - LOCATION UPDATE: ${locationUpdate}")

                    _updateTimeLive.value = locationUpdate.getUpdateTime()

                    _gpsInfo.value = GPSMainInfoModel(
                        _gpsInfo.value.gpsStatus,
                        _gpsInfo.value.firstFix,
                        _gpsInfo.value.visibleSatellites,
                        locationRepository.getGnssYearOfHardware(),
                        locationUpdate.latitude,
                        locationUpdate.longitude,
                        locationUpdate.altitude,
                        locationUpdate.speed,
                        locationUpdate.accuracy,
                        locationUpdate.bearing
                    )

                    getAddress(locationUpdate.latitude, locationUpdate.longitude)

                }
                .catch {
                    println("PACMAC - LOCATION UPDATE EXCEPTION")
                    it.printStackTrace()
                }
                .collect()

        }
    }

    private fun observeGPSStatus() {
        viewModelScope.launch {
            locationRepository.subscribeToGPSStatus()
                .takeWhile { areGPSUpdatesActive }
                .onEach { gpsStatus ->
                    println("PACMAC - GPS STATUS UPDATE: ${gpsStatus.gpsStatus.name}")
                    gpsStatus.satellites?.let {
                        _satellites.value = it
                    }

                    val timeToFirstFix = if (gpsStatus.firstFixTime != -1) {
                        gpsStatus.firstFixTime
                    } else {
                        _gpsInfo.value.firstFix
                    }

                    val satCount =
                        if (gpsStatus.satellites == null) {
                            _gpsInfo.value.visibleSatellites
                        } else {
                            gpsStatus.satelliteCount
                        }

                    _gpsInfo.value = GPSMainInfoModel(
                        gpsStatus.gpsStatus,
                        timeToFirstFix,
                        satCount,
                        locationRepository.getGnssYearOfHardware(),
                        _gpsInfo.value.latitude,
                        _gpsInfo.value.longitude,
                        _gpsInfo.value.altitude,
                        _gpsInfo.value.speed,
                        _gpsInfo.value.accuracy,
                        _gpsInfo.value.bearing
                    )

                }
                .catch {
                    println("PACMAC - GPS STATUS UPDATE EXCEPTION")
                    it.printStackTrace()
                }
                .collect()
        }
    }
}