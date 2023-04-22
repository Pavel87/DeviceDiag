package com.pacmac.devinfo.gps.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.gps.GPSInfoListDestination
import com.pacmac.devinfo.gps.GPSScreen
import com.pacmac.devinfo.gps.GPSViewModelKt
import com.pacmac.devinfo.gps.NMEALogDestination
import com.pacmac.devinfo.gps.NMEALogScreen
import com.pacmac.devinfo.gps.SatellitesDestination
import com.pacmac.devinfo.gps.SatellitesScreen
import com.pacmac.devinfo.gps.TabRow
import com.pacmac.devinfo.gps.gpsTabs
import com.pacmac.devinfo.ui.components.BarTitle
import com.pacmac.devinfo.ui.components.BarTitle2Line
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GPSInfoKt : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val viewModel: GPSViewModelKt = hiltViewModel()
            viewModel.subscribeToGPSUpdates()

            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val currentScreen =
                gpsTabs().find { it.route == currentDestination?.route } ?: GPSInfoListDestination

            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val onBack = { backDispatcher?.onBackPressed() }

            DisposableEffect(key1 = Unit) {
                this.onDispose {
                    viewModel.unsubscribeToGPSUpdates()
                }
            }

            LaunchedEffect(key1 = Unit) {
                lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                    launch {
                        viewModel.onExportDone.collectLatest { filePath ->
                            filePath?.let {
                                val intent = Intent(context, ExportActivity::class.java)
                                intent.putExtra(ExportUtils.EXPORT_FILE, filePath)
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }

            val (street, city) = viewModel.address.value

            DeviceInfoTheme() {
                Scaffold(
                    topBar = {
                        Column() {
                            TopBar(
                                exportVisible = true,
                                titleComposable = {
                                    if (street.isNotBlank() || city.isNotBlank()) {
                                        BarTitle2Line(street, city)
                                    } else {
                                        BarTitle(title = stringResource(id = R.string.activity_title_gps_information))
                                    }
                                },
                                onExportClick = { viewModel.export(context, currentScreen.type) },
                                hasNavigationIcon = true,
                                onBack = { onBack() }
                            )

                            TabRow(allScreens = gpsTabs(), onTabSelected = { destination ->
                                navController.navigate(destination.route) {
                                    launchSingleTop = true
                                    popUpTo(GPSInfoListDestination.route)
                                }

                            }, currentScreen = currentScreen)

                        }
                    }) {
                    val modifier = Modifier.padding(it)
                    NavHost(
                        navController = navController,
                        startDestination = GPSInfoListDestination.route,
                        modifier = modifier
                    ) {
                        composable(route = GPSInfoListDestination.route) {
                            GPSScreen(viewModel = viewModel)
                        }
                        composable(route = SatellitesDestination.route) {
                            SatellitesScreen(viewModel = viewModel)
                        }
                        composable(route = NMEALogDestination.route) {
                            NMEALogScreen(viewModel = viewModel)
                        }
                    }
                }
            }



            if (viewModel.isGPSEnabled().not()) {
                showAlertOnDisabledGPS()
                Toast.makeText(LocalContext.current, R.string.gps_not_available, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    @Preview
    @Composable
    fun PreviewGPS() {
        DeviceInfoTheme() {
            Column() {
                TopBar(
                    title = "",
                    exportVisible = true,
                    hasNavigationIcon = true,
                    onBack = { },
                    onExportClick = {}
                )
                TabRow(
                    allScreens = gpsTabs(),
                    onTabSelected = {},
                    currentScreen = SatellitesDestination
                )
            }
        }
    }

    private fun showAlertOnDisabledGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.location_off_message)
            .setCancelable(true)
            .setPositiveButton(R.string.ok_button) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }
        val gpsAlertDialog = builder.create()
        gpsAlertDialog.show()
    }
}