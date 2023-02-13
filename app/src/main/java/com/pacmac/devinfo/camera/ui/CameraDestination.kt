package com.pacmac.devinfo.camera.ui

import androidx.compose.runtime.Composable
import com.pacmac.devinfo.Destination
import com.pacmac.devinfo.gps.models.ScreenType

private const val CAMERA = "Camera"

object CameraGeneralDestination : Destination {
    override val route: String = "General"
    override val type = ScreenType.CAMERA_GENERAL
    override val screen: @Composable () -> Unit = { CameraGeneralScreen() }
}

class CameraInfoDestination(val camIndex: Int) : Destination {
    override var route: String = CAMERA
    override val type = ScreenType.CAMERA_INFO
    override val screen: @Composable () -> Unit = { CameraInfoScreen() }
}


fun getCamTabs(camCount: Int = 0): List<Destination> {
    val tabs = arrayListOf<Destination>(CameraGeneralDestination)
    for (i in 0 until camCount) {
        val c = CameraInfoDestination(i)
        c.route = "$CAMERA ${i + 1}"
        tabs.add(c)
    }
    return tabs
}