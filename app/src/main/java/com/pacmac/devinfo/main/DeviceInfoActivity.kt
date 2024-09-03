package com.pacmac.devinfo.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.pacmac.devinfo.NewFeaturesScreen
import com.pacmac.devinfo.R
import com.pacmac.devinfo.UpToDateEnum
import com.pacmac.devinfo.config.BuildPropertiesActivityKt
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.gps.TabRow
import com.pacmac.devinfo.main.model.PermissionModalType
import com.pacmac.devinfo.main.model.PermissionState
import com.pacmac.devinfo.main.ui.DeviceInfoDialog
import com.pacmac.devinfo.main.ui.MainDashboard
import com.pacmac.devinfo.main.ui.MainInfo
import com.pacmac.devinfo.main.ui.getMainDestinations
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.utils.Utils
import com.pacmac.devinfo.utils.Utils.CAMERA_PERMISSION
import com.pacmac.devinfo.utils.Utils.LOCATION_PERMISSION
import com.pacmac.devinfo.utils.Utils.PHONE_NUMBER_PERMISSION
import com.pacmac.devinfo.utils.Utils.PHONE_PERMISSION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceInfoActivity : ComponentActivity() {

    private val viewModel: MainViewModelKt by viewModels()

    @Composable
    private fun observePermissionRequestResult() =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, @JvmSuppressWildcards Boolean> ->
            result.forEach { (p, isGranted) ->
                viewModel.updatePermissionState(
                    p,
                    if (isGranted) PermissionState.GRANTED else PermissionState.DENIED
                )
                if (isGranted.not()) return@forEach

                when (p) {
                    LOCATION_PERMISSION -> {
                        viewModel._isLocationPermissionEnabled.value = true
                    }

                    CAMERA_PERMISSION -> {
                        viewModel._isCameraPermissionEnabled.value = true
                    }

                    PHONE_PERMISSION -> {
                        viewModel._isPhonePermissionEnabled.value = true
                    }

                    PHONE_NUMBER_PERMISSION -> {
                        viewModel._isPhoneNumberPermissionEnabled.value = true
                    }
                }
            }
        }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        setContent {
            val context = LocalContext.current
            val permissionLauncher = observePermissionRequestResult()
            var showNewFeatures by remember { mutableStateOf(false) }
            var showGPSError by remember { mutableStateOf(false) }
            var showRateDialog by remember { mutableStateOf(false) }
            var permissionModal by remember { mutableStateOf(PermissionModalType.NO_MODAL) }

            val windowSizeClass = calculateWindowSizeClass(this)

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

                    launch {
                        viewModel.onNavigateToScreen.collectLatest { toClass ->
                            val intent = Intent(context, toClass)
                            context.startActivity(intent)
                        }
                    }
                    launch {
                        viewModel.onPermissionCheck.collectLatest { p ->
                            if (shouldShowRequestPermissionRationale(p.permissions.first())) {
                                permissionModal = PermissionModalType.PERMISSION_RATIOANAL
                            } else {
                                if (p.permissionState == PermissionState.DENIED_FOREVER) {
                                    permissionModal =
                                        PermissionModalType.PERMISSION_DENIED_FOREVER
                                } else {
                                    permissionModal = PermissionModalType.NO_MODAL
                                    permissionLauncher.launch(p.permissions)
                                }
                            }
                        }
                    }
                    launch {
                        viewModel.onGPSNotAvailable.collectLatest {
                            showGPSError = true
                        }
                    }
                }
            }

            DeviceInfoTheme() {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination
                val allDestinations = getMainDestinations()
                val currentScreen =
                    allDestinations.find { it.route == currentDestination?.route } ?: MainInfo
                Box(modifier = Modifier.fillMaxSize()) {

                    if (showNewFeatures) {
                        NewFeaturesScreen(
                            windowWidthSizeClass = windowSizeClass.widthSizeClass,
                            onClose = { showNewFeatures = false },
                            onAppReview = { showRateDialog = true })
                    } else {

                        Scaffold(modifier = Modifier.fillMaxSize(),
                            topBar = {
                                Column() {
                                    TopBar(title = stringResource(id = R.string.app_name),
                                        exportVisible = currentScreen == MainInfo,
                                        onExportClick = { viewModel.export(context) })

                                    TabRow(
                                        allScreens = allDestinations,
                                        onTabSelected = { destination ->
                                            navController.navigate(destination.route) {
                                                launchSingleTop = true
                                                popUpTo(MainInfo.route)
                                            }
                                        },
                                        currentScreen = currentScreen
                                    )
                                }
                            }) {

                            Box(
                                modifier = Modifier
                                    .padding(it)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                NavHost(
                                    navController = navController,
                                    startDestination = MainInfo.route,
                                ) {

                                    composable(route = MainInfo.route) {
                                        MainScreen(viewModel = viewModel) {
                                            startActivity(
                                                Intent(
                                                    context, BuildPropertiesActivityKt::class.java
                                                )
                                            )
                                        }
                                    }
                                    composable(route = MainDashboard.route) {
                                        DashboardScreen(
                                            viewModel = viewModel,
                                            windowSizeClass.widthSizeClass
                                        )
                                    }
                                }


                            }
                        }
                    }

                    if (showGPSError) {
                        DeviceInfoDialog(
                            title = stringResource(id = R.string.missing_permission),
                            msg = stringResource(id = R.string.gps_not_available_in_device),
                            disableDismissButton = true,
                            onDismiss = {
                                showGPSError = false
                            },
                            onPositiveAction = {
                                showGPSError = false
                            },
                            positiveButtonText = stringResource(id = R.string.ok_button),
                        )
                    }

                    if (permissionModal != PermissionModalType.NO_MODAL) {

                        viewModel.permissionRequest?.let { model ->
                            if (permissionModal == PermissionModalType.PERMISSION_RATIOANAL) {
                                DeviceInfoDialog(
                                    title = stringResource(id = R.string.missing_permission),
                                    msg = stringResource(id = model.permissionMsg),
                                    onDismiss = {
                                        permissionModal = PermissionModalType.NO_MODAL
                                    },
                                    onPositiveAction = {
                                        viewModel.updatePermissionState(
                                            model.permissions.first(),
                                            PermissionState.RATIONAL_DISPLAYED
                                        )
                                        permissionLauncher.launch(model.permissions)
                                        permissionModal = PermissionModalType.NO_MODAL
                                    },
                                    dismissButtonText = stringResource(id = R.string.cancel),
                                    positiveButtonText = stringResource(id = R.string.request_perm),
                                )
                            } else {
                                DeviceInfoDialog(
                                    title = stringResource(id = R.string.missing_permission),
                                    msg = stringResource(id = model.permissionDisabledMsg),
                                    disableDismissButton = true,
                                    onPositiveAction = {
                                        permissionModal = PermissionModalType.NO_MODAL
                                    },
                                    onDismiss = {
                                        permissionModal = PermissionModalType.NO_MODAL
                                    },
                                    positiveButtonText = stringResource(id = R.string.ok_button),
                                )
                            }
                        }

                    }

                    if (viewModel.appUpdateStatus.value == UpToDateEnum.NO) {
                        DeviceInfoDialog(
                            title = stringResource(id = R.string.new_version_title),
                            msg = stringResource(id = R.string.new_version_content),
                            onDismiss = {
                                viewModel.appUpgradeModalDisplayed()
                            },
                            onPositiveAction = {
                                viewModel.appUpgradeModalDisplayed()
                                Utils.launchPlayStore(context)
                            },
                            dismissButtonText = stringResource(id = R.string.no_thanks),
                            positiveButtonText = stringResource(id = R.string.update),
                        )
                    }

                    if (showRateDialog) {
                        DeviceInfoDialog(
                            title = stringResource(id = R.string.rate_app),
                            msg = stringResource(id = R.string.rta_dialog_message),
                            onDismiss = {
                                showRateDialog = false
                            },
                            onPositiveAction = {
                                Utils.launchPlayStore(context)
                                showRateDialog = false
                            },
                            positiveButtonText = stringResource(id = R.string.rateit),
                            dismissButtonText = stringResource(id = R.string.no_thanks),
                        )
                    }
                }
            }


            // Check if user disabled LOCATION permission at some point
            viewModel._isLocationPermissionEnabled.value = Utils.checkPermission(
                applicationContext, LOCATION_PERMISSION
            )

            SideEffect {
                lifecycleScope.launch {
                    if (viewModel.isLocationPermissionEnabled.value && viewModel.checkIfAppUpdated()) {
                        showNewFeatures = true
                    }
                }

                lifecycleScope.launch {
                    viewModel.checkLocationPermission()
                }
            }

            viewModel.hasGPS = Utils.hasGPS(context)
        }
    }
}