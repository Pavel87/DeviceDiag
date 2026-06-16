package com.pacmac.devinfo.sensor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pacmac.devinfo.ads.InterstitialAdManager
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SensorInfoKt : ComponentActivity() {

    @Inject
    lateinit var interstitialAdManager: InterstitialAdManager

    private val LIST_DESTINATION = "LIST_DESTINATION"
    private val DETAIL_DESTINATION = "DETAIL_DESTINATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val onBack = {
                interstitialAdManager.maybeShowInterstitial(this@SensorInfoKt) { finish() }
            }

            DeviceInfoTheme() {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = LIST_DESTINATION,
                ) {
                    composable(route = LIST_DESTINATION) {
                        SensorListScreen(
                            onSensorSelected = { sensorType ->
                                navController.navigate(
                                    "DETAIL_DESTINATION/$sensorType"
                                ) { launchSingleTop = true }
                            },
                            onBack = { onBack() }
                        )
                    }
                    composable(
                        "$DETAIL_DESTINATION/{sensorType}",
                        arguments = listOf(navArgument("sensorType") { type = NavType.IntType })
                    ) { backStackEntry ->
                        SensorDetailScreen(
                            backStackEntry.arguments?.getInt("sensorType") ?: 0,
                            onBack = { onBack() })
                    }
                }
            }
        }
    }
}