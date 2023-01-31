package com.pacmac.devinfo.sensor

import com.pacmac.devinfo.UIObject

data class SensorDetailUIState(
    val name: UIObject = UIObject("", "", ""),
    val vendor: UIObject = UIObject("", "", ""),
    val power: UIObject = UIObject("", "", ""),
    val maxRange: UIObject = UIObject("", "", ""),

    val sensorReading1: Float = Integer.MAX_VALUE.toFloat(),
    val sensorReading2: Float = Integer.MAX_VALUE.toFloat(),
    val sensorReading3: Float = Integer.MAX_VALUE.toFloat()
)
