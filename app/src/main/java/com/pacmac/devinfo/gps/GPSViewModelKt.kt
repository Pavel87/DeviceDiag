package com.pacmac.devinfo.gps

import android.content.Context
import android.location.GnssCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.gps.models.GPSMainInfoModel
import com.pacmac.devinfo.gps.models.NMEALog
import com.pacmac.devinfo.gps.models.ScreenType
import com.pacmac.devinfo.gps.models.Satellite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "GPSViewModel"

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
        Float.MAX_VALUE,
        gnssHardwareModelName = locationRepository.getGnssHardwareModelName(),
    )

    private var isNMEALogRunning = false
    private var isExporting = false

    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

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
                    }.execute(this)
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

    private val _gpsInfo = MutableStateFlow(gpsDataDefault)
    val gpsInfo: StateFlow<GPSMainInfoModel> = _gpsInfo.asStateFlow()

    private val _updateTimeLive = MutableStateFlow("--:--:--")
    val updateTimeLive: StateFlow<String> = _updateTimeLive.asStateFlow()

    private val _satellites = MutableStateFlow<List<Satellite>>(emptyList())
    val satellites: StateFlow<List<Satellite>> = _satellites.asStateFlow()

    private val _nmeaLog = MutableStateFlow<List<NMEALog>>(emptyList())
    val nmeaLog: StateFlow<List<NMEALog>> = _nmeaLog.asStateFlow()

    private val _address = MutableStateFlow("" to "")
    val address: StateFlow<Pair<String, String>> = _address.asStateFlow()

    private val _gnssCapabilities = MutableStateFlow<GnssCapabilities?>(null)
    val gnssCapabilities: StateFlow<GnssCapabilities?> = _gnssCapabilities.asStateFlow()

    private val _gnssSignalTypes = MutableStateFlow<List<String>>(emptyList())
    val gnssSignalTypes: StateFlow<List<String>> = _gnssSignalTypes.asStateFlow()

    private val _gnssAntennaFrequencies = MutableStateFlow<List<Double>>(emptyList())
    val gnssAntennaFrequencies: StateFlow<List<Double>> = _gnssAntennaFrequencies.asStateFlow()

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
            loadGnssCapabilities()
        }
    }

    private fun loadGnssCapabilities() {
        viewModelScope.launch(Dispatchers.IO) {
            _gnssCapabilities.value = locationRepository.getGnssCapabilities()
            _gnssSignalTypes.value = locationRepository.getGnssSignalTypesDescription()
            _gnssAntennaFrequencies.value = locationRepository.getGnssAntennaCarrierFrequencies()
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
                locationRepository.fetchCurrentAddress(latitude, longitude)
            }
            _address.value = address
        }
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            locationRepository.subscribeToLocationUpdates()
                .takeWhile { areGPSUpdatesActive }
                .onEach { locationUpdate ->
                    Log.d(TAG, "Location update: $locationUpdate")
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
                        locationUpdate.bearing,
                        verticalAccuracy = locationUpdate.verticalAccuracy,
                        speedAccuracy = locationUpdate.speedAccuracy,
                        bearingAccuracy = locationUpdate.bearingAccuracy,
                        mslAltitude = locationUpdate.mslAltitude,
                        mslAltitudeAccuracy = locationUpdate.mslAltitudeAccuracy,
                        isMock = locationUpdate.isMock,
                        gnssHardwareModelName = locationRepository.getGnssHardwareModelName(),
                    )
                    getAddress(locationUpdate.latitude, locationUpdate.longitude)
                }
                .catch { e ->
                    Log.e(TAG, "Location update exception", e)
                }
                .collect()
        }
    }

    private fun observeGPSStatus() {
        viewModelScope.launch {
            locationRepository.subscribeToGPSStatus()
                .takeWhile { areGPSUpdatesActive }
                .onEach { gpsStatus ->
                    Log.d(TAG, "GPS status update: ${gpsStatus.gpsStatus.name}")
                    gpsStatus.satellites?.let { _satellites.value = it }

                    val timeToFirstFix = if (gpsStatus.firstFixTime != -1) {
                        gpsStatus.firstFixTime
                    } else {
                        _gpsInfo.value.firstFix
                    }

                    val satCount = if (gpsStatus.satellites == null) {
                        _gpsInfo.value.visibleSatellites
                    } else {
                        gpsStatus.satelliteCount
                    }

                    _gpsInfo.value = _gpsInfo.value.copy(
                        gpsStatus = gpsStatus.gpsStatus,
                        firstFix = timeToFirstFix,
                        visibleSatellites = satCount,
                        gnssYearOfHardware = locationRepository.getGnssYearOfHardware(),
                    )
                }
                .catch { e ->
                    Log.e(TAG, "GPS status update exception", e)
                }
                .collect()
        }
    }
}
