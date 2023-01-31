package com.pacmac.devinfo.sensor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorInfoKt : ComponentActivity() {


    private val LIST_DESTINATION = "LIST_DESTINATION"
    private val DETAIL_DESTINATION = "DETAIL_DESTINATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DeviceInfoTheme() {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = LIST_DESTINATION,
                ) {
                    composable(route = LIST_DESTINATION) {
                        SensorListScreen { sensorType ->
                            navController.navigate(
                                "DETAIL_DESTINATION/$sensorType"
                            ) { launchSingleTop = true }
                        }
                    }
                    composable(
                        "$DETAIL_DESTINATION/{sensorType}",
                        arguments = listOf(navArgument("sensorType") { type = NavType.IntType })
                    ) { backStackEntry ->
                        SensorDetailScreen(backStackEntry.arguments?.getInt("sensorType") ?: 0)
                    }
                }
            }
        }
    }
}