package com.pacmac.devinfo.cpu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.export.ExportTask.OnExportTaskFinished
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CPUInfoKt : ComponentActivity(), OnExportTaskFinished {

    private val viewModel by viewModels<CPUViewModelKt>()

    private var isExporting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val onBack = { backDispatcher?.onBackPressed() }

            DeviceInfoTheme {
                Scaffold(topBar = {
                    TopBar(title = stringResource(id = R.string.title_activity_cpu_info),
                        exportVisible = true,
                        onExportClick = {
                            export()
                        },
                        hasNavigationIcon = true,
                        onBack = { onBack() }
                    )
                }) {
                    val modifier = Modifier.padding(it)
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier.fillMaxSize(),
                    ) {
                        InfoListView(
                            modifier = Modifier.weight(1f),
                            data = viewModel.getCPUInfo().value
                        )
                        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_3)
                    }
                }
            }
        }
        viewModel.observeCPUInfo(applicationContext)
    }


    private fun export() {
        if (!isExporting) {
            isExporting = true
            ExportTask(
                applicationContext, viewModel.EXPORT_FILE_NAME, this
            ).execute(viewModel)
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

    override fun onResume() {
        super.onResume()
        viewModel.observeCPUInfo(context = applicationContext)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopObserver()
    }
}