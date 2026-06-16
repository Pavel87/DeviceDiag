package com.pacmac.devinfo.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject

object BluetoothInfoKt {

    const val EXPORT_FILE_NAME = "bluetooth_info"

    /**
     * Returns bluetooth info that does not require BLUETOOTH_CONNECT permission.
     * Safe to call without any runtime permissions.
     */
    fun getBluetoothFeatureInfo(context: Context, packageManager: PackageManager): List<UIObject> {
        val list = mutableListOf<UIObject>()

        val hasBluetooth = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        if (!hasBluetooth) return list

        // Bluetooth Version
        val version = getBluetoothVersion(packageManager)
        list.add(UIObject(context.getString(R.string.bt_version), version))

        // LE Features section
        list.add(UIObject(context.getString(R.string.bt_supported_profiles), "", ListType.TITLE))

        val hasBLE = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        list.add(
            UIObject(
                context.getString(R.string.bt_le_support),
                if (hasBLE) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        // LE Audio (API 33+)
        val hasLEAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.hasSystemFeature("android.hardware.bluetooth_le.audio")
        } else {
            false
        }
        list.add(
            UIObject(
                context.getString(R.string.bt_le_audio),
                if (hasLEAudio) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        // Auracast (LE Audio Broadcast) (API 34+)
        val hasAuracast = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            packageManager.hasSystemFeature("android.hardware.bluetooth_le.audio.broadcast")
        } else {
            false
        }
        list.add(
            UIObject(
                context.getString(R.string.bt_auracast),
                if (hasAuracast) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        return list
    }

    /**
     * Returns full bluetooth info including adapter details and paired devices.
     * Requires BLUETOOTH_CONNECT permission on API 31+.
     */
    @SuppressLint("MissingPermission")
    fun getBluetoothInfo(context: Context, packageManager: PackageManager): List<UIObject> {
        val list = mutableListOf<UIObject>()

        val hasBluetooth = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        if (!hasBluetooth) {
            list.add(UIObject(context.getString(R.string.bt_not_available), ""))
            return list
        }

        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = bluetoothManager?.adapter

        // Adapter Info section
        list.add(UIObject(context.getString(R.string.bt_adapter_info), "", ListType.TITLE))

        if (adapter != null) {
            list.add(
                UIObject(
                    context.getString(R.string.bt_adapter_name),
                    adapter.name ?: "N/A"
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.bt_adapter_address),
                    adapter.address ?: "N/A"
                )
            )
            list.add(
                UIObject(
                    context.getString(R.string.bt_version),
                    getBluetoothVersion(packageManager)
                )
            )
        } else {
            list.add(UIObject(context.getString(R.string.bt_not_available), ""))
            return list
        }

        // LE Features section
        list.add(UIObject(context.getString(R.string.bt_supported_profiles), "", ListType.TITLE))

        val hasBLE = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        list.add(
            UIObject(
                context.getString(R.string.bt_le_support),
                if (hasBLE) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        // LE Audio (API 33+)
        val hasLEAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.hasSystemFeature("android.hardware.bluetooth_le.audio")
        } else {
            false
        }
        list.add(
            UIObject(
                context.getString(R.string.bt_le_audio),
                if (hasLEAudio) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        list.add(
            UIObject(
                context.getString(R.string.bt_le_advertising),
                if (adapter.bluetoothLeAdvertiser != null) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        list.add(
            UIObject(
                context.getString(R.string.bt_offloaded_filtering),
                if (adapter.isOffloadedFilteringSupported) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        list.add(
            UIObject(
                context.getString(R.string.bt_offloaded_batching),
                if (adapter.isOffloadedScanBatchingSupported) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        list.add(
            UIObject(
                context.getString(R.string.bt_multiple_advertisement),
                if (adapter.isMultipleAdvertisementSupported) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            )
        )

        // Auracast (LE Audio Broadcast) (API 34+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val hasAuracast = packageManager.hasSystemFeature(
                "android.hardware.bluetooth_le.audio.broadcast"
            )
            list.add(
                UIObject(
                    context.getString(R.string.bt_auracast),
                    if (hasAuracast) ThreeState.YES else ThreeState.NO,
                    ListType.ICON
                )
            )
        }

        // Max Connected Audio Devices (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(
                UIObject(
                    context.getString(R.string.bt_max_connected_audio),
                    adapter.maxConnectedAudioDevices.toString()
                )
            )
        }

        // Paired Devices section
        list.add(UIObject(context.getString(R.string.bt_paired_devices), "", ListType.TITLE))

        val bondedDevices = adapter.bondedDevices
        if (bondedDevices.isNullOrEmpty()) {
            list.add(UIObject(context.getString(R.string.bt_paired_devices), "None"))
        } else {
            for (device in bondedDevices) {
                val deviceName = device.name ?: "Unknown"
                val deviceType = getDeviceTypeString(device)
                val majorClass = getMajorDeviceClassString(device)
                val info = buildString {
                    append(deviceType)
                    if (majorClass.isNotEmpty()) {
                        append(" | ")
                        append(majorClass)
                    }
                }
                list.add(UIObject(deviceName, info))
            }
        }

        return list
    }

    private fun getBluetoothVersion(packageManager: PackageManager): String {
        return when {
            packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) -> "4.0+ (LE supported)"
            packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) -> "Supported (Classic)"
            else -> "Not Available"
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceTypeString(device: BluetoothDevice): String {
        val baseType = when (device.type) {
            BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Classic"
            BluetoothDevice.DEVICE_TYPE_LE -> "LE"
            BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual"
            else -> "Unknown"
        }
        // Check for hearing aid major class
        val btClass = device.bluetoothClass
        val isHearingAid = btClass != null && btClass.majorDeviceClass == 0x0900
        return if (isHearingAid) "$baseType (Hearing Aid)" else baseType
    }

    @SuppressLint("MissingPermission")
    private fun getMajorDeviceClassString(device: BluetoothDevice): String {
        val btClass = device.bluetoothClass ?: return ""
        return when (btClass.majorDeviceClass) {
            0x0100 -> "Computer"
            0x0200 -> "Phone"
            0x0300 -> "Networking"
            0x0400 -> "Audio/Video"
            0x0500 -> "Peripheral"
            0x0600 -> "Imaging"
            0x0700 -> "Wearable"
            0x0800 -> "Toy"
            0x0900 -> "Health"
            0x0000 -> "Misc"
            0x1F00 -> "Uncategorized"
            else -> ""
        }
    }
}
