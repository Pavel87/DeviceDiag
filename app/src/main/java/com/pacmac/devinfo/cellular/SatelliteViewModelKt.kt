package com.pacmac.devinfo.cellular

import android.app.Application
import android.os.Build
import android.telephony.satellite.SatelliteManager
import android.telephony.satellite.SatelliteStateChangeListener
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class SatelliteViewModelKt @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _satelliteInfo = mutableStateOf<List<UIObject>>(emptyList())
    val satelliteInfo: State<List<UIObject>> = _satelliteInfo

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private var satelliteManager: SatelliteManager? = null
    private var isSatelliteEnabled: Boolean? = null
    private var listener: SatelliteStateChangeListener? = null
    private val executor = Executors.newSingleThreadExecutor()

    init {
        loadSatelliteInfo()
    }

    private fun loadSatelliteInfo() {
        if (!SatelliteUtils.isSatelliteApiAvailable()) {
            _isLoading.value = false
            return
        }

        satelliteManager = SatelliteUtils.getSatelliteManager(application)
        if (satelliteManager == null) {
            _isLoading.value = false
            buildAndSetInfo()
            return
        }

        if (Build.VERSION.SDK_INT >= 36) {
            listener = SatelliteStateChangeListener { enabled ->
                isSatelliteEnabled = enabled
                buildAndSetInfo()
            }
            try {
                satelliteManager?.registerStateChangeListener(executor, listener!!)
            } catch (e: Exception) {
                // May throw if satellite not supported on hardware
            }
        }

        buildAndSetInfo()
        _isLoading.value = false
    }

    fun refresh() {
        buildAndSetInfo()
    }

    private fun buildAndSetInfo() {
        _satelliteInfo.value = SatelliteUtils.buildSatelliteInfoList(
            context = application,
            isSatelliteEnabled = isSatelliteEnabled,
            isSatelliteTransportActive = SatelliteUtils.isSatelliteTransportActive(application)
        )
    }

    override fun onCleared() {
        super.onCleared()
        if (Build.VERSION.SDK_INT >= 36 && listener != null) {
            try {
                satelliteManager?.unregisterStateChangeListener(listener!!)
            } catch (e: Exception) {
                // Ignore
            }
        }
        executor.shutdown()
    }
}
