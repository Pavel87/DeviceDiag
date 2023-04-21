package com.pacmac.devinfo.cellular.model

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.utils.Utils

data class CellNetworkModel(
    val downstreamLinkBandwidth: Int?,
    val upstreamLinkBandwidth: Int?,
    val dataState: Int,
    val dataActivity: Int,
    val isNotMetered: Boolean?,
    val radios: List<Radio>
) {

    companion object {
        fun toUIModelList(context: Context, data: CellNetworkModel?): List<UIObject> {
            data ?: return emptyList()
            val list = ArrayList<UIObject>()


            if (data.dataState != TelephonyManager.DATA_DISCONNECTED) {
                data.downstreamLinkBandwidth?.let {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_link_down_bandwidth),
                            it.toString(),
                            "kbps"
                        )
                    )
                } ?: list.add(
                    UIObject(
                        context.getString(R.string.network_link_down_bandwidth),
                        context.getString(R.string.not_available_info)
                    )
                )

                data.upstreamLinkBandwidth?.let {
                    list.add(
                        UIObject(
                            context.getString(R.string.network_up_bandwidth),
                            it.toString(),
                            "kbps"
                        )
                    )
                } ?: list.add(
                    UIObject(
                        context.getString(R.string.network_up_bandwidth),
                        context.getString(R.string.network_up_bandwidth)
                    )
                )
            }
            list.add(
                Utils.createUIObject(
                    context,
                    getDataStateString(data.dataState),
                    R.string.data_state
                )
            )
            list.add(
                Utils.createUIObject(
                    context,
                    getDataActivityString(data.dataActivity),
                    R.string.data_activity
                )
            )
            getMeteredState(context, data)?.let {
                list.add(it)
            }

            data.radios.forEach {
                list.addAll(Radio.getNetworkInfoForRadio(context, it))
            }

            return list
        }


        private fun getMeteredState(context: Context, data: CellNetworkModel): UIObject? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && data.dataState != TelephonyManager.DATA_DISCONNECTED) {
                var metered = context.getString(R.string.not_available_info)
                data.isNotMetered?.let {
                    metered =
                        if (data.isNotMetered) context.resources.getString(R.string.not_metered)
                        else context.resources.getString(R.string.metered)
                }
                return UIObject(context.getString(R.string.network_meteredness), metered)
            }
            return null
        }


        private fun getDataStateString(value: Int): String? {
            when (value) {
                TelephonyManager.DATA_DISCONNECTED -> return "Disconnected"
                TelephonyManager.DATA_CONNECTING -> return "Connecting"
                TelephonyManager.DATA_CONNECTED -> return "Connected"
                TelephonyManager.DATA_SUSPENDED -> return "Suspended"
            }
            return "Unknown"
        }

        private fun getDataActivityString(value: Int): String? {
            when (value) {
                TelephonyManager.DATA_ACTIVITY_DORMANT -> return "DORMANT"
                TelephonyManager.DATA_ACTIVITY_IN -> return "RECEIVING"
                TelephonyManager.DATA_ACTIVITY_OUT -> return "TRANSMITTING"
                TelephonyManager.DATA_ACTIVITY_INOUT -> return "TRANSMITTING & RECEIVING"
            }
            return "NONE"
        }
    }
}