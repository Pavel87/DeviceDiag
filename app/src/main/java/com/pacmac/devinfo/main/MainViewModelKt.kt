package com.pacmac.devinfo.main

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.AboutActivity
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UpToDateEnum
import com.pacmac.devinfo.audio.AudioInfoActivity
import com.pacmac.devinfo.battery.BatteryInfoKt
import com.pacmac.devinfo.bluetooth.BluetoothInfoActivity
import com.pacmac.devinfo.camera.CameraInfoKt
import com.pacmac.devinfo.cellular.CellularInfoKt
import com.pacmac.devinfo.cpu.CPUInfoKt
import com.pacmac.devinfo.display.DisplayInfoKt
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.gps.ui.GPSInfoKt
import com.pacmac.devinfo.gpu.GPUInfoKt
import com.pacmac.devinfo.main.data.AppRepository
import com.pacmac.devinfo.main.model.DashItem
import com.pacmac.devinfo.main.model.DashModel
import com.pacmac.devinfo.main.model.MainInfoModel
import com.pacmac.devinfo.main.model.PermissionCheckModel
import com.pacmac.devinfo.main.model.PermissionState
import com.pacmac.devinfo.sensor.SensorInfoKt
import com.pacmac.devinfo.storage.StorageInfoKt
import com.pacmac.devinfo.thermal.ThermalInfoActivity
import com.pacmac.devinfo.utils.Utils
import com.pacmac.devinfo.wifi.NetworkInfoKt
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class MainViewModelKt @Inject constructor(
    @ApplicationContext private val context: Context,
    private val telephonyManager: TelephonyManager,
    private val subscriptionManager: SubscriptionManager,
    private val appRepository: AppRepository,
    private val packageManager: PackageManager
) : ViewModel() {

    private var isExporting = false
    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    private val _appUpdateStatus = MutableStateFlow(UpToDateEnum.UNKNOWN)
    val appUpdateStatus: StateFlow<UpToDateEnum> = _appUpdateStatus.asStateFlow()

    private val _onGPSNotAvailable = MutableSharedFlow<Unit>()
    val onGPSNotAvailable = _onGPSNotAvailable.asSharedFlow()

    var hasGPS = true

    private val _onNavigateToScreen = MutableSharedFlow<Class<*>>()
    val onNavigateToScreen = _onNavigateToScreen.asSharedFlow()

    private val _onPermissionCheck = MutableSharedFlow<PermissionCheckModel>()
    val onPermissionCheck = _onPermissionCheck.asSharedFlow().onEach { permissionRequest = it }

    private val _mainInfo = MutableStateFlow<MainInfoModel?>(null)
    val mainInfo: StateFlow<MainInfoModel?> = _mainInfo.asStateFlow()

    private val _isLocationPermissionEnabled = MutableStateFlow(false)
    val isLocationPermissionEnabled: StateFlow<Boolean> = _isLocationPermissionEnabled.asStateFlow()

    private val _isPhonePermissionEnabled = MutableStateFlow(false)
    val isPhonePermissionEnabled: StateFlow<Boolean> = _isPhonePermissionEnabled.asStateFlow()

    private val _isStoragePermissionEnabled = MutableStateFlow(false)
    val isStoragePermissionEnabled: StateFlow<Boolean> = _isStoragePermissionEnabled.asStateFlow()

    private val _isCameraPermissionEnabled = MutableStateFlow(false)
    val isCameraPermissionEnabled: StateFlow<Boolean> = _isCameraPermissionEnabled.asStateFlow()

    private val _isPhoneNumberPermissionEnabled = MutableStateFlow(true)
    val isPhoneNumberPermissionEnabled: StateFlow<Boolean> = _isPhoneNumberPermissionEnabled.asStateFlow()

    private val _isBluetoothPermissionEnabled = MutableStateFlow(false)
    val isBluetoothPermissionEnabled: StateFlow<Boolean> = _isBluetoothPermissionEnabled.asStateFlow()

    fun setLocationPermission(enabled: Boolean) { _isLocationPermissionEnabled.value = enabled }
    fun setCameraPermission(enabled: Boolean) { _isCameraPermissionEnabled.value = enabled }
    fun setPhonePermission(enabled: Boolean) { _isPhonePermissionEnabled.value = enabled }
    fun setPhoneNumberPermission(enabled: Boolean) { _isPhoneNumberPermissionEnabled.value = enabled }
    fun setBluetoothPermission(enabled: Boolean) { _isBluetoothPermissionEnabled.value = enabled }

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
                        appRepository.updatePermissionStatus(permission, PermissionState.DENIED_FOREVER)
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

    fun loadMainInfo() {
        val simCount = MainUtilsKt.getSimCount(telephonyManager)
        _mainInfo.value = MainInfoModel(
            MainUtilsKt.getOsVersion(),
            MainUtilsKt.getModel(),
            MainUtilsKt.getManufacturer(),
            MainUtilsKt.getSerialNumber(_isPhonePermissionEnabled.value),
            MainUtilsKt.getBuildNumber(),
            MainUtilsKt.getHardware(),
            simCount,
            MainUtilsKt.getRadioFirmware(),
            MainUtilsKt.getBootloader(),
            MainUtilsKt.getDeviceLanguageSetting(),
            MainUtilsKt.getDeviceLanguageLocale(),
            MainUtilsKt.getPhoneNumbers(
                telephonyManager,
                subscriptionManager,
                simCount,
                _isPhonePermissionEnabled.value
            ),
            minorSdkVersion = MainUtilsKt.getMinorSdkVersion(),
            fullSdkInt = MainUtilsKt.getFullSdkInt(),
            advancedProtection = MainUtilsKt.getAdvancedProtectionEnabled(context),
        )
    }

    fun getDashboarItems(): List<DashModel> {
        val list = arrayListOf<DashModel>()
        list.add(DashModel(DashItem.CPU_SCREEN, R.drawable.cpu_img_2, "CPU", CPUInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.GPU_SCREEN, R.drawable.gpu_img_2, "GPU", GPUInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.RAM_SCREEN, R.drawable.ram_img_2, "RAM", StorageInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.BAT_SCREEN, R.drawable.battery_img_2, "", BatteryInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.CAM_SCREEN, R.drawable.camera_img_2, "", CameraInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.GPS_SCREEN, R.drawable.gps_img_2, "", GPSInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.CELL_SCREEN, R.drawable.sim_img_2, "SIM    ", CellularInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.SENSOR_SCREEN, R.drawable.sensor_img_2, "", SensorInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.DISPLAY_SCREEN, R.drawable.display_img_2, "", DisplayInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.WIFI_SCREEN, R.drawable.network_img_2, "", NetworkInfoKt::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.BLUETOOTH_SCREEN, R.drawable.bluetooth_img_2, "", BluetoothInfoActivity::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.AUDIO_SCREEN, R.drawable.audio_img_2, "", AudioInfoActivity::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.THERMAL_SCREEN, R.drawable.thermal_img_2, "", ThermalInfoActivity::class.java) { onTileClick(it) })
        list.add(DashModel(DashItem.ABOUT_SCREEN, R.drawable.about_img_2, "", AboutActivity::class.java) { onTileClick(it) })
        return list
    }

    private suspend fun onTileClick(tile: DashModel) {
        when (tile.dashItem) {
            DashItem.CPU_SCREEN -> _onNavigateToScreen.emit(tile.actClass)
            DashItem.GPU_SCREEN -> _onNavigateToScreen.emit(tile.actClass)
            DashItem.RAM_SCREEN -> _onNavigateToScreen.emit(tile.actClass)
            DashItem.BAT_SCREEN -> _onNavigateToScreen.emit(tile.actClass)

            DashItem.CAM_SCREEN -> {
                if (!isCameraPermissionEnabled.value) {
                    checkPermission(Utils.CAMERA_PERMISSION, R.string.cam_permission_msg, R.string.camera_feature_disabled)
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.GPS_SCREEN -> if (hasGPS) {
                if (!isLocationPermissionEnabled.value) {
                    checkPermission(Utils.LOCATION_PERMISSION, R.string.location_permission_msg, R.string.location_feature_disabled)
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            } else {
                _onGPSNotAvailable.emit(Unit)
            }

            DashItem.CELL_SCREEN -> {
                if (!isPhonePermissionEnabled.value) {
                    checkPermission(Utils.PHONE_PERMISSION, R.string.phone_permission_msg, R.string.phone_permission_msg)
                    return
                }
                if (!isLocationPermissionEnabled.value) {
                    checkPermission(Utils.LOCATION_PERMISSION, R.string.location_permission_msg, R.string.location_feature_disabled)
                    return
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !isPhoneNumberPermissionEnabled.value) {
                    checkPermission(Utils.PHONE_NUMBER_PERMISSION, R.string.phone_number_permission_msg, R.string.phone_number_permission_msg)
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.SENSOR_SCREEN -> _onNavigateToScreen.emit(tile.actClass)
            DashItem.DISPLAY_SCREEN -> _onNavigateToScreen.emit(tile.actClass)

            DashItem.WIFI_SCREEN -> {
                if (!isLocationPermissionEnabled.value) {
                    checkPermission(Utils.LOCATION_PERMISSION, R.string.location_permission_msg, R.string.location_feature_disabled)
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.BLUETOOTH_SCREEN -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isBluetoothPermissionEnabled.value) {
                    checkPermission(Utils.BLUETOOTH_PERMISSION, R.string.bt_permission_msg, R.string.bt_feature_disabled)
                    return
                }
                _onNavigateToScreen.emit(tile.actClass)
            }

            DashItem.AUDIO_SCREEN -> _onNavigateToScreen.emit(tile.actClass)
            DashItem.THERMAL_SCREEN -> _onNavigateToScreen.emit(tile.actClass)
            DashItem.ABOUT_SCREEN -> _onNavigateToScreen.emit(tile.actClass)
        }
    }

    private suspend fun checkPermission(permission: String, msgRes: Int, disabledMsg: Int) {
        getPermissionState(permission) { permissionState ->
            viewModelScope.launch {
                val permisions = arrayListOf(permission)
                if (permission == Utils.PHONE_PERMISSION) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !isPhoneNumberPermissionEnabled.value) {
                        permisions.add(Utils.PHONE_NUMBER_PERMISSION)
                    }
                }
                _onPermissionCheck.emit(
                    PermissionCheckModel(msgRes, disabledMsg, permisions.toTypedArray(), permissionState)
                )
            }
        }
    }

    suspend fun checkLocationPermission() {
        if (!isLocationPermissionEnabled.value) {
            checkPermission(Utils.LOCATION_PERMISSION, R.string.location_permission_msg, R.string.location_feature_disabled)
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
