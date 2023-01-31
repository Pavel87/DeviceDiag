package com.pacmac.devinfo.storage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportActivity
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

class StorageInfoKt: ComponentActivity(), ExportTask.OnExportTaskFinished {

    private val viewModel by viewModels<StorageViewModelKt>()

    private var isExporting = false

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceInfoTheme {
                Scaffold(topBar = {
                    TopBar(title = stringResource(id = R.string.title_activity_storage_info),
                        exportVisible = true,
                        onExportClick = {
                            export()
                        })
                }) {
                    val modifier = Modifier.padding(it)
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier.fillMaxSize(),
                    ) {
                        InfoListView(modifier = Modifier.weight(1f), data = viewModel.getStorageInfo().value)
                        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_9)
                    }
                }
            }
        }
        viewModel.observeStorageInfo(applicationContext)
    }


    private fun export() {
        if (!isExporting) {
            isExporting = true
            ExportTask(
                applicationContext, StorageUtils.EXPORT_FILE_NAME, this
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
}