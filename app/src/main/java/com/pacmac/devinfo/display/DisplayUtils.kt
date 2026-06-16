package com.pacmac.devinfo.display

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import java.util.Locale

object DisplayUtils {

    const val EXPORT_FILE_NAME = "display_info"

    internal fun getDensity(context: Context, metrics: DisplayMetrics): UIObject =
        UIObject(context.getString(R.string.display_density), String.format(Locale.ENGLISH, "%d", metrics.densityDpi), "dpi")

    internal fun getScaleFactor(context: Context, metrics: DisplayMetrics): UIObject =
        UIObject(context.getString(R.string.display_scale_factor), String.format(Locale.ENGLISH, "%.1f", metrics.density))

    internal fun getRefreshRate(context: Context, display: Display): UIObject =
        UIObject(context.getString(R.string.display_refresh_rate), String.format(Locale.ENGLISH, "%.1f", display.refreshRate), "fps")

    @Suppress("DEPRECATION")
    internal fun getResolution(context: Context, display: Display, metrics: DisplayMetrics): List<UIObject> {
        val size = Point()
        display.getRealSize(size)
        val screenWidth = size.x
        val screenHeight = size.y
        val widthInch = screenWidth.toDouble() / metrics.xdpi.toDouble()
        val heightInch = screenHeight.toDouble() / metrics.ydpi.toDouble()
        val diagonal = Math.sqrt(Math.pow(widthInch, 2.0) + Math.pow(heightInch, 2.0))

        return listOf(
            UIObject(context.getString(R.string.display_resolution), String.format(Locale.ENGLISH, "%dx%d", screenWidth, screenHeight), "px"),
            UIObject(context.getString(R.string.display_dimension), String.format(Locale.ENGLISH, "%.2fx%.2f", widthInch, heightInch), "in"),
            UIObject(context.getString(R.string.display_dimension) + " [dp]", String.format(Locale.ENGLISH, "%.2fx%.2f",
                160.0 * screenWidth.toFloat() / metrics.densityDpi,
                160.0 * screenHeight.toFloat() / metrics.densityDpi), "dp"),
            UIObject(context.getString(R.string.display_diagonal), String.format(Locale.ENGLISH, "%.2f", diagonal), "in"),
        )
    }

    internal fun getXYDpi(context: Context, metrics: DisplayMetrics): UIObject =
        UIObject(context.getString(R.string.display_xy_dpi), String.format(Locale.ENGLISH, "%.2fx%.2f", metrics.xdpi, metrics.ydpi))

    internal fun getOrientation(context: Context, display: Display): UIObject =
        UIObject(context.getString(R.string.display_orientation), getOrient(context, display.rotation), "°")

    internal fun getLayoutSize(context: Context): UIObject =
        UIObject(context.getString(R.string.display_size), getLayoutQualifier(context))

    internal fun getType(context: Context, display: Display): UIObject =
        UIObject(context.getString(R.string.display_type), display.name)

    internal fun getDrawType(context: Context, metrics: DisplayMetrics): UIObject =
        UIObject(context.getString(R.string.display_draw_size), densityQualifier(context, metrics.densityDpi))

    private fun getLayoutQualifier(context: Context): String {
        val config = context.resources.configuration
        return when (config.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> context.getString(R.string.display_small)
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> context.getString(R.string.display_normal)
            Configuration.SCREENLAYOUT_SIZE_LARGE -> context.getString(R.string.display_large)
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> context.getString(R.string.display_x_large)
            else -> context.getString(R.string.unknown)
        }
    }

    private fun densityQualifier(context: Context, densityDpi: Int): String = when (densityDpi) {
        DisplayMetrics.DENSITY_LOW -> "LDPI"
        DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
        DisplayMetrics.DENSITY_HIGH -> "HDPI"
        DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
        DisplayMetrics.DENSITY_340 -> "340DPI XHDPI - XXHDPI"
        DisplayMetrics.DENSITY_360 -> "360DPI XHDPI - XXHDPI"
        DisplayMetrics.DENSITY_400 -> "400DPI XHDPI - XXHDPI"
        DisplayMetrics.DENSITY_420 -> "420DPI XHDPI - XXHDPI"
        DisplayMetrics.DENSITY_440 -> "440DPI XHDPI - XXHDPI"
        DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
        DisplayMetrics.DENSITY_560 -> "440DPI XXHDPI - XXXHDPI"
        DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
        DisplayMetrics.DENSITY_TV -> "TVDPI"
        DisplayMetrics.DENSITY_260 -> "260DPI HDPI - XHDPI"
        DisplayMetrics.DENSITY_280 -> "280DPI HDPI - XHDPI"
        DisplayMetrics.DENSITY_300 -> "300DPI HDPI - XHDPI"
        else -> context.getString(R.string.unknown)
    }

    private fun getOrient(context: Context, rotation: Int): String = when (rotation) {
        0 -> "0"
        1 -> "90"
        2 -> "180"
        3 -> "270"
        else -> context.getString(R.string.not_available_info)
    }

    internal fun getHdrCapabilities(context: Context, display: Display): List<UIObject> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return listOf(
                UIObject(
                    context.getString(R.string.display_hdr_capabilities),
                    context.getString(R.string.not_available_info)
                )
            )
        }

        val hdrCaps = display.hdrCapabilities
        if (hdrCaps == null || hdrCaps.supportedHdrTypes.isEmpty()) {
            return listOf(
                UIObject(
                    context.getString(R.string.display_hdr_capabilities),
                    context.getString(R.string.display_no_hdr)
                )
            )
        }

        val list = mutableListOf<UIObject>()
        val typeNames = hdrCaps.supportedHdrTypes.map { type ->
            when (type) {
                Display.HdrCapabilities.HDR_TYPE_HDR10 -> "HDR10"
                Display.HdrCapabilities.HDR_TYPE_HLG -> "HLG"
                Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> "Dolby Vision"
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && type == Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS) {
                        "HDR10+"
                    } else {
                        "Unknown ($type)"
                    }
                }
            }
        }
        list.add(UIObject(context.getString(R.string.display_hdr_types), typeNames.joinToString(", ")))
        list.add(
            UIObject(
                context.getString(R.string.display_max_luminance),
                String.format(Locale.ENGLISH, "%.1f", hdrCaps.desiredMaxLuminance),
                "nits"
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.display_min_luminance),
                String.format(Locale.ENGLISH, "%.4f", hdrCaps.desiredMinLuminance),
                "nits"
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.display_max_avg_luminance),
                String.format(Locale.ENGLISH, "%.1f", hdrCaps.desiredMaxAverageLuminance),
                "nits"
            )
        )
        return list
    }

    internal fun getWideColorGamut(context: Context, display: Display): UIObject {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return UIObject(
                context.getString(R.string.display_wide_color_gamut),
                context.getString(R.string.not_available_info)
            )
        }
        val state = if (display.isWideColorGamut) ThreeState.YES else ThreeState.NO
        return UIObject(context.getString(R.string.display_wide_color_gamut), state, ListType.ICON)
    }

    @Suppress("DEPRECATION")
    internal fun getPeakRefreshRate(context: Context, display: Display): List<UIObject> {
        val modes = display.supportedModes
        if (modes.isNullOrEmpty()) {
            return listOf(
                UIObject(
                    context.getString(R.string.display_peak_refresh_rate),
                    context.getString(R.string.not_available_info)
                )
            )
        }
        val peakRate = modes.maxOf { it.refreshRate }
        val modeStrings = modes.map { mode ->
            String.format(Locale.ENGLISH, "%dx%d@%.0ffps", mode.physicalWidth, mode.physicalHeight, mode.refreshRate)
        }
        return listOf(
            UIObject(
                context.getString(R.string.display_peak_refresh_rate),
                String.format(Locale.ENGLISH, "%.1f", peakRate),
                "fps"
            ),
            UIObject(
                context.getString(R.string.display_supported_modes),
                modeStrings.joinToString(", ")
            )
        )
    }

    internal fun getArrSupport(context: Context, display: Display): UIObject {
        if (Build.VERSION.SDK_INT >= 36) {
            return try {
                val hasArr = display.hasArrSupport()
                UIObject(
                    context.getString(R.string.display_arr_support),
                    if (hasArr) ThreeState.YES else ThreeState.NO,
                    ListType.ICON
                )
            } catch (e: Exception) {
                UIObject(context.getString(R.string.display_arr_support), "N/A")
            }
        }
        return UIObject(context.getString(R.string.display_arr_support), "N/A")
    }

    internal fun getSupportedRefreshRates(context: Context, display: Display): UIObject {
        if (Build.VERSION.SDK_INT >= 36) {
            return try {
                val rates = display.supportedRefreshRates
                if (rates != null && rates.isNotEmpty()) {
                    val ratesStr = rates.joinToString(", ") {
                        String.format(Locale.ENGLISH, "%.0f", it)
                    }
                    UIObject(context.getString(R.string.display_supported_refresh_rates), ratesStr, "Hz")
                } else {
                    UIObject(context.getString(R.string.display_supported_refresh_rates), "N/A")
                }
            } catch (e: Exception) {
                UIObject(context.getString(R.string.display_supported_refresh_rates), "N/A")
            }
        }
        return UIObject(context.getString(R.string.display_supported_refresh_rates), "N/A")
    }

    internal fun getHingeAngle(context: Context, sensorManager: SensorManager): UIObject {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return UIObject(
                context.getString(R.string.display_hinge_angle),
                context.getString(R.string.not_available_info)
            )
        }
        val hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE)
        return if (hingeSensor != null) {
            UIObject(context.getString(R.string.display_hinge_angle), "Supported")
        } else {
            UIObject(context.getString(R.string.display_hinge_angle), context.getString(R.string.not_available_info))
        }
    }
}
