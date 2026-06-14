package com.pacmac.devinfo.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.export.ExportTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModelKt @Inject constructor() : ViewModel() {

    private var isExporting = false

    private val _sensorList = MutableStateFlow<List<Sensor>>(emptyList())
    val sensorList: StateFlow<List<Sensor>> = _sensorList.asStateFlow()

    fun retrieveSensors(context: Context) {
        loadSensorInfo(context)
    }

    fun getSensorListForExport(context: Context): List<UIObject> = buildList {
        add(UIObject("Sensor Information", "", ListType.TITLE))
        add(UIObject(context.getString(R.string.sensor_type_export), context.getString(R.string.vendor), ListType.TITLE))
        for (sensor in sensorList.value) {
            add(UIObject(sensor.name, sensor.vendor))
        }
    }

    private fun loadSensorInfo(context: Context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        _sensorList.value = sensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    fun export(context: Context) {
        if (!isExporting) {
            isExporting = true
            ExportTask(context, SensorUtils.EXPORT_FILE_NAME) {
                viewModelScope.launch {
                    isExporting = false
                    _onExportDone.emit(it)
                }
            }.execute(this)
        }
    }
}
