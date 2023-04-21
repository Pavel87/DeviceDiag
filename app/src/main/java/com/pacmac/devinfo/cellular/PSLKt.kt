package com.pacmac.devinfo.cellular

import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellLocation
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.TelephonyDisplayInfo
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class PSLKt : PhoneStateListener() {

    private val _onServiceStateUpdate = MutableStateFlow(ServiceState())
    val onServiceStateUpdate: StateFlow<ServiceState> = _onServiceStateUpdate
    private val _updateCellInfos = MutableStateFlow(Unit)
    val updateCellInfos: StateFlow<Unit> = _updateCellInfos
    private val _updateNetwork = MutableStateFlow(Unit)
    val updateNetwork: StateFlow<Unit> = _updateNetwork
    private val _refreshAll = MutableStateFlow(Unit)
    val refreshAll: StateFlow<Unit> = _refreshAll

    var overrideNetworkType = 0

    override fun onServiceStateChanged(serviceState: ServiceState?) {
        if (serviceState != null) {
            _onServiceStateUpdate.tryEmit(serviceState)
        }
    }

    override fun onCellLocationChanged(location: CellLocation?) {
        if (location != null) {
            _updateCellInfos.tryEmit(Unit)
        }
    }

    override fun onDataConnectionStateChanged(state: Int, networkType: Int) {
        _updateNetwork.tryEmit(Unit)

    }

    override fun onDataActivity(direction: Int) {
        _updateNetwork.tryEmit(Unit)
    }

    override fun onCellInfoChanged(cellInfo: List<CellInfo?>?) {
        if (cellInfo != null) {
            _updateCellInfos.tryEmit(Unit)
        }
    }

    override fun onUserMobileDataStateChanged(enabled: Boolean) {
        _updateNetwork.tryEmit(Unit)
    }

    override fun onActiveDataSubscriptionIdChanged(subId: Int) {
        _refreshAll.tryEmit(Unit)
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
        overrideNetworkType = telephonyDisplayInfo.overrideNetworkType
        _updateNetwork.tryEmit(Unit)
    }

    companion object {
        fun getPSLListenerFlags(): Int {
            var pslListenFlags = (LISTEN_NONE
                    or LISTEN_DATA_ACTIVITY
                    or LISTEN_DATA_CONNECTION_STATE
                    or LISTEN_CELL_INFO
                    or LISTEN_CELL_LOCATION
                    or LISTEN_SERVICE_STATE)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                pslListenFlags = pslListenFlags or LISTEN_ACTIVE_DATA_SUBSCRIPTION_ID_CHANGE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pslListenFlags = pslListenFlags or LISTEN_DISPLAY_INFO_CHANGED
            }
            return pslListenFlags
        }

        fun getStopPslFlag() = LISTEN_NONE
    }
}