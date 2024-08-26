package com.example.a3dcompass.di

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor

class OrientationSensor(
    context: Context
): AndroidSensor(
    context,
    PackageManager.FEATURE_SENSOR_LIGHT,
    Sensor.TYPE_ORIENTATION
) {
}