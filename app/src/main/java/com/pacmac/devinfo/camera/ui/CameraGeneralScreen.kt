package com.pacmac.devinfo.camera.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.camera.CameraUtilsKt
import com.pacmac.devinfo.camera.CameraViewModelKt
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView

@Composable
fun CameraGeneralScreen(viewModel: CameraViewModelKt = hiltViewModel()) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize(),
    ) {
        InfoListView(
            modifier = Modifier.weight(1f),
            data = CameraUtilsKt.getFormattedGeneralInfo(context, viewModel.cameraInfoGeneral.value)
        )
        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_5)
    }
}