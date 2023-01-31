package com.pacmac.devinfo.display

import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayViewModelKt @Inject constructor() : ViewModel() {

    private val displayInfo = mutableStateOf<List<UIObject>>(arrayListOf())
    fun getDisplayInfo(): State<List<UIObject>> = displayInfo

    fun observeDisplayInfo(
        context: Context,
        display: Display,
        metrics: DisplayMetrics
    ) = loadDisplayInfo(context, display, metrics)

    fun getDisplayInfoForExport(context: Context): List<UIObject>? {
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
        list.addAll(displayInfo.value)
        return list
    }

    private fun loadDisplayInfo(context: Context, display: Display, metrics: DisplayMetrics) {
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
        displayInfo.value = list
    }

}