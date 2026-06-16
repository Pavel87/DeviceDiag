package com.pacmac.devinfo.display

import android.content.Context
import android.hardware.SensorManager
import android.util.DisplayMetrics
import android.view.Display
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DisplayViewModelKt @Inject constructor() : ViewModel() {

    private val _displayInfo = MutableStateFlow<List<UIObject>>(emptyList())
    val displayInfo: StateFlow<List<UIObject>> = _displayInfo.asStateFlow()

    fun observeDisplayInfo(
        context: Context,
        display: Display,
        metrics: DisplayMetrics,
        sensorManager: SensorManager? = null
    ) = loadDisplayInfo(context, display, metrics, sensorManager)

    fun getDisplayInfoForExport(context: Context): List<UIObject> {
        val list: MutableList<UIObject> = ArrayList()
        list.add(
            UIObject(
                context.getString(R.string.title_activity_display_info),
                "",
                ListType.TITLE
            )
        )
        list.add(
            UIObject(
                context.getString(R.string.param),
                context.getString(R.string.value),
                ListType.TITLE
            )
        )
        list.addAll(_displayInfo.value)
        return list
    }

    private fun loadDisplayInfo(context: Context, display: Display, metrics: DisplayMetrics, sensorManager: SensorManager? = null) {
        val list: MutableList<UIObject> = ArrayList()
        list.add(DisplayUtils.getDensity(context, metrics))
        list.add(DisplayUtils.getScaleFactor(context, metrics))
        list.add(DisplayUtils.getRefreshRate(context, display))
        list.addAll(DisplayUtils.getResolution(context, display, metrics))
        list.add(DisplayUtils.getXYDpi(context, metrics))
        list.add(DisplayUtils.getOrientation(context, display))
        list.add(DisplayUtils.getLayoutSize(context))
        list.add(DisplayUtils.getType(context, display))
        list.add(DisplayUtils.getDrawType(context, metrics))
        list.addAll(DisplayUtils.getHdrCapabilities(context, display))
        list.add(DisplayUtils.getWideColorGamut(context, display))
        list.addAll(DisplayUtils.getPeakRefreshRate(context, display))
        list.add(DisplayUtils.getArrSupport(context, display))
        list.add(DisplayUtils.getSupportedRefreshRates(context, display))
        if (sensorManager != null) {
            list.add(DisplayUtils.getHingeAngle(context, sensorManager))
        }
        _displayInfo.value = list
    }

}
