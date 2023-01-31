package com.pacmac.devinfo.gps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.gps.ui.SatelliteItemView
import com.pacmac.devinfo.ui.anim.FadeInfinite
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme


@Composable
fun SatellitesScreen(modifier: Modifier = Modifier, viewModel: GPSViewModelKt = hiltViewModel()) {

    val listState = rememberLazyListState()
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier,
            state = listState,
            content = {
                item {
                    SatelliteHeaderView(viewModel.satellites.value.size)
                }
                items(viewModel.satellites.value) { satellite ->
                    SatelliteItemView(satellite = satellite)
                    Divider()
                }
            })
    }
}

@Composable
fun SatelliteHeaderView(satCount: Int = 0) {

    Surface(color = MaterialTheme.colorScheme.secondary) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.active_satellites).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier,
                textAlign = TextAlign.Start
            )


            // Animate if no FIX yet
            if (satCount <= 0) {
                FadeInfinite() {
                    Text(
                        text = stringResource(id = R.string.gps_first_fix_acquiring),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.End
                    )
                }
            } else {
                Text(
                    text = satCount.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.End
                )
            }


        }
    }
}


@Preview
@Composable
fun PreviewSatelliteHeaderView(modifier: Modifier = Modifier) {
    DeviceInfoTheme {
        SatelliteHeaderView(5)
    }
}