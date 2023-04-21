package com.pacmac.devinfo.gps

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.gps.models.NMEALog
import com.pacmac.devinfo.ui.anim.FadeInfinite
import com.pacmac.devinfo.ui.components.ActionButton
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.ui.theme.color_title_bg

@Composable
fun NMEALogScreen(viewModel: GPSViewModelKt = hiltViewModel()) {

    var isStarted by remember { mutableStateOf(false) }

    val buttonText = if (isStarted) {
        stringResource(id = R.string.nmea_stop)
    } else {
        viewModel.unsubscribeToNMEALog()
        stringResource(id = R.string.start_logging)
    }

    DisposableEffect(key1 = Unit, effect = {
        this.onDispose {
            isStarted = false
            viewModel.unsubscribeToNMEALog()
        }
    })

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            NMEAStatusBar(isStarted)

            ActionButton(
                text = buttonText,
                isEnabled = true,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(vertical = 8.dp),
                onClick = {
                    isStarted = !isStarted
                    if (isStarted) {
                        viewModel.subscribeToNMEALog()
                    } else {
                        viewModel.unsubscribeToNMEALog()
                    }
                })

            LogView(viewModel.nmeaLog.value)
        }
    }
}

@Composable
fun LogView(nmeaLogs: List<NMEALog>) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {

        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(8.dp),
            content = {
            items(nmeaLogs) { log ->
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            append(log.timeDate)
                        }
                        append(" ${log.message}")
                    }, style = MaterialTheme.typography.bodySmall, modifier = Modifier
                )
            }
        })
    }
}


@Composable
fun NMEAStatusBar(isStarted: Boolean) {
    Surface(modifier = Modifier.fillMaxWidth(), color = color_title_bg) {
        if (isStarted) {
            FadeInfinite() {
                Text(
                    text = stringResource(id = R.string.nmea_fetching_data),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
            }
        } else {
            Text(
                text = stringResource(id = R.string.nmea_fetching_data_stopped),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewLogView() {

    val logs = arrayListOf<NMEALog>()
    logs.add(NMEALog("23:00:32", "This is an NMEA log message1"))
    logs.add(NMEALog("23:32:02", "This is an NMEA log message2"))
    logs.add(NMEALog("23:56:11", "This is an NMEA log message3"))

    DeviceInfoTheme() {
        LogView(nmeaLogs = logs)
    }
}