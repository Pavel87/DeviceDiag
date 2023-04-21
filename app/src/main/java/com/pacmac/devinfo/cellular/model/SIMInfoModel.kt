package com.pacmac.devinfo.cellular.model

import android.content.Context
import android.telephony.TelephonyManager
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.utils.Utils

data class SIMInfoModel(
    val simState: Int?,
    val phoneNumber: String?,
    val voiceMailNumber: String?,
    val serviceProvider: String?,
    val SIMMcc: String?,
    val SIMMnc: String?,
    val simCountryISO: String?,
    val isGSMPhone: Int,
    val imeiOrMeid: String?,
    val tac: String?,
    val manufacturerCode: String?,
    val carrierId: String?,
    val getGroupIdLevel: String?,
    val iccid: String?,
    val imsi: String?,
    val isEmbedded: Boolean?
) {


    companion object {
        fun getUIObjects(context: Context, data: List<SIMInfoModel>): List<UIObject> {
            val list = ArrayList<UIObject>()
            if (data.isEmpty()) return list

            data.forEachIndexed { i, sim ->
                list.add(UIObject(context.getString(R.string.simID), "${i + 1}", ListType.TITLE))
                list.add(getSIMStateString(context, sim.simState))
                list.add(Utils.createUIObject(context, sim.phoneNumber, R.string.phone_number))
                list.add(Utils.createUIObject(context, sim.voiceMailNumber, R.string.voicemail_number))
                list.add(Utils.createUIObject(context, sim.serviceProvider, R.string.spn))
                list.add(Utils.createUIObject(context, sim.SIMMcc, R.string.mcc))
                list.add(Utils.createUIObject(context, sim.SIMMnc, R.string.mnc))
                list.add(Utils.createUIObject(context, sim.simCountryISO?.uppercase(), R.string.country_iso))
                sim.imeiOrMeid?.let {
                    val label =
                        if (sim.isGSMPhone == TelephonyManager.PHONE_TYPE_GSM) R.string.imei else R.string.meid
                    list.add(Utils.createUIObject(context, sim.imeiOrMeid, label))
                }
                list.add(Utils.createUIObject(context, sim.tac, R.string.tac))

                if (sim.isGSMPhone == TelephonyManager.PHONE_TYPE_CDMA) {
                    list.add(
                        Utils.createUIObject(
                            context,
                            sim.manufacturerCode,
                            R.string.manufacturer_code
                        )
                    )
                }
                list.add(Utils.createUIObject(context, sim.carrierId, R.string.carrier_id))
                list.add(Utils.createUIObject(context, sim.getGroupIdLevel, R.string.group_id_level1))
                list.add(Utils.createUIObject(context, sim.iccid, R.string.sim_serial_number))
                list.add(Utils.createUIObject(context, sim.imsi, R.string.imsi))
                list.add(Utils.createUIObject(context, sim.isEmbedded, R.string.embedded))
            }

            return list
        }



        private fun getSIMStateString(context: Context, state: Int?): UIObject {
            val stateString: String = when (state) {
                TelephonyManager.SIM_STATE_UNKNOWN -> context.getString(R.string.sim_state_unknown)
                TelephonyManager.SIM_STATE_ABSENT -> context.getString(R.string.no_sim_inserted)
                TelephonyManager.SIM_STATE_PIN_REQUIRED -> context.getString(R.string.sim_locked_pin)
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> context.getString(R.string.sim_locked_puk)
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> context.getString(R.string.network_locked)
                TelephonyManager.SIM_STATE_READY -> context.getString(R.string.sim_ready)
                6 -> context.getString(R.string.not_ready)
                7 -> context.getString(R.string.sim_disabled)
                8 -> context.getString(R.string.sim_io_error)
                9 -> context.getString(R.string.sim_restricted)
                10 -> context.getString(R.string.sim_loaded)
                11 -> context.getString(R.string.sim_present)
                else -> context.getString(R.string.default_sim_state) + state
            }
            return UIObject(context.getString(R.string.sim_state), stateString)
        }
    }
}
