package com.pacmac.devinfo.sensor

import android.content.Context
import android.hardware.Sensor
import com.pacmac.devinfo.R
import java.util.Locale

object SensorUtils {
    const val EXPORT_FILE_NAME = "sensor_info"

    const val TYPE_GOOGLE_DOUBLE_TAP = 65539
    const val TYPE_GOOGLE_DOUBLE_TWIST = 65538
    const val TYPE_GOOGLE_SENSORS_SYNC = 65537

    // Google
    const val TYPE_GOOGLE_TEMPERATURE_BOSH = 65536
    const val TYPE_CYWEE_FLIP = 33171019
    const val TYPE_CYWEE_PICKUP = 33171018

    // Cywee
    const val TYPE_CYWEE_HAND_UP = 33171016
    const val TYPE_COARSE_MOTION_CLASSIFIER = 33171012
    const val TYPE_MOTION_ACCEL = 33171011
    const val TYPE_PEDESTRIAN_ACTIVITY_MONITOR = 33171010
    const val TYPE_PEDOMETER = 33171009
    const val TYPE_RELATIVE_MOTION_DETECTOR = 33171007
    const val TYPE_ABSOLUTE_MOTION_DETECTOR = 33171006
    const val TYPE_TILT = 33171003
    const val TYPE_FACING = 33171002
    const val TYPE_TAP = 33171001

    // QTI sensors
    const val TYPE_BASIC_GESTURES = 33171000
    const val TYPE_DYNAMIC_SENSOR_META = 32
    const val TYPE_DEVICE_ORIENTATION = 27
    const val TYPE_WRIST_TILT_GESTURE = 26
    const val TYPE_PICK_UP_GESTURE = 25
    const val TYPE_GLANCE_GESTURE = 24
    const val TYPE_WAKE_GESTURE = 23
    const val TYPE_TILT_DETECTOR = 22


    fun getUnits(type: Int, context: Context): String {
        when (type) {
            Sensor.TYPE_LIGHT -> return "lux"
            Sensor.TYPE_PRESSURE -> return "hPa"
            Sensor.TYPE_PROXIMITY -> return "cm"
            Sensor.TYPE_RELATIVE_HUMIDITY -> return "%"
            Sensor.TYPE_TEMPERATURE, Sensor.TYPE_AMBIENT_TEMPERATURE, TYPE_GOOGLE_TEMPERATURE_BOSH -> return "°C"
            Sensor.TYPE_HEART_RATE -> return "bps"
            Sensor.TYPE_STEP_COUNTER, TYPE_PEDOMETER -> return context.getString(R.string.sensor_step_unit)
            Sensor.TYPE_GRAVITY -> return "m/s^2"
            Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> return "uT"
            Sensor.TYPE_STEP_DETECTOR, Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_ORIENTATION, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_GAME_ROTATION_VECTOR, Sensor.TYPE_GYROSCOPE_UNCALIBRATED, Sensor.TYPE_SIGNIFICANT_MOTION, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, Sensor.TYPE_POSE_6DOF, Sensor.TYPE_STATIONARY_DETECT, Sensor.TYPE_MOTION_DETECT, Sensor.TYPE_HEART_BEAT, Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT, Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, TYPE_TILT_DETECTOR, TYPE_WAKE_GESTURE, TYPE_PICK_UP_GESTURE, TYPE_GLANCE_GESTURE, TYPE_WRIST_TILT_GESTURE, TYPE_DEVICE_ORIENTATION, TYPE_DYNAMIC_SENSOR_META, TYPE_BASIC_GESTURES, TYPE_TAP, TYPE_FACING, TYPE_TILT, TYPE_ABSOLUTE_MOTION_DETECTOR, TYPE_RELATIVE_MOTION_DETECTOR, TYPE_CYWEE_HAND_UP, TYPE_CYWEE_PICKUP, TYPE_CYWEE_FLIP, TYPE_GOOGLE_SENSORS_SYNC, TYPE_GOOGLE_DOUBLE_TWIST, TYPE_GOOGLE_DOUBLE_TAP -> {}
        }
        return ""
    }

    private fun isValid(value: Float) = value != Integer.MAX_VALUE.toFloat()

    var stepCounter = 0

    fun getReadings(sensorType: Int, context: Context, uiState: SensorDetailUIState): List<String> {

        val list = arrayListOf<String>()

        when (sensorType) {
            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> {
                if (isValid(uiState.sensorReading1).not()) return list

                if (uiState.sensorReading1 > 0) {
                    list.add(context.getString(R.string.sensor_on_body))
                } else {
                    list.add(context.getString(R.string.sensor_off_body))
                }
            }

            Sensor.TYPE_STEP_DETECTOR -> {
                list.add(context.getString(R.string.sensor_step))
            }

            Sensor.TYPE_STEP_COUNTER, TYPE_PEDOMETER -> {
                if (isValid(uiState.sensorReading1).not()) return list

                val steps = uiState.sensorReading1.toDouble()
                if (stepCounter == 3) {
                    stepCounter = 0
                }
                if (stepCounter == 0) {
                    list.add(
                        String.format(
                            Locale.ENGLISH,
                            "%.0f ",
                            steps
                        ) + SensorUtils.getUnits(sensorType, context) + if (steps < 2.0) "" else "s"
                    )
                } else if (stepCounter == 1) {
                    list.add(
                        String.format(
                            Locale.ENGLISH,
                            "%.0f ",
                            steps
                        ) + SensorUtils.getUnits(sensorType, context) + if (steps < 2.0) "" else "s"
                    )
                } else {
                    list.add(
                        String.format(
                            "%.0f ",
                            steps
                        ) + SensorUtils.getUnits(sensorType, context) + if (steps < 2.0) "" else "s"
                    )
                }
                stepCounter++
            }

            Sensor.TYPE_PROXIMITY -> {
                if (isValid(uiState.sensorReading1).not()) return list
                when (uiState.sensorReading1) {
                    0f -> list.add(context.getString(R.string.sensor_near))
                    uiState.maxRange.value.toFloat() -> list.add(context.getString(R.string.sensor_far))
                    else -> {
                        list.add(
                            String.format(
                                Locale.ENGLISH, "%.2f %s",
                                uiState.sensorReading1, SensorUtils.getUnits(sensorType, context)
                            )
                        )
                    }
                }
            }

            Sensor.TYPE_SIGNIFICANT_MOTION -> {
                //TODO
            }

            Sensor.TYPE_HEART_BEAT -> {
                if (isValid(uiState.sensorReading1).not()) return list
                if (uiState.sensorReading1 > 0) {
                    list.add(context.getString(R.string.sensor_detected))
                } else {
                    list.add(context.getString(R.string.sensor_no_heart_beat))
                }
            }

            Sensor.TYPE_ORIENTATION -> {
                if (isValid(uiState.sensorReading1).not()
                    || isValid(uiState.sensorReading2).not()
                    || isValid(uiState.sensorReading3).not()
                ) return list

                list.add(
                    String.format(
                        Locale.ENGLISH, context.getString(R.string.sensor_azimuth),
                        uiState.sensorReading1
                    ) + getUnits(sensorType, context)
                )
                list.add(
                    String.format(
                        Locale.ENGLISH, context.getString(R.string.sensor_pitch),
                        uiState.sensorReading2
                    ) + getUnits(sensorType, context)
                )
                list.add(
                    String.format(
                        Locale.ENGLISH, context.getString(R.string.sensor_roll),
                        uiState.sensorReading3
                    ) + getUnits(sensorType, context)
                )
            }

            Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
                if (isValid(uiState.sensorReading1).not()
                    || isValid(uiState.sensorReading2).not()
                    || isValid(uiState.sensorReading3).not()
                ) return list

                list.add(
                    String.format(
                        Locale.ENGLISH, "X: %.0f %s",
                        uiState.sensorReading1, getUnits(sensorType, context)
                    )
                )
                list.add(
                    String.format(
                        Locale.ENGLISH, "Y: %.0f %s",
                        uiState.sensorReading2, getUnits(sensorType, context)
                    )
                )
                list.add(
                    String.format(
                        Locale.ENGLISH, "Z: %.0f %s",
                        uiState.sensorReading3, getUnits(sensorType, context)
                    )
                )
            }

            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, Sensor.TYPE_POSE_6DOF, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, Sensor.TYPE_GRAVITY, Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_GAME_ROTATION_VECTOR, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> {
                if (isValid(uiState.sensorReading1).not()
                    || isValid(uiState.sensorReading2).not()
                    || isValid(uiState.sensorReading3).not()
                ) return list

                list.add(
                    String.format(
                        Locale.ENGLISH, "X: %.1f ",
                        uiState.sensorReading1
                    )
                )
                list.add(
                    String.format(
                        Locale.ENGLISH, "Y: %.1f ",
                        uiState.sensorReading2
                    )
                )
                list.add(
                    String.format(
                        Locale.ENGLISH, "Z: %.1f ",
                        uiState.sensorReading3
                    )
                )
            }

            Sensor.TYPE_LIGHT, Sensor.TYPE_PRESSURE, Sensor.TYPE_TEMPERATURE, Sensor.TYPE_RELATIVE_HUMIDITY, Sensor.TYPE_AMBIENT_TEMPERATURE, TYPE_GOOGLE_TEMPERATURE_BOSH, Sensor.TYPE_MOTION_DETECT, Sensor.TYPE_STATIONARY_DETECT, Sensor.TYPE_HEART_RATE, TYPE_TILT_DETECTOR, TYPE_WAKE_GESTURE, TYPE_PICK_UP_GESTURE, TYPE_GLANCE_GESTURE, TYPE_WRIST_TILT_GESTURE, TYPE_DEVICE_ORIENTATION, TYPE_DYNAMIC_SENSOR_META, TYPE_BASIC_GESTURES, TYPE_TAP, TYPE_FACING, TYPE_TILT, TYPE_ABSOLUTE_MOTION_DETECTOR, SensorUtils.TYPE_RELATIVE_MOTION_DETECTOR, SensorUtils.TYPE_PEDESTRIAN_ACTIVITY_MONITOR, TYPE_CYWEE_HAND_UP, TYPE_CYWEE_PICKUP, TYPE_CYWEE_FLIP, TYPE_GOOGLE_SENSORS_SYNC, TYPE_GOOGLE_DOUBLE_TWIST, TYPE_GOOGLE_DOUBLE_TAP, TYPE_MOTION_ACCEL, TYPE_COARSE_MOTION_CLASSIFIER -> {
                if (isValid(uiState.sensorReading1).not()) return list
                list.add(
                    String.format(
                        Locale.ENGLISH, "%.2f %s",
                        uiState.sensorReading1, SensorUtils.getUnits(sensorType, context)
                    )
                )
            }

            else -> {}
        }
        return list
    }
}