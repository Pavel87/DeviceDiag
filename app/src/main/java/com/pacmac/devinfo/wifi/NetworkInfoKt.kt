package com.pacmac.devinfo.wifi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportActivity
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.export.ExportTask.OnExportTaskFinished
import com.pacmac.devinfo.export.ExportUtils
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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DeviceInfoTheme {
                Scaffold(topBar = {
                    TopBar(title = stringResource(id = R.string.title_activity_display_info),
                        exportVisible = true,
                        onExportClick = {
                            export()
                        })
                }) {
                    val modifier = Modifier
                        .padding(it)
                        .fillMaxSize()

                    InfoListView(
                        modifier = modifier,
                        data = viewModel.getWifiInfo().value,
                        header = {
                            PacmacAdBanner(
                                adText = stringResource(id = R.string.icmp_ping_ad),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Utils.openGooglePlayListing("com.pacmac.pinger", this)
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