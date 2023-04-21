package com.pacmac.devinfo.cellular

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.CellInfo
import android.telephony.ServiceState
import android.telephony.SubscriptionManager
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.cellular.model.BasicPhoneModel
import com.pacmac.devinfo.cellular.model.CellModel
import com.pacmac.devinfo.cellular.model.CellNetworkModel
import com.pacmac.devinfo.cellular.model.SIMInfoModel
import com.pacmac.devinfo.utils.Utils
import java.util.Locale
import java.util.regex.Pattern

object MobileNetworkUtilKt {

    fun getSIMCount(telephonyManager: TelephonyManager): Int {
        var slotCount = -1
        if (telephonyManager.phoneType == TelephonyManager.PHONE_TYPE_NONE || telephonyManager.phoneCount == 0) {
            slotCount = 0
        } else {
            try {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    slotCount = telephonyManager.activeModemCount
                } else {
                    slotCount = telephonyManager.phoneCount
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return slotCount
    }

    fun getPhoneRadio(telephonyManager: TelephonyManager) = when (telephonyManager.phoneType) {
        TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
        TelephonyManager.PHONE_TYPE_GSM -> "GSM"
        TelephonyManager.PHONE_TYPE_SIP -> "SIP"
        else -> ""
    }


    @SuppressLint("MissingPermission")
    fun getDeviceSoftwareVersion(telephonyManager: TelephonyManager) =
        telephonyManager.deviceSoftwareVersion

    fun getMultiSIMSupport(context: Context, state: Int): String? {
        return when (state) {
            TelephonyManager.MULTISIM_ALLOWED -> context.getString(R.string.ms_supported)
            TelephonyManager.MULTISIM_NOT_SUPPORTED_BY_HARDWARE -> context.getString(R.string.restricted_by_hw)
            TelephonyManager.MULTISIM_NOT_SUPPORTED_BY_CARRIER -> context.getString(R.string.restricted_by_carrier)
            else -> context.resources.getString(R.string.not_available_info)
        }
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getCarrierConfig(telephonyManager: TelephonyManager): List<Pair<String, String>> {
        val persistableBundle = telephonyManager.carrierConfig
        val list: ArrayList<Pair<String, String>> = arrayListOf()
        if (persistableBundle != null && persistableBundle.size() > 0) {
            for (key in persistableBundle.keySet()) {
                val prettyKey = key.replace("_", " ").uppercase(Locale.getDefault())
                var data = persistableBundle[key]
                if (data is IntArray) {
                    val temp = StringBuilder()
                    for (i in data) {
                        temp.append(i)
                        temp.append(",")
                    }
                    data = if (temp.isNotEmpty()) {
                        temp.substring(0, temp.length - 1)
                    } else {
                        ""
                    }
                }
                if (data is Array<*>) {
                    val temp = StringBuilder()
                    for (s in data as Array<String?>) {
                        temp.append(s)
                        temp.append(",")
                    }
                    data = if (temp.isNotEmpty()) {
                        temp.substring(0, temp.length - 1)
                    } else {
                        ""
                    }
                }
                list.add(prettyKey to data.toString())
            }
        }
        return list
    }

    fun getSimState(telephonyManager: TelephonyManager, slotID: Int, isMultiSIM: Boolean): Int {
        var state = TelephonyManager.SIM_STATE_UNKNOWN
        if (!isMultiSIM) {
            state = telephonyManager.simState
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                state = telephonyManager.getSimState(slotID)
            } else {
                val simStateRaw = getOutput(telephonyManager, "getSimState", slotID)
                if (simStateRaw != null) {
                    state = simStateRaw.toInt()
                }
            }
        }
        return state
    }

    @SuppressLint("MissingPermission")
    fun getLine1Number(
        telephonyManager: TelephonyManager,
        subscriptionManager: SubscriptionManager,
        slotID: Int,
        isMultiSIM: Boolean,
        isPhoneNumberPermissionEnabled: Boolean
    ): String? {
        if (!isPhoneNumberPermissionEnabled) {
            return null
        }
        var phoneNumber: String?
        try {
            phoneNumber =
                if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    telephonyManager.line1Number
                } else {
                    @SuppressLint("MissingPermission") val subscriptionInfo =
                        subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID)
                            ?: return null
                    subscriptionInfo.number
                }
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                phoneNumber = null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            phoneNumber = null
        }
        return phoneNumber
    }

    @SuppressLint("MissingPermission")
    fun getVoiceMailNumber(
        telephonyManager: TelephonyManager,
        subscriptionManager: SubscriptionManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var phoneNumber: String?
        try {
            phoneNumber = if (!isMultiSIM) {
                telephonyManager.voiceMailNumber
            } else {
                @SuppressLint("MissingPermission") val subscriptionInfo =
                    subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID)
                if (subscriptionInfo == null) {
                    return null
                } else {
                    getOutput(
                        telephonyManager, "getVoiceMailNumber", subscriptionInfo.subscriptionId
                    )
                }
            }
            if (phoneNumber.isNullOrEmpty()) {
                phoneNumber = null
            }
        } catch (e: java.lang.Exception) {
            phoneNumber = null
        }
        return phoneNumber
    }


    private fun getOutput(
        telephonyManager: TelephonyManager, methodName: String, slotId: Int
    ): String? {
        val telephonyClass: Class<*>
        var reflectionMethod: String? = null
        var output: String? = null
        try {
            telephonyClass = Class.forName(telephonyManager.javaClass.name)
            for (method in telephonyClass.methods) {
                val name = method.name
                if (name == methodName) {
                    val params = method.parameterTypes
                    if (params.size == 1 && params[0].name == "int") {
                        reflectionMethod = name
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (reflectionMethod != null) {
            try {
                output = getOpByReflection(telephonyManager, reflectionMethod, slotId)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return output
    }

    @Throws(java.lang.Exception::class)
    private fun getOpByReflection(
        telephony: TelephonyManager, predictedMethodName: String, slotID: Int
    ): String? {
        var result: String? = null
        val telephonyClass = Class.forName(telephony.javaClass.name)
        val parameter = arrayOfNulls<Class<*>?>(1)
        parameter[0] = Int::class.javaPrimitiveType
        val getSimID = telephonyClass.getMethod(predictedMethodName, *parameter)
        val ob_phone: Any?
        val obParameter = arrayOfNulls<Any>(1)
        obParameter[0] = slotID
        if (getSimID != null) {
            ob_phone = getSimID.invoke(telephony, *obParameter)
            if (ob_phone != null) {
                result = ob_phone.toString()
            }
        }
        return result
    }


    /**
     * SIM INFO
     */
    fun getSIMMCC(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        return if (!isMultiSIM || Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            val mccmnc = telephonyManager.simOperator
            if (mccmnc.length > 3) {
                mccmnc.substring(0, 3)
            } else {
                null
            }
        } else {
            @SuppressLint("MissingPermission") val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            var mcc: String?
            mcc = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                subscriptionInfo.mccString
            } else {
                String.format(Locale.ENGLISH, "%d", subscriptionInfo.mcc)
            }
            if (mcc == null || mcc == "0") {
                mcc = null
            }
            mcc
        }
    }


    fun getSIMMNC(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        return if (!isMultiSIM) {
            val mccmnc = telephonyManager.simOperator
            if (mccmnc.length > 3) {
                mccmnc.substring(3)
            } else {
                null
            }
        } else {
            @SuppressLint("MissingPermission") val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            var mnc: String?
            mnc = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                subscriptionInfo.mncString
            } else {
                String.format(Locale.ENGLISH, "%d", subscriptionInfo.mnc)
            }
            if (mnc == null || mnc == "0") {
                mnc = null
            }
            mnc
        }
    }

    fun getSIMServiceProviderName(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var spn: String? = null
        return if (!isMultiSIM) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                spn =
                    if (telephonyManager.simSpecificCarrierIdName != null) telephonyManager.simSpecificCarrierIdName.toString() else null
            }
            if (spn == null) {
                spn = telephonyManager.simOperatorName
            }
            if (spn.isNullOrEmpty()) {
                spn = null
            }
            spn
        } else {
            @SuppressLint("MissingPermission") val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                spn =
                    if (t.simSpecificCarrierIdName != null) t.simSpecificCarrierIdName.toString() else null
                if (spn == null) {
                    spn = t.simOperatorName
                }
            } else {
                spn =
                    if (subscriptionInfo.displayName != null) subscriptionInfo.displayName.toString() else null
            }
            spn
        }
    }


    fun getSIMCountryISO(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var countryISO: String?
        if (!isMultiSIM) {
            return telephonyManager.simCountryIso
        } else {
            @SuppressLint("MissingPermission") val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            countryISO = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                t.simCountryIso
            } else {
                subscriptionInfo.countryIso
            }
        }
        if (countryISO.isNullOrEmpty()) {
            countryISO = null
        }
        return countryISO
    }


    @SuppressLint("NewApi")
    fun getICCID(subscriptionManager: SubscriptionManager, slotID: Int): String? {
        @SuppressLint("MissingPermission") val subscriptionInfo =
            subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID)
        return subscriptionInfo?.iccId
    }


    @SuppressLint("NewApi")
    fun isEmbedded(subscriptionManager: SubscriptionManager, slotID: Int): Boolean? {
        @SuppressLint("MissingPermission") val subscriptionInfo =
            subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID)
        return subscriptionInfo?.isEmbedded
    }

    @SuppressLint("MissingPermission")
    fun getIMEIOrMEID(
        telephonyManager: TelephonyManager, slotID: Int
    ): String? {
        var imeiOrMeid: String? = null
        val isIMEI = telephonyManager.phoneType == TelephonyManager.PHONE_TYPE_GSM
        try {
            imeiOrMeid = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                if (isIMEI) {
                    telephonyManager.getImei(slotID)
                } else {
                    telephonyManager.getMeid(slotID)
                }
            } else {
                getOutput(telephonyManager, "getDeviceId", slotID)
            }
            return imeiOrMeid
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getTAC(telephonyManager: TelephonyManager, slot: Int): String? {
        return telephonyManager.getTypeAllocationCode(slot)
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getManufacturerCode(telephonyManager: TelephonyManager, slot: Int): String? {
        return telephonyManager.getManufacturerCode(slot)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    fun getCarrierID(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        val carrierID: Int = if (!isMultiSIM) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                telephonyManager.simSpecificCarrierId
            } else {
                telephonyManager.simCarrierId
            }
        } else {
            @SuppressLint("MissingPermission") val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                t.simSpecificCarrierId
            } else {
                t.simCarrierId
            }
        }
        return if (carrierID > 0) {
            carrierID.toString()
        } else null
    }

    @SuppressLint("MissingPermission")
    fun getGroupIdLevel(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        return if (!isMultiSIM) {
            telephonyManager.groupIdLevel1
        } else {
            @SuppressLint("MissingPermission") val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: null
            return getOutput(
                telephonyManager, "getGroupIdLevel1", subscriptionInfo!!.subscriptionId
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getIMSI(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        if (!isMultiSIM) {
            return telephonyManager.subscriberId
        } else {
            @SuppressLint("MissingPermission") val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            return getOutput(
                telephonyManager, "getSubscriberId", subscriptionInfo.subscriptionId
            )
        }
    }

    fun getDownstreamLinkBandwidth(connectivityManager: ConnectivityManager): Int? {
        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                return networkCapabilities.linkDownstreamBandwidthKbps
            }
        }
        return null
    }

    fun getUpstreamLinkBandwidth(connectivityManager: ConnectivityManager): Int? {
        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                return networkCapabilities.linkUpstreamBandwidthKbps
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    fun getDataState(telephonyManager: TelephonyManager): Int {
        return telephonyManager.dataState
    }

    @SuppressLint("MissingPermission")
    fun getDataActivity(telephonyManager: TelephonyManager): Int {
        return telephonyManager.dataActivity
    }


    /**
     * return TRUE if NOT METERED NETWORK
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    fun getMeteredState(connectivityManager: ConnectivityManager): Boolean? {
        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                return (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) || networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED
                ))
            }
        }
        return null
    }


    @SuppressLint("MissingPermission")
    fun getGeneration(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var type = 0
        if (!isMultiSIM) {
            type = telephonyManager.dataNetworkType
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                type = t.dataNetworkType
            } else {
                val output = getOutput(telephonyManager, "getNetworkType", slotID)
                if (output != null) {
                    type = output.toInt()
                }
            }
        }
        return getNetworkClass(type)
    }

    private fun getNetworkClass(networkType: Int): String? {
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS, 16, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, 17 -> "3G"
            TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> "4G"
            20 -> "5G"
            else -> null
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun getVoiceServiceState(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): Int? {
        var state: ServiceState? = null
        if (!isMultiSIM) {
            state = telephonyManager.serviceState
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                state = t.serviceState
            }
        }
        return state?.state
    }

    @SuppressLint("MissingPermission")
    fun getVoiceServiceState(serviceState: ServiceState?): Int? {
        var state = -1
        if (serviceState != null) {
            state = serviceState.state
        }
        return if (state == -1) {
            null
        } else state
    }

    private fun getNetworkTypeString(value: Int): String? {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            when (value) {
                TelephonyManager.NETWORK_TYPE_GSM -> return "GSM"
                TelephonyManager.NETWORK_TYPE_TD_SCDMA -> return "TD-SCDMA"
                TelephonyManager.NETWORK_TYPE_IWLAN -> return "IWLAN"
                19 -> return "LTE CA"
                20 -> return "5G (None Stand Alone)"
            }
        }
        when (value) {
            TelephonyManager.NETWORK_TYPE_1xRTT -> return "1xRTT"
            TelephonyManager.NETWORK_TYPE_CDMA -> return "CDMA (Either IS95A or IS95B)"
            TelephonyManager.NETWORK_TYPE_EDGE -> return "EDGE"
            TelephonyManager.NETWORK_TYPE_EHRPD -> return "eHRPD"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> return "EVDO revision 0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> return "EVDO revision A"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> return "EVDO revision B"
            TelephonyManager.NETWORK_TYPE_GPRS -> return "GPRS"
            TelephonyManager.NETWORK_TYPE_HSDPA -> return "HSDPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> return "HSPA"
            TelephonyManager.NETWORK_TYPE_HSPAP -> return "HSPAP"
            TelephonyManager.NETWORK_TYPE_HSUPA -> return "HSUPA"
            TelephonyManager.NETWORK_TYPE_IDEN -> return "iDen"
            TelephonyManager.NETWORK_TYPE_LTE -> return "LTE"
            TelephonyManager.NETWORK_TYPE_UMTS -> return "UMTS"
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> return "Uknown"
        }
        return "error"
    }

    private fun getOverrideNetworkTypeString(value: Int): String? {
        when (value) {
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA -> return "LTE Carrier Aggregation"
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> return "LTE Advanced Pro"
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA -> return "5G NSA"
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE -> return "5G NSA MMW"
            TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED -> return "5G+ (Advanced)"
        }
        return "N/A"
    }


    @SuppressLint("MissingPermission")
    fun getVoiceNetworkType(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var type = 0
        if (!isMultiSIM) {
            type = telephonyManager.voiceNetworkType
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                type = t.voiceNetworkType
            } else {
                val output = getOutput(telephonyManager, "getVoiceNetworkType", slotID)
                if (output != null) {
                    type = output.toInt()
                }
            }
        }
        return getNetworkTypeString(type)
    }

    @SuppressLint("MissingPermission")
    fun getDataNetworkType(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean,
        overrideNetworkType: Int
    ): String? {
        var type = 0
        if (!isMultiSIM) {
            type =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && overrideNetworkType != TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE) {
                    return getOverrideNetworkTypeString(overrideNetworkType)
                } else {
                    telephonyManager.dataNetworkType
                }
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && overrideNetworkType != TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE) {
                return getOverrideNetworkTypeString(overrideNetworkType)

            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                type = t.dataNetworkType
            } else {
                val output = getOutput(telephonyManager, "getDataNetworkType", slotID)
                if (output != null) {
                    type = output.toInt()
                }
            }
        }
        return getNetworkTypeString(type)
    }

    @SuppressLint("MissingPermission")
    fun getNetworkSPN(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var spn: String? = if (!isMultiSIM) {
            telephonyManager.networkOperatorName
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                t.networkOperatorName
            } else {
                getOutput(telephonyManager, "getNetworkOperatorName", slotID)
            }
        }
        if (spn == null || spn.isEmpty()) {
            spn = null
        }
        return spn
    }

    /**
     * NETWORK INFO
     */
    @SuppressLint("MissingPermission")
    fun getMCC(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        val mccmnc: String? = if (!isMultiSIM) {
            telephonyManager.networkOperator
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                t.networkOperator
            } else {
                getOutput(telephonyManager, "getNetworkOperatorForPhone", slotID)
            }
        }
        return if (mccmnc != null && mccmnc.length > 3) {
            mccmnc.substring(0, 3)
        } else {
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun getMNC(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var mccmnc: String? = null
        mccmnc = if (!isMultiSIM) {
            telephonyManager.networkOperator
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                t.networkOperator
            } else {
                getOutput(telephonyManager, "getNetworkOperatorForPhone", slotID)
            }
        }
        return if (mccmnc != null && mccmnc.length > 3) {
            mccmnc.substring(3)
        } else {
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun getNetworkCountryCode(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var countryCode: String? = null
        countryCode = if (!isMultiSIM) {
            telephonyManager.networkCountryIso
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                t.networkCountryIso
            } else {
                getOutput(telephonyManager, "getNetworkCountryIso", slotID)
            }
        }
        if (countryCode == null || countryCode.length == 0) {
            countryCode = null
        }
        return countryCode
    }


    @SuppressLint("MissingPermission", "NewApi")
    fun isDataEnabled(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): Boolean? {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) return null

        var isDataEnabled = false
        if (!isMultiSIM) {
            isDataEnabled = telephonyManager.isDataEnabled
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                isDataEnabled = t.isDataEnabled
            } else {
                try {
                    val output = getOutput(telephonyManager, "getDataEnabled", slotID)
                    if (output != null) {
                        isDataEnabled = java.lang.Boolean.parseBoolean(output)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        return isDataEnabled
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun isDataRoamingEnabled(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): Boolean? {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) return null
        var isDataRoamingEnabled = false
        isDataRoamingEnabled = if (!isMultiSIM) {
            telephonyManager.isDataRoamingEnabled
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
            t.isDataRoamingEnabled
        }
        return isDataRoamingEnabled
    }

    @SuppressLint("MissingPermission")
    fun getForbiddenPlmns(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) return null
        var plmns: Array<String>? = null
        if (!isMultiSIM) {
            plmns = telephonyManager.forbiddenPlmns
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID)
                    ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                plmns = t.forbiddenPlmns
            }
        }
        if (plmns.isNullOrEmpty()) {
            return null
        }
        val stringBuilder = java.lang.StringBuilder()
        for (plmn in plmns) {
            stringBuilder.append("$plmn  ")
        }
        return stringBuilder.toString().trim { it <= ' ' }.replace("  ", ", ")
    }

    @SuppressLint("MissingPermission")
    fun getRejectCause(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) return null


        val s: ServiceState? = if (isMultiSIM) {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
            t.serviceState
        } else {
            telephonyManager.serviceState
        }
        try {
            val p1 = "domain=CS[^/]*?rejectCause=(.\\w*)"
            val p2 = "domain=CS[^/]*?reasonForDenial=(.\\w*)"
            var pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE)
            var m = pattern.matcher(s.toString())
            if (!m.find(0)) {
                pattern = Pattern.compile(p2, Pattern.CASE_INSENSITIVE)
                m = pattern.matcher(s.toString())
            }
            if (m.find(0)) {
                val rejectCauseAsString = m.group(1)
                if (rejectCauseAsString != null) {
                    return getRejectReasonString(rejectCauseAsString.toInt())
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }


    private fun getRejectReasonString(cause: Int): String? {
        when (cause) {
            0 -> return "None"
            1 -> return "Authentication Failure"
            2 -> return "IMSI unknown in HLR"
            3 -> return "Illegal MS"
            4 -> return "Illegal ME"
            5 -> return "PLMN not allowed"
            6 -> return "Location area not allowed"
            7 -> return "Roaming not allowed"
            8 -> return "No Suitable Cells in this Location Area"
            9 -> return "Network failure"
            10 -> return "Persistent location update reject"
            11 -> return "PLMN not allowed"
            12 -> return "Location area not allowed"
            13 -> return "Roaming not allowed in this Location Area"
            15 -> return "No Suitable Cells in this Location Area"
            17 -> return "Network Failure"
            20 -> return "MAC Failure"
            21 -> return "Sync Failure"
            22 -> return "Congestion"
            23 -> return "GSM Authentication unacceptable"
            25 -> return "Not Authorized for this CSG"
            32 -> return "Service option not supported"
            33 -> return "Requested service option not subscribed"
            34 -> return "Service option temporarily out of order"
            38 -> return "Call cannot be identified"
            95 -> return "Semantically incorrect message"
            96 -> return "Invalid mandatory information"
            97 -> return "Message type non-existent or not implemented"
            98 -> return "Message type not compatible with protocol state"
            99 -> return "Information element non-existent or not implemented"
            100 -> return "Conditional IE error"
            101 -> return "Message not compatible with protocol state"
            111 -> return "Protocol error"
        }
        return if (cause in 48..63) {
            "Retry upon entry into a new cell"
        } else "No Rejection Detected"
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission", "WrongConstant")
    fun getLTECADuplexMode(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var duplexMode = -1
        duplexMode = if (!isMultiSIM) {
            telephonyManager.serviceState!!.duplexMode
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID)
                    ?: return null
            val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                ?: return null
            t.serviceState!!.duplexMode
        }
        return if (duplexMode < 0) {
            null
        } else getDuplexModeString(duplexMode)
    }


    private fun getDuplexModeString(value: Int): String? {
        return when (value) {
            ServiceState.DUPLEX_MODE_FDD -> "FDD"
            ServiceState.DUPLEX_MODE_TDD -> "TDD"
            else -> "Unknown"
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    fun getLTECABandwidths(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): String? {
        var bandwidths: IntArray? = null
        if (!isMultiSIM) {
            bandwidths = telephonyManager.serviceState!!.cellBandwidths
        } else {
            val subscriptionInfo =
                subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID) ?: return null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                bandwidths = t.serviceState!!.cellBandwidths
            }
        }
        if (bandwidths == null || bandwidths.isEmpty()) {
            return null
        }
        val stringBuilder = java.lang.StringBuilder()
        var firstBandwidth = true
        for (bandwidth in bandwidths) {
            if (!firstBandwidth) stringBuilder.append(", ")
            firstBandwidth = false
            stringBuilder.append(bandwidth)
            stringBuilder.append(" kHz")
        }
        return stringBuilder.toString()
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    fun get5GServiceState(
        subscriptionManager: SubscriptionManager,
        telephonyManager: TelephonyManager,
        slotID: Int,
        isMultiSIM: Boolean
    ): ServiceState? {
        var s: ServiceState? = null
        if (isMultiSIM) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val subscriptionInfo =
                    subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotID)
                        ?: return null
                val t = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                s = t.serviceState
            }
        } else {
            s = telephonyManager.serviceState
        }
        return s
    }

    /**
     * @param serviceState
     * @return nr state as integer.
     */
    fun getENDCStatus(serviceState: ServiceState): String? {
        // TODO if this throws exception in Q then we should only parse the string.
        try {
            for (method in serviceState.javaClass.declaredMethods) {
                if (method.name.lowercase(Locale.getDefault()).contains("getendcstat")) {
                    method.isAccessible = true
                    val invoke = method.invoke(serviceState, *arrayOfNulls(0)) as Int
                    if (invoke != null) {
                        if (invoke < 0) {
                            return null
                        }
                        return if (invoke == 1) "SUPPORTED" else "NOT SUPPORTED"
                    }
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        // WILL ATTEMPT TO PARSE STRING AND SEARCH FOR KNOWN PATTERNS
        return parseENDCStatusFromString(serviceState.toString())
    }


    private fun parseENDCStatusFromString(serviceStateString: String?): String? {
        if (serviceStateString != null) {
            try {
                val p1 = "[^/]*?EndcStatus=(\\w+)\\s"
                val p2 = "domain=PS[^/]*?endcAvailable\\s=\\s(\\w+)"
                val p3 = "domain=PS[^/]*?endcAvailable=(\\w+)\\s"
                var pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE)
                var m = pattern.matcher(serviceStateString)
                if (!m.find(0)) {
                    pattern = Pattern.compile(p2, Pattern.CASE_INSENSITIVE)
                    m = pattern.matcher(serviceStateString)
                }
                if (!m.find(0)) {
                    pattern = Pattern.compile(p3, Pattern.CASE_INSENSITIVE)
                    m = pattern.matcher(serviceStateString)
                }
                if (m.find(0)) {
                    val g1 = m.group(1)
                    if (g1 != null) {
                        val result = g1 == "1" || g1 == "true"
                        return if (result) "SUPPORTED" else "NOT SUPPORTED"
                    }
                }
            } catch (e: java.lang.Exception) {
            }
        }
        return null
    }

    /**
     * @param serviceState
     * @return nr state as integer.
     */
    fun get5GStatus(serviceState: ServiceState): String? {
        try {
            for (method in serviceState.javaClass.declaredMethods) {
                // getnrstat will cover all known cases getNrStatus and getNrState
                if (method.name.lowercase(Locale.getDefault()).contains("getnrstat")) {
                    method.isAccessible = true
                    val invoke = method.invoke(serviceState, *arrayOfNulls(0)) as Int
                    if (invoke != null) {
                        when (invoke) {
                            0 -> return "NONE"
                            1 -> return "RESTRICTED"
                            2 -> return "NOT RESTRICTED"
                            3 -> return "CONNECTED"
                        }
                        return null
                    }
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        // WILL ATTEMPT TO PARSE STRING AND SEARCH FOR KNOWN PATTERNS
        return parseNRStatusFromString(serviceState.toString())
    }

    private fun parseNRStatusFromString(serviceStateString: String?): String? {
        var status: String? = null
        if (serviceStateString != null) {
            try {
                val p1 = "domain=PS[^/]*?nrStat\\w+=(\\w+)"
                val pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE)
                val m = pattern.matcher(serviceStateString)
                if (m.find(0)) {
                    status = getNrStatusIntFromString(m.group(1))
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return status
    }

    private fun getNrStatusIntFromString(statusString: String?): String? {
        if (statusString == null) return null
        val RESTRICTED = "RESTRICTED"
        val NOT_RESTRICTED = "NOT_RESTRICTED"
        val CONNECTED = "CONNECTED"
        return when (statusString) {
            NOT_RESTRICTED -> "NOT RESTRICTED"
            CONNECTED -> "CONNECTED"
            RESTRICTED -> "RESTRICTED"
            else -> "NONE"
        }
    }

    fun getNRFrequency(serviceState: ServiceState): String? {
        try {
            for (method in serviceState.javaClass.declaredMethods) {
                if (method.name == "getNrFrequencyRange") {
                    method.isAccessible = true
                    val freq = method.invoke(serviceState, *arrayOfNulls(0)) as Int
                    if (freq != null) {
                        when (freq) {
                            1 -> return "Below 1GHz"
                            2 -> return "1GHz - 3GHz"
                            3 -> return "3GHz - 6GHz"
                            4 -> return "millimeter Wave"
                        }
                        return null
                    }
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return parseNRFreqRangeFromString(serviceState.toString())
    }

    private fun parseNRFreqRangeFromString(serviceStateString: String?): String? {
        if (serviceStateString != null) {
            try {
                val p1 = "mNrFrequencyRange=(-?[0-9])"
                val pattern = Pattern.compile(p1, Pattern.CASE_INSENSITIVE)
                val m = pattern.matcher(serviceStateString)
                if (m.find(0)) {
                    val g1 = m.group(1)
                    if (g1 != null) {
                        val freq = g1.toInt()
                        when (freq) {
                            1 -> return "Below 1GHz"
                            2 -> return "1GHz - 3GHz"
                            3 -> return "3GHz - 6GHz"
                            4 -> return "Millimeter Wave"
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun getMobileBandForLTE(earfcn: Int): String? {
        if (earfcn > 67535) {
            return "ERR"
        } else if (earfcn >= 67366) {
            return "67CA" // band 67 only for CarrierAgg
        } else if (earfcn >= 66436) {
            return "66"
        } else if (earfcn >= 65536) {
            return "65"
        } else if (earfcn > 54339) {
            return "ERR"
        } else if (earfcn >= 46790 /* inferred from the end range of BAND_45 */) {
            return "46"
        } else if (earfcn >= 46590) {
            return "45"
        } else if (earfcn >= 45590) {
            return "44"
        } else if (earfcn >= 43590) {
            return "43"
        } else if (earfcn >= 41590) {
            return "42"
        } else if (earfcn >= 39650) {
            return "41"
        } else if (earfcn >= 38650) {
            return "40"
        } else if (earfcn >= 38250) {
            return "39"
        } else if (earfcn >= 37750) {
            return "38"
        } else if (earfcn >= 37550) {
            return "37"
        } else if (earfcn >= 36950) {
            return "36"
        } else if (earfcn >= 36350) {
            return "35"
        } else if (earfcn >= 36200) {
            return "34"
        } else if (earfcn >= 36000) {
            return "33"
        } else if (earfcn > 10359) {
            return "ERR"
        } else if (earfcn >= 9920) {
            return "32CA"
        } else if (earfcn >= 9870) {
            return "31"
        } else if (earfcn >= 9770) {
            return "30"
        } else if (earfcn >= 9660) {
            return "29CA"
        } else if (earfcn >= 9210) {
            return "28"
        } else if (earfcn >= 9040) {
            return "27"
        } else if (earfcn >= 8690) {
            return "26"
        } else if (earfcn >= 8040) {
            return "25"
        } else if (earfcn >= 7700) {
            return "24"
        } else if (earfcn >= 7500) {
            return "23"
        } else if (earfcn >= 6600) {
            return "22"
        } else if (earfcn >= 6450) {
            return "21"
        } else if (earfcn >= 6150) {
            return "20"
        } else if (earfcn >= 6000) {
            return "19"
        } else if (earfcn >= 5850) {
            return "18"
        } else if (earfcn >= 5730) {
            return "17"
        } else if (earfcn > 5379) {
            return "ERR"
        } else if (earfcn >= 5280) {
            return "14"
        } else if (earfcn >= 5180) {
            return "13"
        } else if (earfcn >= 5010) {
            return "12"
        } else if (earfcn >= 4750) {
            return "11"
        } else if (earfcn >= 4150) {
            return "10"
        } else if (earfcn >= 3800) {
            return "9"
        } else if (earfcn >= 3450) {
            return "8"
        } else if (earfcn >= 2750) {
            return "7"
        } else if (earfcn >= 2650) {
            return "6"
        } else if (earfcn >= 2400) {
            return "5"
        } else if (earfcn >= 1950) {
            return "4"
        } else if (earfcn >= 1200) {
            return "3"
        } else if (earfcn >= 600) {
            return "2"
        } else if (earfcn >= 0) {
            return "1"
        }
        return "ERR"
    }

    fun getAllPhoneInfoForExport(
        context: Context,
        basicModel: BasicPhoneModel,
        simInfos: List<SIMInfoModel>,
        networkInfos: CellNetworkModel,
        cellInfos: List<CellInfo>,
        config: List<Pair<String, String>>
    ): List<UIObject>? {
        val fullList: MutableList<UIObject> = java.util.ArrayList()

        fullList.add(UIObject(context.getString(R.string.activity_title_phone_info), ""))
        fullList.addAll(BasicPhoneModel.getUIObjects(context, basicModel))

        fullList.add(UIObject("", ""))
        fullList.add(UIObject("", ""))
        fullList.addAll(SIMInfoModel.getUIObjects(context, simInfos))


        fullList.add(UIObject("", ""))
        fullList.add(UIObject("", ""))
        fullList.add(UIObject(context.getString(R.string.active_network_info), ""))
        fullList.addAll(CellNetworkModel.toUIModelList(context, networkInfos))

        fullList.add(UIObject("", ""))
        fullList.add(UIObject("", ""))
        fullList.add(UIObject(context.getString(R.string.connected_cell_info), ""))
        fullList.addAll(CellModel.toUIModelList(context, cellInfos))

        fullList.add(UIObject("", ""))
        fullList.add(UIObject("", ""))
        fullList.add(UIObject(context.getString(R.string.carrier_config_long), ""))
        fullList.addAll(Utils.getUIObjectsFromBuildProps(context,config))
        return fullList
    }
}