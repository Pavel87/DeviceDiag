package com.pacmac.devinfo.gps

import androidx.compose.runtime.Composable
import com.pacmac.devinfo.gps.models.GPSScreenType

interface GPSDestinations {
    val route: String
    val type: GPSScreenType
    val screen: @Composable () -> Unit
}

object GPSInfoListDestination : GPSDestinations {
    override val route: String = "GPS Info"
    override val type = GPSScreenType.MAIN
    override val screen: @Composable () -> Unit = { GPSScreen() }
}

object SatellitesDestination : GPSDestinations {
    override val route: String = "Satellites"
    override val type = GPSScreenType.SATELLITES
    override val screen: @Composable () -> Unit = { SatellitesScreen() }
}

object NMEALogDestination : GPSDestinations {
    override val route: String = "NMEA"
    override val type = GPSScreenType.NMEA
    override val screen: @Composable () -> Unit = { NMEALogScreen() }
}

fun gpsTabs() = listOf(GPSInfoListDestination, SatellitesDestination, NMEALogDestination)