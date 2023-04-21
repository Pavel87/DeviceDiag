package com.pacmac.devinfo.config

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuildPropertiesActivityKt: ComponentActivity() {

    private val viewModel by viewModels<BuildPropViewModelKt>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceInfoTheme {
                BuildPropertiesScreen(viewModel)
            }
        }
    }
}