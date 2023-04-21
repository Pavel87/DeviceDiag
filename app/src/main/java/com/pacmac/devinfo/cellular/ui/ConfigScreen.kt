package com.pacmac.devinfo.cellular.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.cellular.CellularViewModelKt
import com.pacmac.devinfo.config.PropCounter
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.utils.Utils

@Composable
fun ConfigScreen(viewModel: CellularViewModelKt = hiltViewModel()) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize(),
    ) {
        InfoListView(
            modifier = Modifier.weight(1f), data = Utils.getUIObjectsFromBuildProps(
                LocalContext.current, viewModel.filteredCarrierConfig.value
            )
        )
        PropCounter(
            viewModel.filteredCarrierConfig.value.size,
            viewModel.getSizeOfTheList(),
            Modifier.fillMaxWidth()
        )
    }
}