package com.pacmac.devinfo.cellular.model

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.cellular.MobileNetworkUtilKt
import java.util.Locale

data class BasicPhoneModel(
    val slotCount: Int,
    val phoneRadioType: String,
    val deviceSWVersion: String?,
    val isSmsCapable: Boolean,
    val isVoiceCapable: Boolean,
    val isConcurrentVoiceAndDataSupported: Boolean?,
    val isRttSupported: Boolean?,
    val isMultiSimSupported: Int?,
    val isWorldPhone: Boolean
) {

    companion object {
        fun getUIObjects(context: Context, data: BasicPhoneModel?): List<UIObject> {
            val list = ArrayList<UIObject>()
            data ?: return list
            list.add(getSimCount(context, data.slotCount))
            list.add(getPhoneRadioType(context, data.phoneRadioType))
            list.add(getDeviceSWVersion(context, data.deviceSWVersion))
            list.add(isSmsCapable(context, data.isSmsCapable))
            list.add(isVoiceCapable(context, data.isVoiceCapable))
            data.isConcurrentVoiceAndDataSupported?.let {
                list.add(isConcurrentVoiceAndDataSupported(context, it))
            }
            data.isRttSupported?.let {
                list.add(isRttSupported(context, it))
            }
            data.isMultiSimSupported?.let {
                list.add(isMultiSimSupported(context, it))
            }
            list.add(isWorldPhone(context, data.isWorldPhone))
            return list
        }

        private fun isWorldPhone(context: Context, isWorldPhone: Boolean): UIObject {
            return UIObject(
                context.getString(R.string.is_world_phone),
                if (isWorldPhone) context.getString(R.string.yes_string) else context.getString(
                    R.string.no_string
                )
            )
        }

        @SuppressLint("MissingPermission")
        @TargetApi(29)
        private fun isMultiSimSupported(context: Context, isMultiSimSupported: Int): UIObject {
            return UIObject(
                context.getString(R.string.multi_sim_support),
                MobileNetworkUtilKt.getMultiSIMSupport(context, isMultiSimSupported)
            )
        }

        private fun isSmsCapable(context: Context, isSmsCapable: Boolean): UIObject {
            return UIObject(
                context.getString(R.string.sms_service),
                if (isSmsCapable) context.resources.getString(R.string.supported) else context.resources.getString(
                    R.string.not_supported
                )
            )
        }

        @TargetApi(29)
        private fun isRttSupported(context: Context, isRttSupported: Boolean): UIObject {
            return UIObject(
                context.getString(R.string.real_time_text),
                if (isRttSupported) context.resources.getString(R.string.supported) else context.resources.getString(
                    R.string.not_supported
                )
            )
        }

        private fun isVoiceCapable(context: Context, isVoiceCapable: Boolean): UIObject {
            return UIObject(
                context.getString(R.string.voice_capable),
                if (isVoiceCapable) context.getString(R.string.yes_string) else context.getString(
                    R.string.no_string
                )
            )
        }

        @TargetApi(26)
        private fun isConcurrentVoiceAndDataSupported(
            context: Context,
            isConcurrentVoiceAndDataSupported: Boolean
        ): UIObject {
            return UIObject(
                context.getString(R.string.concurrent_voice_support),
                if (isConcurrentVoiceAndDataSupported) context.resources.getString(
                    R.string.supported
                ) else context.resources.getString(R.string.not_supported)
            )
        }


        private fun getSimCount(context: Context, simCount: Int): UIObject {
            return if (simCount != -1) {
                UIObject(
                    context.getString(R.string.sim_count),
                    String.format(Locale.ENGLISH, "%d", simCount)
                )
            } else {
                UIObject(
                    context.getString(R.string.sim_count),
                    String.format(Locale.ENGLISH, "%d", context.resources.getString(R.string.error))
                )
            }
        }

        private fun getPhoneRadioType(context: Context, phoneType: String): UIObject {
            return UIObject(context.getString(R.string.phone_radio), phoneType)
        }

        private fun getDeviceSWVersion(context: Context, swVersion: String?): UIObject {
            return UIObject(
                context.getString(R.string.sw_version),
                swVersion ?: context.resources.getString(R.string.not_available_info)
            )
        }


    }


}
