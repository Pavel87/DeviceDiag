package com.pacmac.devinfo.gps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun GPSScreen(modifier: Modifier = Modifier, viewModel: GPSViewModelKt = hiltViewModel()) {


    val gpsData = viewModel.getMainGPSData()

    Surface(modifier = Modifier.fillMaxSize()) {
        InfoListView(
            modifier = modifier,
            data = Utils.getMainGPSInfoList(LocalContext.current, gpsData.value),
            header = {
                MainGPSScreenHeader(lastUpdateTime = viewModel.getUpdateTimeLive().value)
            }
        )
    }
}


@Composable
fun MainGPSScreenHeader(lastUpdateTime: String = "--:--:--") {
    Card(
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.last_update),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = lastUpdateTime,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}


@Preview
@Composable
fun PreviewGPSScreen() {
    DeviceInfoTheme {
        MainGPSScreenHeader()
    }

}