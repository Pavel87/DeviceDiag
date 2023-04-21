package com.pacmac.devinfo.cellular.model

import android.content.Context
import android.os.Build
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.utils.Utils

data class Radio(
    val index: Int,
    val gen: String?,
    val serviceState: Int?,
    val voiceNetworkType: String?,
    val dataNetworkType: String?,
    val networkSPN: String?,
    val mcc: String?,
    val mnc: String?,
    val countryCode: String?,
    val isDataEnabled: Boolean?,
    val isDataRoamingEnabled: Boolean?,
    val plmns: String?,
    val rejectCause: String?,
    val lteCADuplex: String?,
    val lteCABandwidths: String?,
    val endcStatus: String?,
    val fiveGStatus: String?,
    val nrFrequency: String?,
    val is4Gor5G: Boolean
) {

    companion object {
        fun getNetworkInfoForRadio(context: Context, data: Radio): List<UIObject> {
            val list = ArrayList<UIObject>()
            list.add(
                UIObject(
                    context.getString(R.string.network),
                    (data.index + 1).toString(),
                    ListType.TITLE
                )
            )

            list.add(Utils.createUIObject(context, data.gen, R.string.network_gen))
            list.add(
                Utils.createUIObject(
                    context,
                    getVoiceServiceState(data.serviceState),
                    R.string.service_state
                )
            )
            list.add(
                Utils.createUIObject(
                    context,
                    data.voiceNetworkType,
                    R.string.voice_network_type
                )
            )
            list.add(
                Utils.createUIObject(
                    context,
                    data.dataNetworkType,
                    R.string.data_network_type
                )
            )
            list.add(Utils.createUIObject(context, data.networkSPN, R.string.spn))
            list.add(Utils.createUIObject(context, data.mcc, R.string.mcc))
            list.add(Utils.createUIObject(context, data.mnc, R.string.mnc))
            list.add(Utils.createUIObject(context, data.countryCode, R.string.network_country_code))

            data.isDataEnabled?.let {
                list.add(Utils.createUIObject(context, it, R.string.data_enabled))
            }
            data.isDataRoamingEnabled?.let {
                list.add(Utils.createUIObject(context, it, R.string.data_roaming_enabled))
            }
            list.add(Utils.createUIObject(context, data.plmns, R.string.forbidden_plmns))
            list.add(Utils.createUIObject(context, data.rejectCause, R.string.cs_reject_cause))


            if (data.is4Gor5G && Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                list.add(Utils.createUIObject(context, data.lteCADuplex, R.string.duplex_mode))
                list.add(
                    Utils.createUIObject(
                        context,
                        data.lteCABandwidths,
                        R.string.cell_bandwidths
                    )
                )

                data.endcStatus?.let {
                    Utils.createUIObject(context, it, R.string.ends_status)
                }

                data.fiveGStatus?.let {
                    Utils.createUIObject(context, it, R.string.nr_status)
                }

                data.nrFrequency?.let {
                    Utils.createUIObject(context, it, R.string.nr_frequency)
                }
            }

            return list
        }


        private fun getVoiceServiceState(value: Int?): String? {
            when (value) {
                0 -> return "In Service"
                1 -> return "Out Of Service"
                2 -> return "Emergency Calls Only"
                3 -> return "Power Off"
            }
            return "Unknown"
        }
    }

}
