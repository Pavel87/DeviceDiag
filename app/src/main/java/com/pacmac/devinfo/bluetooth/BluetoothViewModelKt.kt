package com.pacmac.devinfo.bluetooth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModelKt @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManager: PackageManager
) : ViewModel() {

    private val _bluetoothInfo = MutableStateFlow<List<UIObject>>(emptyList())
    val bluetoothInfo: StateFlow<List<UIObject>> = _bluetoothInfo.asStateFlow()

    init {
        loadBluetoothInfo()
    }

    private fun loadBluetoothInfo() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            _bluetoothInfo.value = listOf(
                UIObject(context.getString(R.string.bt_not_available), "")
            )
            return
        }

        val hasConnectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (hasConnectPermission) {
            try {
                _bluetoothInfo.value =
                    BluetoothInfoKt.getBluetoothInfo(context, packageManager)
            } catch (_: SecurityException) {
                _bluetoothInfo.value =
                    BluetoothInfoKt.getBluetoothFeatureInfo(context, packageManager)
            }
        } else {
            _bluetoothInfo.value =
                BluetoothInfoKt.getBluetoothFeatureInfo(context, packageManager)
        }
    }

    fun refreshBluetoothInfo() {
        loadBluetoothInfo()
    }

    fun getBluetoothInfoForExport(context: Context): List<UIObject> = buildList {
        add(
            UIObject(
                context.getString(R.string.title_activity_bluetooth_info),
                "",
                ListType.TITLE
            )
        )
        add(
            UIObject(
                context.getString(R.string.param),
                context.getString(R.string.value),
                ListType.TITLE
            )
        )
        addAll(_bluetoothInfo.value)
    }
}
