package com.pacmac.devinfo.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.main.model.MainInfoModel
import com.pacmac.devinfo.ui.components.ActionButton
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.InfoListView

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModelKt = hiltViewModel(),
    openBuildProp: () -> Unit
) {
    val context = LocalContext.current
    val hasPhonePermission = MainUtilsKt.hasReadPhoneStatePermission(context)
    viewModel.loadMainInfo(hasPhonePermission)

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize(),
    ) {
        InfoListView(
            modifier = Modifier.weight(1f), data = MainInfoModel.toUIModelList(context, viewModel.mainInfo.value)
        )

        ActionButton(
            text = stringResource(id = R.string.show_buildprop),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp)
                .height(50.dp)
                .padding(horizontal = 16.dp),
            onClick = openBuildProp,
            isEnabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_1)
    }
}