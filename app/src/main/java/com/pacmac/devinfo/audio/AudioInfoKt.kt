package com.pacmac.devinfo.audio

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject

object AudioInfoKt {

    const val EXPORT_FILE_NAME = "audio_info"

    fun getAudioInfo(context: Context, packageManager: PackageManager): List<UIObject> {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return buildList {
            addAll(getDeviceCapabilities(context, audioManager, packageManager))
            addAll(getOutputDevices(context, audioManager))
            addAll(getCodecs(context))
        }
    }

    private fun getDeviceCapabilities(
        context: Context,
        audioManager: AudioManager,
        packageManager: PackageManager
    ): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.audio_device_info), "", ListType.TITLE))

        // Output Sample Rate
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        add(UIObject(context.getString(R.string.audio_output_sample_rate), sampleRate ?: "N/A", "Hz"))

        // Output Buffer Size
        val bufferSize = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        add(UIObject(context.getString(R.string.audio_output_buffer_size), bufferSize ?: "N/A", "frames"))

        // Low Latency Audio
        val lowLatency = if (packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY))
            ThreeState.YES else ThreeState.NO
        add(UIObject(context.getString(R.string.audio_low_latency), lowLatency, ListType.ICON))

        // Audio Pro
        val audioPro = if (packageManager.hasSystemFeature("android.hardware.audio.pro"))
            ThreeState.YES else ThreeState.NO
        add(UIObject(context.getString(R.string.audio_pro), audioPro, ListType.ICON))

        // MIDI Support
        val midi = if (packageManager.hasSystemFeature("android.hardware.midi"))
            ThreeState.YES else ThreeState.NO
        add(UIObject(context.getString(R.string.audio_midi), midi, ListType.ICON))

        // Spatial Audio (API 32+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            val spatializer = audioManager.spatializer
            val spatialState = if (spatializer.immersiveAudioLevel >
                android.media.Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_NONE
            ) ThreeState.YES else ThreeState.NO
            add(UIObject(context.getString(R.string.audio_spatial_support), spatialState, ListType.ICON))
        } else {
            add(UIObject(context.getString(R.string.audio_spatial_support), "N/A"))
        }

        // Microphone Count (API 28+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val micCount = try {
                audioManager.microphones.size.toString()
            } catch (e: Exception) {
                "N/A"
            }
            add(UIObject(context.getString(R.string.audio_microphone_count), micCount))
        } else {
            add(UIObject(context.getString(R.string.audio_microphone_count), "N/A"))
        }

        // APV Codec support (API 36+)
        if (Build.VERSION.SDK_INT >= 36) {
            val hasApv = try {
                val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)
                codecList.codecInfos.any { codec ->
                    codec.supportedTypes.any { it.equals(MediaFormat.MIMETYPE_VIDEO_APV, ignoreCase = true) }
                }
            } catch (e: Exception) { false }
            add(UIObject(
                context.getString(R.string.audio_apv_codec),
                if (hasApv) ThreeState.YES else ThreeState.NO,
                ListType.ICON
            ))
        }
    }

    private fun getOutputDevices(
        context: Context,
        audioManager: AudioManager
    ): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.audio_output_devices), "", ListType.TITLE))

        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            val typeLabel = getDeviceTypeLabel(device.type)
            add(UIObject(device.productName.toString(), typeLabel))
        }

        if (devices.isEmpty()) {
            add(UIObject("No output devices found", ""))
        }
    }

    private fun getCodecs(context: Context): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.audio_codecs_title), "", ListType.TITLE))

        val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)
        val codecInfos = codecList.codecInfos

        // Decoders first, then encoders
        val decoders = codecInfos.filter { !it.isEncoder }
        val encoders = codecInfos.filter { it.isEncoder }

        for (codec in decoders) {
            val hwLabel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (codec.isHardwareAccelerated) " (HW)" else " (SW)"
            } else ""
            add(UIObject(codec.name, "Decoder$hwLabel"))
        }

        for (codec in encoders) {
            val hwLabel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (codec.isHardwareAccelerated) " (HW)" else " (SW)"
            } else ""
            add(UIObject(codec.name, "Encoder$hwLabel"))
        }
    }

    @Suppress("DEPRECATION")
    private fun getDeviceTypeLabel(type: Int): String = when (type) {
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Built-in Speaker"
        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> "Built-in Earpiece"
        AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Wired Headset"
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Wired Headphones"
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "Bluetooth SCO"
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth A2DP"
        AudioDeviceInfo.TYPE_HDMI -> "HDMI"
        AudioDeviceInfo.TYPE_DOCK -> "Dock"
        AudioDeviceInfo.TYPE_USB_ACCESSORY -> "USB Accessory"
        AudioDeviceInfo.TYPE_USB_DEVICE -> "USB Device"
        AudioDeviceInfo.TYPE_USB_HEADSET -> "USB Headset"
        AudioDeviceInfo.TYPE_TELEPHONY -> "Telephony"
        AudioDeviceInfo.TYPE_LINE_ANALOG -> "Line Analog"
        AudioDeviceInfo.TYPE_LINE_DIGITAL -> "Line Digital"
        AudioDeviceInfo.TYPE_IP -> "IP"
        AudioDeviceInfo.TYPE_BUS -> "Bus"
        AudioDeviceInfo.TYPE_HEARING_AID -> "Hearing Aid"
        AudioDeviceInfo.TYPE_BUILTIN_MIC -> "Built-in Mic"
        AudioDeviceInfo.TYPE_FM_TUNER -> "FM Tuner"
        AudioDeviceInfo.TYPE_TV_TUNER -> "TV Tuner"
        AudioDeviceInfo.TYPE_AUX_LINE -> "AUX Line"
        35 -> "BLE Hearing Aid" // TYPE_BLE_HEARING_AID (API 37)
        else -> "Unknown ($type)"
    }
}
