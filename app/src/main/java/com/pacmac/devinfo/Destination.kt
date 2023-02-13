package com.pacmac.devinfo

import androidx.compose.runtime.Composable
import com.pacmac.devinfo.gps.models.ScreenType

interface Destination {
    val route: String
    val type: ScreenType
    val screen: @Composable () -> Unit
}