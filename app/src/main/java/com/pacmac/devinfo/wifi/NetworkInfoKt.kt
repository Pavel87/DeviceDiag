package com.pacmac.devinfo.wifi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.export.ExportTask.OnExportTaskFinished
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.components.PacmacAdBanner
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NetworkInfoKt : ComponentActivity(), OnExportTaskFinished {

    private var isExporting = false

    private val viewModel: NetworkViewModelKt by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val onBack = { backDispatcher?.onBackPressed() }

            val windowSizeClass = calculateWindowSizeClass(this)
            val context = LocalContext.current

            DeviceInfoTheme {
                Scaffold(topBar = {
                    TopBar(title = stringResource(id = R.string.title_activity_display_info),
                        exportVisible = true,
                        onExportClick = {
                            export()
                        },
                        hasNavigationIcon = true,
                        onBack = { onBack() }
                    )
                }) {
                    val modifier = Modifier
                        .padding(it)
                        .fillMaxSize()

                    InfoListView(
                        modifier = modifier,
                        data = viewModel.getWifiInfo().value,
                        header = {
                            val contentModifier =
                                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                                    Modifier.fillMaxWidth()
                                } else {
                                    Modifier.width(390.dp)
                                }
                            PacmacAdBanner(
                                adText = stringResource(id = R.string.icmp_ping_ad),
                                modifier = contentModifier.padding(8.dp)
                            ) {
                                Utils.openGooglePlayListing("com.pacmac.pinger", context)
                            }
                        }
                    )
                }
            }
        }
        viewModel.observeNetworkInfo(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        viewModel.observeNetworkInfo(context = applicationContext)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopObserver()
    }

    private fun export() {
        if (!isExporting) {
            isExporting = true
            ExportTask(applicationContext, NetworkUtils.EXPORT_FILE_NAME, this).execute(viewModel)
        }
    }

    override fun onExportTaskFinished(filePath: String?) {
        isExporting = false
        if (filePath != null) {
            val intent = Intent(applicationContext, ExportActivity::class.java)
            intent.putExtra(ExportUtils.EXPORT_FILE, filePath)
            startActivity(intent)
        }
    }

}

@Preview(widthDp = 1000)
@Composable
fun PreviewNetworkInfo() {
    DeviceInfoTheme {

        LocalContext.current
        InfoListView(
            modifier = Modifier,
            data = emptyList(),
            header = {
//            val contentModifier = if (WindowWidthSizeClass.Compact) {
                Modifier.fillMaxWidth()
//            } else {
                Modifier.width(300.dp)
//            }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                ) {
                    PacmacAdBanner(
                        adText = stringResource(id = R.string.icmp_ping_ad),
                        modifier = Modifier.width(380.dp)
                    ) {
                    }
                }
            }
        )
    }
}
