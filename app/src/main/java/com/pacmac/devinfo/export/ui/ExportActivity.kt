package com.pacmac.devinfo.export.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.export.ExportViewModel
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ExportActivity : ComponentActivity() {

    private var filePathString: String? = ""

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ExportViewModel = hiltViewModel()
            val windowSizeClass = calculateWindowSizeClass(this)
            val navController = rememberNavController()
            val openedSlots = viewModel.openedSlots.collectAsState(initial = 0)
            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val onBack = { backDispatcher?.onBackPressed() }

            if (intent != null) {
                filePathString = intent.getStringExtra(ExportUtils.EXPORT_FILE) ?: ""
            }

            println("PACMAC -- openedSlots: $openedSlots")

            LaunchedEffect(key1 = Unit) {

                launch {
                    viewModel.connectionError.collectLatest {
                        Toast.makeText(
                            applicationContext,
                            R.string.check_internet_connection,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                launch {
                    viewModel.onStartPromoActivity.collectLatest {
                        navController.navigate(PromoScreenDestinations.route)
                    }
                }
                launch {
                    viewModel.loadRewardAd.collectLatest {
                        println("PACMAC -- LOADING REWARD AD")
                        createAndLoadRewardedAd(viewModel.getAdShowCallback())
                    }
                }
                launch {
                    viewModel.showRewardAd.collectLatest {
                        println("PACMAC -- SHOW REWARD AD")
                        it.show(this@ExportActivity, viewModel.getUserEarnedRewardListener())
                    }
                }

                launch {
                    viewModel.onRewardEarned.collectLatest { slotCount ->
                        if (slotCount < 5) {
                            Toast.makeText(
                                applicationContext, R.string.earning_export_slots, Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                R.string.export_slots_unlocked,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            DeviceInfoTheme {
                NavHost(
                    navController = navController, startDestination = ExportScreenDestinations.route
                ) {

                    composable(ExportScreenDestinations.route) {
                        ExportScreen(isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
                            exportCount = openedSlots.value,
                            loadAdEnabled = viewModel.isShowAdButtonEnabled.value,
                            onAdClick = { viewModel.onAdClick() },
                            onExportClick = {
                                viewModel.onExportClick()
                                ExportUtils.sendShareIntent(
                                    this@ExportActivity, File(filePathString)
                                )
                            },
                            onBack = { onBack() }
                        )
                    }

                    composable(PromoScreenDestinations.route) {
                        PromoScreen(isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
                            onClose = { navController.popBackStack() })
                    }
                }
            }
        }
    }

    private fun createAndLoadRewardedAd(adShowCallback: RewardedAdLoadCallback) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, resources.getString(R.string.rewarded1), adRequest, adShowCallback)
    }
}