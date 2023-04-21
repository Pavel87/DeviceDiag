package com.pacmac.devinfo.wifi

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.utils.Utility
import com.pacmac.devinfo.utils.Utils
import com.pacmac.devinfo.wifi.NetworkUtils.getDHCPInfo
import com.pacmac.devinfo.wifi.NetworkUtils.getRadiosState
import com.pacmac.devinfo.wifi.NetworkUtils.getWifiFeatures
import com.pacmac.devinfo.wifi.NetworkUtils.getWifiInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NetworkViewModelKt @Inject constructor() : ViewModel() {

    private var isActive = false

    private val wifiInfo = mutableStateOf<List<UIObject>>(arrayListOf())

    private var radioState: List<UIObject>? = ArrayList()
    private var wifiInformation: List<UIObject>? = ArrayList()
    private var dhcpInformation: List<UIObject>? = ArrayList()
    private var wifiFeatures: List<UIObject>? = ArrayList()


    fun getWifiInfo(): State<List<UIObject>> {
        return wifiInfo
    }

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
                        e.printStackTrace()
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun stopObserver() {
        isActive = false
    }

    fun getWifiInfoForExport(): List<UIObject>? {
        val list: MutableList<UIObject> = ArrayList()
        if (radioState != null && radioState!!.size != 0) {
            list.addAll(radioState!!)
        }
        if (wifiInformation != null && wifiInformation!!.size != 0) {
            list.add(UIObject("", ""))
            list.add(UIObject("", ""))
            list.addAll(wifiInformation!!)
        }
        if (dhcpInformation != null && dhcpInformation!!.size != 0) {
            list.add(UIObject("", ""))
            list.add(UIObject("", ""))
            list.addAll(dhcpInformation!!)
        }
        if (wifiFeatures != null && wifiFeatures!!.size != 0) {
            list.add(UIObject("", ""))
            list.add(UIObject("", ""))
            list.addAll(wifiFeatures!!)
        }
        return list
    }

    private fun loadWifiInfo(context: Context) {
        val list: MutableList<UIObject> = ArrayList()
        val isLocationPermissionEnabled =
            Utils.checkPermission(context, Utils.LOCATION_PERMISSION)
        radioState = getRadiosState(context)
        wifiInformation = getWifiInformation(context, isLocationPermissionEnabled)
        dhcpInformation = getDHCPInfo(context)
        wifiFeatures = getWifiFeatures(context)
        list.addAll(radioState!!)
        list.addAll(wifiInformation!!)
        if (dhcpInformation != null) {
            list.addAll(dhcpInformation!!)
        }
        list.addAll(wifiFeatures!!)
        wifiInfo.value = list
    }
}