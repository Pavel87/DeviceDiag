package com.pacmac.devinfo.battery

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.export.ExportTask.OnExportTaskFinished
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BatteryInfoKt : ComponentActivity(), OnExportTaskFinished {

    private val viewModel by viewModels<BatteryViewModelKt>()
    private var isExporting = false


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceInfoTheme {
                Scaffold(
                    topBar = {
                        TopBar(
                            title = stringResource(id = R.string.title_activity_battery_info),
                            exportVisible = true,
                            onExportClick = {
                                if (!isExporting) {
                                    isExporting = true
                                    ExportTask(
                                        applicationContext,
                                        BatteryUtils.EXPORT_FILE_NAME,
                                        this
                                    ).execute(viewModel)
                                }
                            }
                        )
                    }
                ) { contentPadding ->
                    val modifier = Modifier.padding(contentPadding)
                    InfoListView(modifier = modifier, data = viewModel.getBatteryData().value)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // TODO make sure it is updating
        viewModel.registerReceiver(applicationContext)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unRegisterReceiver(applicationContext)
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