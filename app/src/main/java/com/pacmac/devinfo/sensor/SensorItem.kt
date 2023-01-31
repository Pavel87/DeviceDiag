package com.pacmac.devinfo.sensor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun SensorItem(
    sensorIndex: Int, sensorType: String, sensorManufacturer: String, onItemClick: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = "$sensorIndex",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.width(20.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp)
            ) {
                Text(
                    text = sensorType.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,

                    )
                Text(
                    text = sensorManufacturer,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                )
            }
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = stringResource(id = R.string.navigate_to_sensor_detail),
                Modifier.clickable { onItemClick.invoke() }
                    .size(36.dp)
            )
        }

    }

}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun PreviewSensorItem() {
    DeviceInfoTheme() {
        SensorItem(9, "Gyroscope Gyroscope Gyroscope Gyroscope Gyroscope Gyroscope", "Motorola") {

        }
    }
}