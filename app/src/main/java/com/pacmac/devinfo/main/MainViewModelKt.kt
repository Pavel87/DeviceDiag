package com.pacmac.devinfo.main

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.AboutActivity
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UpToDateEnum
import com.pacmac.devinfo.battery.BatteryInfoKt
import com.pacmac.devinfo.camera.CameraInfoKt
import com.pacmac.devinfo.cellular.CellularInfoKt
import com.pacmac.devinfo.cpu.CPUInfoKt
import com.pacmac.devinfo.display.DisplayInfoKt
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.gps.ui.GPSInfoKt
import com.pacmac.devinfo.main.data.AppRepository
import com.pacmac.devinfo.main.model.DashItem
import com.pacmac.devinfo.main.model.DashModel
import com.pacmac.devinfo.main.model.MainInfoModel
import com.pacmac.devinfo.main.model.PermissionCheckModel
import com.pacmac.devinfo.main.model.PermissionState
import com.pacmac.devinfo.sensor.SensorInfoKt
import com.pacmac.devinfo.storage.StorageInfoKt
import com.pacmac.devinfo.utils.Utils
import com.pacmac.devinfo.wifi.NetworkInfoKt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModelKt @Inject constructor(
    private val telephonyManager: TelephonyManager,
    private val appRepository: AppRepository,
    private val packageManager: PackageManager
) : ViewModel() {

    private var isExporting = false
    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    var _appUpdateStatus = mutableStateOf(UpToDateEnum.UNKNOWN)
    val appUpdateStatus: State<UpToDateEnum> = _appUpdateStatus


    private val _onGPSNotAvailable = MutableSharedFlow<Unit>()
    val onGPSNotAvailable = _onGPSNotAvailable.asSharedFlow()

    var hasGPS = true

    private val _onNavigateToScreen = MutableSharedFlow<Class<*>>()
    val onNavigateToScreen = _onNavigateToScreen.asSharedFlow()

    private val _onPermissionCheck = MutableSharedFlow<PermissionCheckModel>()
    val onPermissionCheck = _onPermissionCheck.asSharedFlow().onEach { permissionRequest = it }

    private val _mainInfo = mutableStateOf<MainInfoModel?>(null)
    val mainInfo: State<MainInfoModel?> = _mainInfo

    val _isLocationPermissionEnabled = mutableStateOf(true)
    val isLocationPermissionEnabled: State<Boolean> = _isLocationPermissionEnabled

    val _isPhonePermissionEnabled = mutableStateOf(true)
    val isPhonePermissionEnabled: State<Boolean> = _isPhonePermissionEnabled
    val _isStoragePermissionEnabled = mutableStateOf(true)
    val isStoragePermissionEnabled: State<Boolean> = _isStoragePermissionEnabled
    val _isCameraPermissionEnabled = mutableStateOf(true)
    val isCameraPermissionEnabled: State<Boolean> = _isCameraPermissionEnabled
    val _isPhoneNumberPermissionEnabled = mutableStateOf(true)
    val isPhoneNumberPermissionEnabled: State<Boolean> = _isPhoneNumberPermissionEnabled

    var permissionRequest: PermissionCheckModel? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.getLatestAppUpdate()
                .collect { _appUpdateStatus.value = it }
        }
    }

    fun updatePermissionState(permission: String, state: PermissionState) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.getPermissionStatus(permission)
                .collect {
                    if (it == PermissionState.RATIONAL_DISPLAYED && state == PermissionState.DENIED) {
                        appRepository.updatePermissionStatus(
                            permission,
                            PermissionState.DENIED_FOREVER
                        )
                    } else {
                        appRepository.updatePermissionStatus(permission, state)
                    }
                }
        }


    }

    private suspend fun getPermissionState(
        permission: String,
        function: (PermissionState) -> Unit
    ) {
        appRepository.getPermissionStatus(permission)
            .collectLatest { function.invoke(it) }
    }

    // REFACTOR EXPORT logic
    fun export(context: Context) {
        if (!isExporting) {
            isExporting = true
            ExportTask(context, MainUtilsKt.EXPORT_FILE_NAME) {
                viewModelScope.launch {
                    isExporting = false
                    _onExportDone.emit(it)
                }
            }.execute(this)
        }
    }

    fun loadMainInfo(hasReadPhoneStatePermission: Boolean) {
        _mainInfo.value = MainInfoModel(
            MainUtilsKt.getOsVersion(),
            MainUtilsKt.getModel(),
            MainUtilsKt.getManufacturer(),
            MainUtilsKt.getSerialNumber(hasReadPhoneStatePermission),
            MainUtilsKt.getBuildNumber(),
            MainUtilsKt.getHardware(),
            MainUtilsKt.getSimCount(telephonyManager),
            MainUtilsKt.getRadioFirmware(),
            MainUtilsKt.getBootloader(),
            MainUtilsKt.getDeviceLanguageSetting(),
            MainUtilsKt.getDeviceLanguageLocale(),
        )
    }

    fun getDashboarItems(): List<DashModel> {
        val list = arrayListOf<DashModel>()
        list.add(DashModel(
            DashItem.CPU_SCREEN, R.drawable.cpuimg, CPUInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.RAM_SCREEN, R.drawable.ramimg, StorageInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.BAT_SCREEN, R.drawable.batimg, BatteryInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.CAM_SCREEN, R.drawable.camimg, CameraInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.GPS_SCREEN, R.drawable.gpsimg, GPSInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.CELL_SCREEN, R.drawable.simimg, CellularInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.SENSOR_SCREEN, R.drawable.sensorimg, SensorInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.DISPLAY_SCREEN, R.drawable.displayimg, DisplayInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.WIFI_SCREEN, R.drawable.wifiimg, NetworkInfoKt::class.java
        ) { onTileClick(it) })
        list.add(DashModel(
            DashItem.ABOUT_SCREEN, R.drawable.aboutimg, AboutActivity::class.java
        ) { onTileClick(it) })
        return list
    }

    private suspend fun onTileClick(tile: DashModel) {
        when (tile.dashItem) {
            DashItem.CPU_SCREEN -> {
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.RAM_SCREEN -> {
                if (!isStoragePermissionEnabled.value) {
                    checkPermission(
                        Utils.STORAGE_PERMISSION,
                        R.string.storage_permission_msg,
                        R.string.storage_feature_disabled
                    )
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.BAT_SCREEN -> {
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.CAM_SCREEN -> {
                if (!isCameraPermissionEnabled.value) {
                    checkPermission(
                        Utils.CAMERA_PERMISSION,
                        R.string.cam_permission_msg,
                        R.string.camera_feature_disabled
                    )
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.GPS_SCREEN -> if (hasGPS) {
                if (!isLocationPermissionEnabled.value) {
                    checkPermission(
                        Utils.LOCATION_PERMISSION,
                        R.string.location_permission_msg,
                        R.string.location_feature_disabled
                    )
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            } else {
                _onGPSNotAvailable.emit(Unit)
            }

            DashItem.CELL_SCREEN -> {
                if (!isPhonePermissionEnabled.value) {
                    checkPermission(
                        Utils.PHONE_PERMISSION,
                        R.string.phone_permission_msg,
                        R.string.phone_permission_msg
                    )
                    return
                }

                if (!isLocationPermissionEnabled.value) {
                    checkPermission(
                        Utils.LOCATION_PERMISSION,
                        R.string.location_permission_msg,
                        R.string.location_feature_disabled
                    )
                    return
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !isPhoneNumberPermissionEnabled.value) {
                    checkPermission(
                        Utils.PHONE_NUMBER_PERMISSION,
                        R.string.phone_number_permission_msg,
                        R.string.phone_number_permission_msg
                    )
                    return
                }

                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.SENSOR_SCREEN -> {
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.DISPLAY_SCREEN -> {
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.WIFI_SCREEN -> {
                if (!isLocationPermissionEnabled.value) {
                    checkPermission(
                        Utils.LOCATION_PERMISSION,
                        R.string.location_permission_msg,
                        R.string.location_feature_disabled
                    )
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.ABOUT_SCREEN -> {
                _onNavigateToScreen.emit(tile.actClass)
            }
        }
    }

    private suspend fun checkPermission(permission: String, msgRes: Int, disabledMsg: Int) {
        getPermissionState(permission) { permissionState ->
            viewModelScope.launch {
                _onPermissionCheck.emit(
                    PermissionCheckModel(msgRes, disabledMsg, arrayOf(permission), permissionState)
                )
            }
        }
    }

    suspend fun checkLocationPermission() {
        if (!isLocationPermissionEnabled.value) {
            checkPermission(
                Utils.LOCATION_PERMISSION,
                R.string.location_permission_msg,
                R.string.location_feature_disabled
            )
            return
        }
    }

    suspend fun checkIfAppUpdated(): Boolean {
        val storedVersionCode = appRepository.getLastStoredAppVersion().firstOrNull()
        var appVersionCode = -1
        try {
            appVersionCode = MainUtilsKt.getappVersionCode(packageManager)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (appVersionCode != storedVersionCode) {
            appRepository.updateLastAppVersion(appVersionCode)
            return storedVersionCode != 0
        }
        return false
    }

    fun appUpgradeModalDisplayed() {
        _appUpdateStatus.value = UpToDateEnum.UNKNOWN
        appRepository.appUpgradeModalDisplayed()
    }
}