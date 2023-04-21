package com.pacmac.devinfo.cellular.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.cellular.CellularViewModelKt
import com.pacmac.devinfo.cellular.model.CellNetworkModel
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView

@Composable
fun MobileNetworkScreen(viewModel: CellularViewModelKt = hiltViewModel()) {

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize(),
    ) {
        InfoListView(
            modifier = Modifier.weight(1f),
            data = CellNetworkModel.toUIModelList(LocalContext.current, viewModel.networkInfos.value)
        )
        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_6)
    }

}