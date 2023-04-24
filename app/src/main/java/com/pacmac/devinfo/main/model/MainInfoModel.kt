package com.pacmac.devinfo.main.model

import android.content.Context
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject

data class MainInfoModel(
    val osVersion: String,
    val model: String,
    val manufacturer: String,
    val serialNumber: String?,
    val buildNumber: String,
    val hardware: String,
    val simCount: Int, // -1 error
    val radioFirmware: String?,
    val bootloader: String,
    val language: String,
    val locale: String,
    val phoneNumbers: ArrayList<String>
) {

    companion object {
        fun toUIModelList(context: Context, data: MainInfoModel?): List<UIObject> {
            data ?: return emptyList()
            val list = ArrayList<UIObject>()
            list.add(UIObject(context.getString(R.string.os_version), data.osVersion))
            list.add(UIObject(context.getString(R.string.device_model), data.model))
            list.add(UIObject(context.getString(R.string.device_manufacturer), data.manufacturer))
            data.serialNumber?.let {
                list.add(UIObject(context.getString(R.string.device_sn), data.serialNumber))
            }
            list.add(UIObject(context.getString(R.string.device_build_number), data.buildNumber))
            list.add(UIObject(context.getString(R.string.device_hardware), data.hardware))
            if (data.simCount >= 0) {
                list.add(UIObject(context.getString(R.string.sim_count), data.simCount.toString()))
            }
            data.radioFirmware?.let {
                list.add(
                    UIObject(
                        context.getString(R.string.device_radio_fw),
                        data.radioFirmware.toString()
                    )
                )
            }
            list.add(UIObject(context.getString(R.string.device_bootloader), data.bootloader))
            list.add(UIObject(context.getString(R.string.device_lang), data.language))
            list.add(UIObject(context.getString(R.string.device_locale), data.locale))

            list.addAll(addPhoneNumbers(context, data.phoneNumbers))

            return list
        }

        private fun addPhoneNumbers(context: Context, phoneNumbers: ArrayList<String>): List<UIObject> {
            val uiList = arrayListOf<UIObject>()

            if (phoneNumbers.isEmpty()) {
                return uiList
            }

            if (phoneNumbers.size == 1) {
                uiList.add(
                    UIObject(
                        context.resources.getString(R.string.phone_number),
                        phoneNumbers[0]
                    )
                )
                return uiList
            }

            phoneNumbers.forEachIndexed { i, phoneNumber ->
                uiList.add(
                    UIObject(
                        "${context.resources.getString(R.string.phone_number)} ${i + 1}",
                        phoneNumber
                    )
                )
            }
            return uiList
        }
    }
}