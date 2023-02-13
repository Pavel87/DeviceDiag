package com.pacmac.devinfo.gps

import androidx.compose.runtime.Composable
import com.pacmac.devinfo.Destination
import com.pacmac.devinfo.gps.models.ScreenType
object GPSInfoListDestination : Destination {
    override val route: String = "GPS Info"
    override val type = ScreenType.MAIN
    override val screen: @Composable () -> Unit = { GPSScreen() }
}

object SatellitesDestination : Destination {
    override val route: String = "Satellites"
    override val type = ScreenType.SATELLITES
    override val screen: @Composable () -> Unit = { SatellitesScreen() }
}

object NMEALogDestination : Destination {
    override val route: String = "NMEA"
    override val type = ScreenType.NMEA
    override val screen: @Composable () -> Unit = { NMEALogScreen() }
}

fun gpsTabs() = listOf(GPSInfoListDestination, SatellitesDestination, NMEALogDestination)