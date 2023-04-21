package com.pacmac.devinfo.cellular

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pacmac.devinfo.Destination
import com.pacmac.devinfo.R
import com.pacmac.devinfo.cellular.ui.CellDestination
import com.pacmac.devinfo.cellular.ui.CellScreen
import com.pacmac.devinfo.cellular.ui.ConfigDestination
import com.pacmac.devinfo.cellular.ui.ConfigScreen
import com.pacmac.devinfo.cellular.ui.MobileNetworkScreen
import com.pacmac.devinfo.cellular.ui.NetworkDestination
import com.pacmac.devinfo.cellular.ui.PhoneAndSIMScreen
import com.pacmac.devinfo.cellular.ui.SIMDestination
import com.pacmac.devinfo.cellular.ui.getCellTabs
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.gps.TabRow
import com.pacmac.devinfo.ui.components.SearchBar
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CellularInfoKt : ComponentActivity() {

    val viewModel: CellularViewModelKt by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val allDestinations = getCellTabs()

            var searchTerm by rememberSaveable { mutableStateOf("") }
            var enableSearch by rememberSaveable { mutableStateOf(false) }
            var hideSearch by remember { mutableStateOf(true) }

            val currentScreen = allDestinations.find { it.route == currentDestination?.route } ?: SIMDestination

            hideSearch = currentScreen.route != ConfigDestination.route
            if (hideSearch) {
                enableSearch = false
                searchTerm = ""
                viewModel.filterProperties(searchTerm)
            }


            viewModel.observeSIMInfo(isPhonePermission(context))

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

            DeviceInfoTheme {
                Scaffold(
                    topBar = {
                        Column() {
                            if (enableSearch.not()) {
                                TopBar(title = getTitle(currentScreen),
                                    exportVisible = true,
                                    actionButton = {

                                        if (hideSearch.not()) {
                                            IconButton(onClick = { enableSearch = true }) {
                                                Icon(
                                                    imageVector = Icons.Filled.Search,
                                                    contentDescription = stringResource(id = R.string.action_search)
                                                )
                                            }
                                        }
                                    },
                                    onExportClick = { viewModel.export(context) }
                                )
                            } else {
                                SearchBar(
                                    searchTerm = searchTerm,
                                    onNewSearchTerm = {
                                        searchTerm = it
                                        viewModel.filterProperties(it)
                                    },
                                    onHideSearch = {
                                        enableSearch = false
                                    }
                                )
                            }

                            TabRow(
                                allScreens = allDestinations,
                                onTabSelected = { destination ->

                                    navController.navigate(destination.route) {
                                        launchSingleTop = true
                                        popUpTo(SIMDestination.route)
                                    }
                                },
                                currentScreen = currentScreen
                            )
                        }
                    }

                ) {

                    val modifier = Modifier.padding(it)

                    NavHost(
                        navController = navController,
                        startDestination = SIMDestination.route,
                        modifier = modifier
                    ) {

                        composable(route = SIMDestination.route) {
                            PhoneAndSIMScreen(viewModel = viewModel)
                        }
                        composable(route = NetworkDestination.route) {
                            MobileNetworkScreen(viewModel = viewModel)
                        }
                        composable(route = CellDestination.route) {
                            CellScreen(viewModel = viewModel)
                        }
                        composable(route = ConfigDestination.route) {
                            ConfigScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerPSL()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterPSL()
    }

    private fun isPhonePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Utils.checkPermission(context, Utils.PHONE_PERMISSION)
        } else false
    }

    @Composable
    private fun getTitle(destination: Destination) = when (destination) {
        is SIMDestination -> stringResource(id = R.string.title_phone_and_sim)
        is NetworkDestination -> stringResource(id = R.string.active_network)
        is CellDestination -> stringResource(id = R.string.connected_cells)
        is ConfigDestination -> stringResource(id = R.string.carrier_config)
        else -> ""
    }
}