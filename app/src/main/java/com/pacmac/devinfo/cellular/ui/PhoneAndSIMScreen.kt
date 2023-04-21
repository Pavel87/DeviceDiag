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
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.cellular.model.BasicPhoneModel
import com.pacmac.devinfo.cellular.CellularViewModelKt
import com.pacmac.devinfo.cellular.model.SIMInfoModel
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView

@Composable
fun PhoneAndSIMScreen(
    viewModel: CellularViewModelKt = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    //BasicPhoneModel.getUIObjects(LocalContext.current, viewModel.basicInfo.value)
    val uiList = arrayListOf<UIObject>()
    uiList.addAll(BasicPhoneModel.getUIObjects(LocalContext.current, viewModel.basicInfo.value))
    uiList.addAll(SIMInfoModel.getUIObjects(LocalContext.current, viewModel.simInfos.value))

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize(),
    ) {
        InfoListView(
            modifier = Modifier.weight(1f),
            data = uiList
        )
        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_14)
    }

}