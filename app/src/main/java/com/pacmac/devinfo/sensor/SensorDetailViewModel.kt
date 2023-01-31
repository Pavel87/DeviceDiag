package com.pacmac.devinfo.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SensorDetailViewModel @Inject constructor(val sensorManager: SensorManager) : ViewModel() {

    private var sensor: Sensor? = null
    private val SAMPLING_FREQ = 5 * 1000 * 1000

    private val _uiState = MutableStateFlow(SensorDetailUIState())
    val uiState: StateFlow<SensorDetailUIState> = _uiState.asStateFlow()


    fun loadSensor(sensorType: Int) {
        sensor = sensorManager.getDefaultSensor(sensorType)
        if (sensor == null) {
            sensor = sensorManager.getDefaultSensor(sensorType, true)
        }

        sensor?.let {
            _uiState.value = SensorDetailUIState(
                name = UIObject("", it.name),
                vendor = UIObject("", it.vendor),
                power = UIObject("", it.power.toString(), "mA"),
                maxRange = UIObject("", String.format("%.2f ", it.maximumRange))
            )
        }
    }

    fun subscribeToSensor() {
        sensor?.let {
            println("subscribeToSensor")
            sensorManager.registerListener(sensorEventListener, sensor, SAMPLING_FREQ)
        }
    }

    fun unsubscribeToSensor() {
        sensor?.let {
            println("unsubscribeToSensor")
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    private var sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            val sensorsValues = floatArrayOf(0.0f, 0.0f, 0.0f)
            for (i in sensorEvent.values.indices) {
                if (i < 3) {
                    sensorsValues[i] = sensorEvent.values[i]
                }
            }

            _uiState.value = SensorDetailUIState(
                name = _uiState.value.name,
                vendor = _uiState.value.vendor,
                power = _uiState.value.power,
                maxRange = _uiState.value.maxRange,
                sensorReading1 = sensorsValues[0],
                sensorReading2 = sensorsValues[1],
                sensorReading3 = sensorsValues[2],
            )
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {
            // Log.d("TAG2", "accuracy: " + i);
        }
    }
}