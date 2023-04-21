package com.pacmac.devinfo.cellular

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.CellInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.cellular.model.BasicPhoneModel
import com.pacmac.devinfo.cellular.model.CellNetworkModel
import com.pacmac.devinfo.cellular.model.Radio
import com.pacmac.devinfo.cellular.model.SIMInfoModel
import com.pacmac.devinfo.export.ExportTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CellularViewModelKt @Inject constructor(
    private val telephonyManager: TelephonyManager,
    private val subscriptionManager: SubscriptionManager,
    private val connectivityManager: ConnectivityManager,
    private val pslKt: PSLKt
) : ViewModel() {

    val EXPORT_FILE_NAME = "phone_cellular_info";

    private var isPhoneNumberPermissionEnabled = false

    private val _basicInfo = mutableStateOf<BasicPhoneModel?>(null)
    val basicInfo: State<BasicPhoneModel?> = _basicInfo

    private val _simInfos = mutableStateOf<List<SIMInfoModel>>(emptyList())
    val simInfos: State<List<SIMInfoModel>> = _simInfos

    private val _networkInfos = mutableStateOf<CellNetworkModel?>(null)
    val networkInfos: State<CellNetworkModel?> = _networkInfos

    private val _cellInfos = mutableStateOf<List<CellInfo>?>(null)
    val cellInfos: State<List<CellInfo>?> = _cellInfos

    init {
        observeBasicPhoneInfo()
        loadNetworkInfo()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            retrieveCarrierConfig()
        }

        observeServiceUpdates()
        observeNetworkUpdates()
        observeCellUpdates()
        observeFullRefresh()
    }

    private fun observeServiceUpdates() {
        viewModelScope.launch {
            pslKt.onServiceStateUpdate.onEach {
                loadNetworkInfo()
                loadCellInfos()
            }.collect()
        }
    }

    private fun observeNetworkUpdates() {
        viewModelScope.launch {
            pslKt.updateNetwork.onEach {
                loadNetworkInfo()
                loadCellInfos()
            }.collect()
        }
    }

    private fun observeCellUpdates() {
        viewModelScope.launch {
            pslKt.updateCellInfos.onEach {
                loadCellInfos()
            }.collect()
        }
    }

    private fun observeFullRefresh() {
        viewModelScope.launch {
            pslKt.refreshAll.onEach {
                observeBasicPhoneInfo()
                loadSIMInfos(isPhoneNumberPermissionEnabled)
                loadNetworkInfo()
                loadCellInfos()
            }.collect()
        }
    }

    private var isExporting = false
    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    // REFACTOR EXPORT logic
    fun export(context: Context) {
        if (!isExporting) {
            isExporting = true
            ExportTask(context, EXPORT_FILE_NAME) {
                viewModelScope.launch {
                    isExporting = false
                    _onExportDone.emit(it)
                }
            }.execute(this)
        }
    }

    private fun observeBasicPhoneInfo() {
        viewModelScope.launch {
            loadBasicPhoneInfo()
                .onEach {
                    _basicInfo.value = it
                }
                .launchIn(viewModelScope)
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadBasicPhoneInfo() = flow {
        val slotCount = MobileNetworkUtilKt.getSIMCount(telephonyManager)
        val phoneRadioType = MobileNetworkUtilKt.getPhoneRadio(telephonyManager)
        val swVersion = MobileNetworkUtilKt.getDeviceSoftwareVersion(telephonyManager)

        val isSMSCapable = telephonyManager.isSmsCapable
        val isVoiceCapable = telephonyManager.isVoiceCapable


        val isConcurrentVoiceAndDataSupported =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                telephonyManager.isConcurrentVoiceAndDataSupported
            } else null

        val isRttSupported: Boolean?
        val isMultiSimSupported: Int?
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            isRttSupported = telephonyManager.isRttSupported
            isMultiSimSupported = telephonyManager.isMultiSimSupported
        } else {
            isRttSupported = null
            isMultiSimSupported = null
        }
        val isWorldPhone = telephonyManager.isWorldPhone

        emit(
            BasicPhoneModel(
                slotCount,
                phoneRadioType,
                swVersion,
                isSMSCapable,
                isVoiceCapable,
                isConcurrentVoiceAndDataSupported,
                isRttSupported,
                isMultiSimSupported,
                isWorldPhone
            )
        )
    }

    fun observeSIMInfo(isPhoneNumberPermissionEnabled: Boolean) {
        this.isPhoneNumberPermissionEnabled = isPhoneNumberPermissionEnabled
        loadSIMInfos(isPhoneNumberPermissionEnabled)
    }

    private fun loadSIMInfos(isPhoneNumberPermissionEnabled: Boolean) {
        var slotCount = 0
        try {
            slotCount = MobileNetworkUtilKt.getSIMCount(telephonyManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val isMultiSIM = slotCount > 1

        val simList = arrayListOf<SIMInfoModel>()

        for (i in 0 until slotCount) {

            val simState = MobileNetworkUtilKt.getSimState(telephonyManager, i, isMultiSIM)

            val phoneNumber = MobileNetworkUtilKt.getLine1Number(
                telephonyManager,
                subscriptionManager,
                i,
                isMultiSIM,
                isPhoneNumberPermissionEnabled
            )

            val voiceMailNumber = MobileNetworkUtilKt.getVoiceMailNumber(
                telephonyManager,
                subscriptionManager,
                i,
                isMultiSIM
            )

            val serviceProvider = MobileNetworkUtilKt.getSIMServiceProviderName(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )
            val simMCC =
                MobileNetworkUtilKt.getSIMMCC(subscriptionManager, telephonyManager, i, isMultiSIM)
            val simMNC =
                MobileNetworkUtilKt.getSIMMNC(subscriptionManager, telephonyManager, i, isMultiSIM)
            val simCountryISO = MobileNetworkUtilKt.getSIMCountryISO(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )

            val isGSM = telephonyManager.phoneType

            var imeiOrMeid: String? = null
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && !Build.VERSION.RELEASE.contains("10")) {
                imeiOrMeid = MobileNetworkUtilKt.getIMEIOrMEID(telephonyManager, i)
            }

            var tac: String? = null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                tac = MobileNetworkUtilKt.getTAC(telephonyManager, i)
            }

            var manufacturerCode: String? = null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                val isCDMA = isGSM == TelephonyManager.PHONE_TYPE_CDMA
                if (isCDMA) {
                    manufacturerCode = MobileNetworkUtilKt.getManufacturerCode(telephonyManager, i)
                }
            }
            var carrierId: String? = null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                carrierId = MobileNetworkUtilKt.getCarrierID(
                    subscriptionManager,
                    telephonyManager,
                    i,
                    isMultiSIM
                )
            }

            var getGroupIdLevel: String? = null
            try {
                getGroupIdLevel = MobileNetworkUtilKt.getGroupIdLevel(
                    subscriptionManager,
                    telephonyManager,
                    i,
                    isMultiSIM
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val iccid = MobileNetworkUtilKt.getICCID(subscriptionManager, i)

            var imsi: String? = null
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                imsi = MobileNetworkUtilKt.getIMSI(
                    subscriptionManager,
                    telephonyManager,
                    i,
                    isMultiSIM
                )
            }

            var isEmbedded: Boolean? = null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                isEmbedded = MobileNetworkUtilKt.isEmbedded(subscriptionManager, i)
            }

            simList.add(
                SIMInfoModel(
                    simState = simState,
                    phoneNumber = phoneNumber,
                    voiceMailNumber = voiceMailNumber,
                    serviceProvider = serviceProvider,
                    SIMMcc = simMCC,
                    SIMMnc = simMNC,
                    simCountryISO = simCountryISO,
                    isGSMPhone = isGSM,
                    imeiOrMeid = imeiOrMeid,
                    tac = tac,
                    manufacturerCode = manufacturerCode,
                    carrierId = carrierId,
                    getGroupIdLevel = getGroupIdLevel,
                    iccid = iccid,
                    imsi = imsi,
                    isEmbedded = isEmbedded,
                )
            )
        }
        _simInfos.value = simList
    }

    private fun loadNetworkInfo() {
        var slotCount = 0
        try {
            slotCount = MobileNetworkUtilKt.getSIMCount(telephonyManager)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val isMultiSIM = slotCount > 1
        var downstreamLinkBandwidth: Int? = null
        var upstreamLinkBandwidth: Int? = null

        if (telephonyManager.dataState != TelephonyManager.DATA_DISCONNECTED) {
            downstreamLinkBandwidth =
                MobileNetworkUtilKt.getDownstreamLinkBandwidth(connectivityManager)
            upstreamLinkBandwidth =
                MobileNetworkUtilKt.getUpstreamLinkBandwidth(connectivityManager)
        }
        val dataState = MobileNetworkUtilKt.getDataState(telephonyManager)
        val dataActivity = MobileNetworkUtilKt.getDataActivity(telephonyManager)

        var isNotMetered: Boolean? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && telephonyManager.dataState != TelephonyManager.DATA_DISCONNECTED) {
            isNotMetered = MobileNetworkUtilKt.getMeteredState(connectivityManager)
        }

        val radios = arrayListOf<Radio>()
        for (i in 0 until slotCount) {
            val radioIndex = i

            // generation
            val generation = MobileNetworkUtilKt.getGeneration(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )
            val is4G = generation?.contains("4G") ?: false
            val is5G = generation?.contains("5G") ?: false

            // service state
            val serviceState = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                MobileNetworkUtilKt.getVoiceServiceState(
                    subscriptionManager,
                    telephonyManager,
                    i,
                    isMultiSIM
                )
            } else {
                MobileNetworkUtilKt.getVoiceServiceState(pslKt.onServiceStateUpdate.value)
            }

            // network type
            val voiceNetworkType = MobileNetworkUtilKt.getVoiceNetworkType(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )
            val dataNetworkType = MobileNetworkUtilKt.getDataNetworkType(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM,
                pslKt.overrideNetworkType
            )

            // SPN
            val networkSPN = MobileNetworkUtilKt.getNetworkSPN(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )
            // mcc
            val mcc =
                MobileNetworkUtilKt.getMCC(subscriptionManager, telephonyManager, i, isMultiSIM)
            // mnc
            val mnc =
                MobileNetworkUtilKt.getMNC(subscriptionManager, telephonyManager, i, isMultiSIM)

            // network CC
            val countryCode = MobileNetworkUtilKt.getNetworkCountryCode(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )?.run { uppercase() }
            // data enabled
            val isDataEnabled = MobileNetworkUtilKt.isDataEnabled(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )
            // data roaming enabled

            val isDataRoamingEnabled = MobileNetworkUtilKt.isDataRoamingEnabled(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )

            // forbidden PLMNs
            val plmns = MobileNetworkUtilKt.getForbiddenPlmns(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )

            // Reject Cause for Data Network
            val rejectCause = MobileNetworkUtilKt.getRejectCause(
                subscriptionManager,
                telephonyManager,
                i,
                isMultiSIM
            )


            var lteCADuplex: String? = null
            var lteCABandwidths: String? = null
            var endcStatus: String? = null
            var fiveGStatus: String? = null
            var nrFrequency: String? = null

            // LTE CA bandwidths
            if ((is4G || is5G) && Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                lteCADuplex = MobileNetworkUtilKt.getLTECADuplexMode(
                    subscriptionManager,
                    telephonyManager,
                    i,
                    isMultiSIM
                )

                lteCABandwidths = MobileNetworkUtilKt.getLTECABandwidths(
                    subscriptionManager,
                    telephonyManager,
                    i,
                    isMultiSIM
                )

                val serviceState5G = MobileNetworkUtilKt.get5GServiceState(
                    subscriptionManager,
                    telephonyManager,
                    i,
                    isMultiSIM
                )

                serviceState5G?.let {
                    endcStatus = MobileNetworkUtilKt.getENDCStatus(serviceState5G)
                    fiveGStatus = MobileNetworkUtilKt.get5GStatus(serviceState5G)
                    nrFrequency = MobileNetworkUtilKt.getNRFrequency(serviceState5G)
                }
            }

            radios.add(
                Radio(
                    radioIndex,
                    generation,
                    serviceState,
                    voiceNetworkType,
                    dataNetworkType,
                    networkSPN,
                    mcc,
                    mnc,
                    countryCode,
                    isDataEnabled,
                    isDataRoamingEnabled,
                    plmns,
                    rejectCause,
                    lteCADuplex,
                    lteCABandwidths,
                    endcStatus,
                    fiveGStatus,
                    nrFrequency,
                    is4G || is5G
                )
            )
        }

        val networkInfoModel = CellNetworkModel(
            downstreamLinkBandwidth,
            upstreamLinkBandwidth,
            dataState,
            dataActivity,
            isNotMetered,
            radios
        )

        _networkInfos.value = networkInfoModel
    }

    private var _carrierConfig: List<Pair<String, String?>> = emptyList()

    private val _filteredCarrierConfig = mutableStateOf(_carrierConfig)
    val filteredCarrierConfig: State<List<Pair<String, String?>>> = _filteredCarrierConfig

    @RequiresApi(Build.VERSION_CODES.O)
    fun retrieveCarrierConfig() {
        viewModelScope.launch {
            loadCarrierConfig()
                .onEach {
                    launch(Dispatchers.Main) {
                        _carrierConfig = it
                        _filteredCarrierConfig.value = it
                    }
                }.collect()
        }
    }

    fun filterProperties(searchTerm: String) {
        _filteredCarrierConfig.value = _carrierConfig.filter { it.first.contains(searchTerm, true) }
    }

    fun getSizeOfTheList() = _carrierConfig.size

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun loadCarrierConfig() = flow {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            emit(MobileNetworkUtilKt.getCarrierConfig(telephonyManager))
        } else {
            emit(emptyList())
        }
    }

    fun registerPSL() {
        println("PACMAC - REGISTER PSL")
        telephonyManager.listen(pslKt, PSLKt.getPSLListenerFlags())
    }

    fun unregisterPSL() = telephonyManager.listen(pslKt, PSLKt.getStopPslFlag())


    private fun loadCellInfos() {
        viewModelScope.launch(Dispatchers.IO) {
            val cells = getAllCellInfo()
            if (cells.isNullOrEmpty()) return@launch
            parseCellInfos(cells)
        }
    }

    @SuppressLint("MissingPermission")
    fun getAllCellInfo(): List<CellInfo?>? {
        try {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                telephonyManager.allCellInfo
            } else {
                CellInfoFutureTask.getAllCellInfoBlocking(telephonyManager)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }


    private suspend fun parseCellInfos(cells: List<CellInfo?>) {
        val cellList = arrayListOf<CellInfo>()
        cells.forEach { cell ->
            cell ?: return@forEach
            cellList.add(cell)
        }
        withContext(Dispatchers.Main) {
            _cellInfos.value = cellList
        }
    }

}
