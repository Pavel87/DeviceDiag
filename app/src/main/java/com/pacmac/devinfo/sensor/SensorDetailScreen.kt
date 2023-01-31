package com.pacmac.devinfo.sensor


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.MainItemView
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorDetailScreen(sensorType: Int, viewModel: SensorDetailViewModel = hiltViewModel()) {

    SensorUtils.stepCounter = 0

    Surface(color = MaterialTheme.colorScheme.surface) {
        val state by viewModel.uiState.collectAsState()

        viewModel.loadSensor(sensorType)
        viewModel.subscribeToSensor()

        DisposableEffect(key1 = Unit) {
            this.onDispose {
                viewModel.unsubscribeToSensor()
            }
        }

        Scaffold(topBar = {
            TopBar(
                title = stringResource(id = R.string.title_activity_sensor_detail)
            )

        }) {
            val modifier = Modifier.padding(it)

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                val configuration = LocalConfiguration.current
                when (configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp)
                        ) {
                            val mod = Modifier.fillMaxWidth()
                            MainSensorContent(sensorType, state, mod)
                        }
                    }

                    else -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp)
                        ) {
                            val mod = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                            MainSensorContent(sensorType, state, mod)
                        }
                    }
                }

                AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_15)
            }
        }
    }
}

@Composable
fun MainSensorContent(sensorType: Int, state: SensorDetailUIState, modifier: Modifier) {
    if (state.name.value.isNotBlank()) {
        SensorView(sensorType, state, modifier)
    } else {
        InvalidSensor(sensorType, modifier)
    }
    SensorReadingsCard(sensorType, state, modifier)
}

@Composable
fun SensorReadingsCard(sensorType: Int, state: SensorDetailUIState, modifier: Modifier) {
    val context = LocalContext.current

    Card(
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier,
    ) {

        val configuration = LocalConfiguration.current

        val boxModifier = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Modifier
        } else {
            Modifier.fillMaxHeight()
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = boxModifier.defaultMinSize(minHeight = 200.dp)
        ) {
            Column {
                SensorUtils.getReadings(sensorType, context, state).forEach { reading ->
                    SensorReading(
                        reading,
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSensorReadingsCard() {
    DeviceInfoTheme() {
        Row() {
            SensorReadingsCard(
                34, SensorDetailUIState(sensorReading1 = 1f), Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

    }
}

@Composable
fun SensorReading(reading: String, modifier: Modifier) {
    Text(
        text = reading,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Composable
fun SensorView(sensorType: Int, state: SensorDetailUIState, modifier: Modifier) {
    val context = LocalContext.current
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
            MainItemView(
                label = stringResource(id = R.string.sensor),
                value = state.name.value,
                ""
            )
            MainItemView(
                label = stringResource(id = R.string.vendor),
                value = state.vendor.value,
                ""
            )
            MainItemView(
                label = stringResource(id = R.string.power),
                value = state.power.value,
                state.power.suffix
            )
            MainItemView(
                label = stringResource(id = R.string.max_range),
                value = state.maxRange.value,
                SensorUtils.getUnits(sensorType, context)
            )
        }
    }
}

@Composable
fun InvalidSensor(sensorType: Int, modifier: Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
            MainItemView(
                label = stringResource(id = R.string.sensor),
                value = String.format(
                    stringResource(id = R.string.sensor_type),
                    sensorType
                ),
                ""
            )
            MainItemView(
                label = stringResource(id = R.string.vendor),
                value = stringResource(id = R.string.unknown),
                ""
            )
            MainItemView(
                label = stringResource(id = R.string.power),
                value = stringResource(id = R.string.unknown),
                ""
            )
        }
    }
}

