package com.pacmac.devinfo.export.ui

import androidx.compose.runtime.Composable
import com.pacmac.devinfo.Destination
import com.pacmac.devinfo.gps.models.ScreenType

object ExportScreenDestinations: Destination {
    override val route: String = "ExportScreen"
    override val nameResId: Int = 0
    override val type: ScreenType =  ScreenType.EXPORT
    override val screen: @Composable () -> Unit = {}
}
object PromoScreenDestinations: Destination {
    override val route: String = "PromoScreen"
    override val nameResId: Int = 0
    override val type: ScreenType =  ScreenType.PROMO
    override val screen: @Composable () -> Unit = {}
}