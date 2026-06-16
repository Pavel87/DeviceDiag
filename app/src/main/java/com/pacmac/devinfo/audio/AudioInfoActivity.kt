package com.pacmac.devinfo.audio

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ads.InterstitialAdManager
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.export.ExportTask.OnExportTaskFinished
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioInfoActivity : ComponentActivity(), OnExportTaskFinished {

    private val viewModel: AudioViewModelKt by viewModels()
    private var isExporting = false

    @Inject
    lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val audioInfo by viewModel.audioInfo.collectAsState()
            val onBack = {
                interstitialAdManager.maybeShowInterstitial(this@AudioInfoActivity) { finish() }
            }

            DeviceInfoTheme {
                Scaffold(topBar = {
                    TopBar(
                        title = stringResource(id = R.string.title_activity_audio_info),
                        exportVisible = true,
                        onExportClick = { export() },
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
                            data = audioInfo,
                        )
                        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_13)
                    }
                }
            }
        }
    }

    private fun export() {
        if (!isExporting) {
            isExporting = true
            ExportTask(applicationContext, AudioInfoKt.EXPORT_FILE_NAME, this)
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
