package com.pacmac.devinfo.camera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pacmac.devinfo.R
import com.pacmac.devinfo.camera.ui.CameraGeneralDestination
import com.pacmac.devinfo.camera.ui.CameraGeneralScreen
import com.pacmac.devinfo.camera.ui.CameraInfoDestination
import com.pacmac.devinfo.camera.ui.CameraInfoScreen
import com.pacmac.devinfo.camera.ui.getCamTabs
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.gps.TabRow
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CameraInfoKt : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val viewModel: CameraViewModelKt = hiltViewModel()
            var camId by remember { mutableStateOf(0) }

            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val allDestinations = getCamTabs(viewModel.cameraCount.value)

            val currentScreen = allDestinations.find { it.route == currentDestination?.route }
                ?: CameraGeneralDestination

            val context = LocalContext.current


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

            DeviceInfoTheme() {
                Scaffold(
                    topBar = {
                        Column() {
                            TopBar(
                                title = stringResource(id = R.string.title_activity_camera_info),
                                exportVisible = true,
                            ) { viewModel.export(context) }

                            TabRow(
                                allScreens = allDestinations,
                                onTabSelected = { destination ->

                                    if (destination is CameraInfoDestination) {
                                        camId = destination.camIndex
                                    }
                                    navController.navigate(destination.route) {
                                        launchSingleTop = true
                                        popUpTo(CameraGeneralDestination.route)
                                    }
                                },
                                currentScreen = currentScreen
                            )
                        }
                    }) {

                    val modifier = Modifier.padding(it)

                    NavHost(
                        navController = navController,
                        startDestination = CameraGeneralDestination.route,
                        modifier = modifier
                    ) {

                        composable(route = CameraGeneralDestination.route) {
                            CameraGeneralScreen(viewModel = viewModel)
                        }

                        allDestinations.forEach { dest ->
                            if (dest is CameraInfoDestination) {
                                composable(route = dest.route) {
                                    CameraInfoScreen(camId, viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}