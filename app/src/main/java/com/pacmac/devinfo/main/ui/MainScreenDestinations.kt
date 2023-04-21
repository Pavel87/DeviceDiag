package com.pacmac.devinfo.main.ui

import androidx.compose.runtime.Composable
import com.pacmac.devinfo.Destination
import com.pacmac.devinfo.R
import com.pacmac.devinfo.gps.models.ScreenType

object MainInfo: Destination {
    override val route: String = "MainInfo"
    override val nameResId: Int = R.string.title_section1
    override val type = ScreenType.MAIN_INFO
    override val screen: @Composable () -> Unit = { }
}
object MainDashboard: Destination {
    override val route: String = "Dashboard"
    override val nameResId: Int = R.string.title_section2
    override val type = ScreenType.DASHBOARD
    override val screen: @Composable () -> Unit = {}
}

fun getMainDestinations() = listOf(MainInfo, MainDashboard)