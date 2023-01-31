package com.pacmac.devinfo.display

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportActivity
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.export.ExportTask.OnExportTaskFinished
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayInfoKt : ComponentActivity(), OnExportTaskFinished {

    private val viewModel: DisplayViewModelKt by viewModels()
    private var isExporting = false


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
                    val modifier = Modifier.padding(it)
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier.fillMaxSize(),
                    ) {
                        InfoListView(
                            modifier = Modifier.weight(1f),
                            data = viewModel.getDisplayInfo().value,
                            header = {
                                Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Column {
                                        Image(
                                            painter = painterResource(id = R.mipmap.draw),
                                            contentDescription = "drawableView",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .height(32.dp)
                                        )
                                        Divider()
                                    }
                                }

                            }
                        )
                        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_16)
                    }
                }
            }
        }

        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display
        } else {
            windowManager.defaultDisplay
        }

        val metrics = DisplayMetrics()
        display?.getMetrics(metrics)
        display?.let {
            viewModel.observeDisplayInfo(context = this, display = display, metrics = metrics)
        }
    }

    private fun export() {
        if (!isExporting) {
            isExporting = true
            ExportTask(applicationContext, DisplayUtils.EXPORT_FILE_NAME, this)
                .execute(viewModel)
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