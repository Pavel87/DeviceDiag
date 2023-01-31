package com.pacmac.devinfo.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.export.ExportTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModelKt @Inject constructor() : ViewModel() {

    private var isExporting = false

    private val _sensorList = mutableStateOf<List<Sensor>>(arrayListOf())

    val sensorList: State<List<Sensor>>
        get() = _sensorList

    fun retrieveSensors(context: Context) {
        loadSensorInfo(context)
    }

    fun getSensorListForExport(context: Context): List<UIObject>? {
        val list: MutableList<UIObject> = ArrayList()
        list.add(UIObject("Sensor Information", "", ListType.TITLE))
        list.add(
            UIObject(
                context.getString(R.string.sensor_type_export),
                context.getString(R.string.vendor),
                ListType.TITLE
            )
        )
        for (sensor in sensorList.value) {
            list.add(UIObject(sensor.name, sensor.vendor))
        }
        return list
    }

    private fun loadSensorInfo(context: Context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        _sensorList.value = deviceSensors
    }

    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    // TODO remove context from here
    // REFACTOR EXPORT logic
    fun export(context: Context) {
        if (!isExporting) {
            isExporting = true
            ExportTask(
                context, SensorUtils.EXPORT_FILE_NAME
            ) {
                viewModelScope.launch {
                    isExporting = false
                    _onExportDone.emit(it)
                }
            }.execute(this)
        }
    }
}