package com.pacmac.devinfo.wifi

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.utils.Utils
import com.pacmac.devinfo.wifi.NetworkUtils.getDHCPInfo
import com.pacmac.devinfo.wifi.NetworkUtils.getRadiosState
import com.pacmac.devinfo.wifi.NetworkUtils.getWifiFeatures
import com.pacmac.devinfo.wifi.NetworkUtils.getWifiInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "NetworkViewModel"

@HiltViewModel
class NetworkViewModelKt @Inject constructor() : ViewModel() {

    private var isActive = false

    private val _wifiInfo = MutableStateFlow<List<UIObject>>(emptyList())
    val wifiInfo: StateFlow<List<UIObject>> = _wifiInfo.asStateFlow()

    private var radioState: List<UIObject> = emptyList()
    private var wifiInformation: List<UIObject> = emptyList()
    private var dhcpInformation: List<UIObject> = emptyList()
    private var wifiFeatures: List<UIObject> = emptyList()

    fun fetchWifiInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            loadWifiInfo(context)
        }
    }

    private fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (isActive) {
            emit(Unit)
            delay(period)
        }
    }

    fun observeNetworkInfo(context: Context) {
        if (isActive) return
        isActive = true
        tickerFlow(2.seconds).onEach {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    loadWifiInfo(context)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load wifi info", e)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun stopObserver() {
        isActive = false
    }

    fun getWifiInfoForExport(): List<UIObject> = buildList {
        if (radioState.isNotEmpty()) addAll(radioState)
        if (wifiInformation.isNotEmpty()) {
            add(UIObject("", ""))
            add(UIObject("", ""))
            addAll(wifiInformation)
        }
        if (dhcpInformation.isNotEmpty()) {
            add(UIObject("", ""))
            add(UIObject("", ""))
            addAll(dhcpInformation)
        }
        if (wifiFeatures.isNotEmpty()) {
            add(UIObject("", ""))
            add(UIObject("", ""))
            addAll(wifiFeatures)
        }
    }

    private fun loadWifiInfo(context: Context) {
        val isLocationPermissionEnabled = Utils.checkPermission(context, Utils.LOCATION_PERMISSION)
        radioState = getRadiosState(context) ?: emptyList()
        wifiInformation = getWifiInformation(context, isLocationPermissionEnabled) ?: emptyList()
        dhcpInformation = getDHCPInfo(context) ?: emptyList()
        wifiFeatures = getWifiFeatures(context) ?: emptyList()

        _wifiInfo.value = buildList {
            addAll(radioState)
            addAll(wifiInformation)
            addAll(dhcpInformation)
            addAll(wifiFeatures)
        }
    }
}
