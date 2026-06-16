package com.pacmac.devinfo.cellular.ui

import android.os.Build
import androidx.compose.runtime.Composable
import com.pacmac.devinfo.Destination
import com.pacmac.devinfo.R
import com.pacmac.devinfo.gps.models.ScreenType

object SIMDestination : Destination {
    override val route: String = "SIM"
    override val nameResId: Int = R.string.cell_phone_sim_tab
    override val type = ScreenType.SIM
    override val screen: @Composable () -> Unit = { PhoneAndSIMScreen() }
}

object NetworkDestination : Destination {
    override val route: String = "NETWORK"
    override val nameResId: Int = R.string.cell_network_tab
    override val type = ScreenType.NETWORK
    override val screen: @Composable () -> Unit = { MobileNetworkScreen() }
}

object CellDestination : Destination {
    override val route: String = "CELL"
    override val nameResId: Int = R.string.cell_info_tab
    override val type = ScreenType.CELL
    override val screen: @Composable () -> Unit = { CellScreen() }
}

object ConfigDestination : Destination {
    override val route: String = "CONFIG"
    override val nameResId: Int = R.string.carrier_config_tab
    override val type = ScreenType.CONFIG
    override val screen: @Composable () -> Unit = { ConfigScreen() }
}

object SatelliteDestination : Destination {
    override val route: String = "SATELLITE"
    override val nameResId: Int = R.string.satellite_tab
    override val type = ScreenType.SATELLITE
    override val screen: @Composable () -> Unit = { SatelliteScreen() }
}

fun getCellTabs(): List<Destination> = buildList {
    add(SIMDestination)
    add(NetworkDestination)
    add(CellDestination)
    if (Build.VERSION.SDK_INT >= 36) {
        add(SatelliteDestination)
    }
    add(ConfigDestination)
}