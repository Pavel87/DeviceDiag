package com.pacmac.devinfo.display

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.Display
import com.pacmac.devinfo.R
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
}
