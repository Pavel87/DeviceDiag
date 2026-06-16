package com.pacmac.devinfo.cellular

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.satellite.SatelliteManager
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject

object SatelliteUtils {

    fun isSatelliteApiAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= 36
    }

    fun getSatelliteManager(context: Context): SatelliteManager? {
        if (!isSatelliteApiAvailable()) return null
        return try {
            context.getSystemService(SatelliteManager::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun isSatelliteTransportActive(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 36) return false
        return try {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val network = connMgr.activeNetwork ?: return false
            val caps = connMgr.getNetworkCapabilities(network) ?: return false
            caps.hasTransport(NetworkCapabilities.TRANSPORT_SATELLITE)
        } catch (e: Exception) {
            false
        }
    }

    fun buildSatelliteInfoList(
        context: Context,
        isSatelliteEnabled: Boolean?,
        isSatelliteTransportActive: Boolean
    ): List<UIObject> {
        val list = mutableListOf<UIObject>()

        list.add(
            UIObject(
                context.getString(R.string.satellite_communication),
                "",
                ListType.TITLE
            )
        )

        val managerAvailable = getSatelliteManager(context) != null
        list.add(
            UIObject(
                context.getString(R.string.satellite_supported),
                if (managerAvailable) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        if (!managerAvailable) return list

        isSatelliteEnabled?.let {
            list.add(
                UIObject(
                    context.getString(R.string.satellite_modem_state),
                    if (it) context.getString(R.string.nfc_enabled) else context.getString(R.string.nfc_disabled)
                )
            )
        }

        list.add(
            UIObject(
                context.getString(R.string.network_satellite_transport),
                if (isSatelliteTransportActive) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        return list
    }
}
