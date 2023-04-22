package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ResolutionUIObject
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import com.pacmac.devinfo.camera.model.Resolution
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun InfoListView(
    modifier: Modifier, data: List<UIObject>,
    header: @Composable () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LazyColumn(modifier = modifier,
        state = listState,
        content = {
            item {
                header.invoke()
            }
            items(data) { item ->
                when (item.type) {
                    ListType.TITLE -> {
                        TitleItemView(label = item.label, value = item.value)
                    }

                    ListType.MAIN -> {
                        MainItemView(
                            label = item.label,
                            value = item.value,
                            suffix = item.suffix ?: ""
                        )
                    }

                    ListType.ICON -> {
                        ImageItemView(label = item.label, value = item.state)
                    }

                    ListType.RESOLUTION -> {
                        item as ResolutionUIObject
                        ResolutionItemView(title = item.title, resolutions = item.resolutions)
                    }
                }
                if (item.type != ListType.RESOLUTION) {
                    Divider()
                }

            }
        }
    )

}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun PreviewItemListView() {
    DeviceInfoTheme {

        val data = arrayListOf<UIObject>()
        data.add(
            UIObject(
                stringResource(id = R.string.title_activity_battery_info),
                "",
                ListType.TITLE
            )
        )
        data.add(
            UIObject(
                stringResource(id = R.string.param),
                stringResource(id = R.string.value),
                ListType.TITLE
            )
        )
        data.add(
            UIObject(
                stringResource(id = R.string.wifi_enabled),
                stringResource(id = R.string.yes_string)
            )
        )
        data.add(UIObject(stringResource(id = R.string.battery_temperature), "40", "°C"))
        data.add(
            UIObject(
                stringResource(id = R.string.network_wifi_6ghz_band),
                ThreeState.YES,
                ListType.ICON
            )
        )
        val res = arrayListOf<Resolution>()
        res.apply {
            add(Resolution(3264, 2448))
            add(Resolution(2448, 2448))
            add(Resolution(960, 720))
            add(Resolution(360, 180))
            add(Resolution(1080, 920))
            add(Resolution(2048, 1080))
            add(Resolution(4896, 2048))
        }
        data.add(ResolutionUIObject(stringResource(id = R.string.supported_picture_size), res))

        InfoListView(
            modifier = Modifier.fillMaxWidth(),
            data
        )
    }
}