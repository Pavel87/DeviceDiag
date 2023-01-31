package com.pacmac.devinfo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DeviceInfoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}