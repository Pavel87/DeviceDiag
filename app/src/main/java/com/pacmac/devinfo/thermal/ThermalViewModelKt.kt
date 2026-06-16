package com.pacmac.devinfo.thermal

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ThermalViewModel"

@HiltViewModel
class ThermalViewModelKt @Inject constructor() : ViewModel() {

    val EXPORT_FILE_NAME = "thermal_info"

    private var isActive = false
    private val _thermalInfo = MutableStateFlow<List<UIObject>>(emptyList())
    val thermalInfo: StateFlow<List<UIObject>> = _thermalInfo.asStateFlow()

    private fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (isActive) {
            emit(Unit)
            delay(period)
        }
    }

    fun observeThermalInfo(context: Context) {
        if (isActive) return
        isActive = true
        tickerFlow(2.seconds)
            .onEach {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        loadThermalInfo(context)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load thermal info", e)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun stopObserver() {
        isActive = false
    }

    private fun loadThermalInfo(context: Context) {
        _thermalInfo.value = ThermalInfoKt.getThermalInfo(context)
    }

    fun getThermalInfoForExport(context: Context): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.title_activity_thermal_info), "", ListType.TITLE))
        add(UIObject(context.getString(R.string.param), context.getString(R.string.value), ListType.TITLE))
        addAll(thermalInfo.value)
    }
}
