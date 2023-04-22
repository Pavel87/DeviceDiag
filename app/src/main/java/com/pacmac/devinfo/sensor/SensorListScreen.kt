package com.pacmac.devinfo.sensor

import android.content.Intent
import android.content.res.Configuration
import android.hardware.Sensor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SensorListScreen(
    viewModel: SensorViewModelKt = hiltViewModel(),
    onSensorSelected: (type: Int) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    viewModel.retrieveSensors(context)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            launch {
                viewModel.onExportDone.collectLatest { filePath ->
                    filePath?.let {
                        val intent = Intent(context, ExportActivity::class.java)
                        intent.putExtra(ExportUtils.EXPORT_FILE, filePath)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    Scaffold(topBar = {
        TopBar(
            title = stringResource(id = R.string.title_activity_sensor_list), exportVisible = true,
            onExportClick = {
                viewModel.export(context)
            },
            hasNavigationIcon = true,
            onBack = onBack
        )
    }) {
        val modifier = Modifier.padding(it)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxSize(),
        ) {
            SensorList(
                modifier = Modifier.weight(1f),
                data = viewModel.sensorList.value
            ) { onSensorSelected.invoke(it) }
            AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_8)
        }
    }
}

@Composable
fun SensorList(modifier: Modifier, data: List<Sensor>, onSensorSelected: (type: Int) -> Unit) {
    val listState = rememberLazyListState()
    LazyColumn(modifier = modifier, state = listState, content = {
        itemsIndexed(data) { index, item ->
            SensorItem(
                sensorIndex = index + 1, sensorType = item.name, sensorManufacturer = item.vendor
            ) {
                onSensorSelected.invoke(item.type)
            }
            Divider()

        }
    })
}


@Preview(
    showBackground = true,
    widthDp = 400,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewSensorListScreen() {
    DeviceInfoTheme {
        SensorList(Modifier.fillMaxWidth(), arrayListOf()) {}
    }
}