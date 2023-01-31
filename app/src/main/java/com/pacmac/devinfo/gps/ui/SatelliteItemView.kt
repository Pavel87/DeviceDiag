package com.pacmac.devinfo.gps.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.gps.Utils
import com.pacmac.devinfo.gps.models.Satellite
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun SatelliteItemView(satellite: Satellite, modfier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modfier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .wrapContentHeight()
                .fillMaxWidth()
        ) {

            Text(text = satellite.id.toString(), style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    SatValue(
                        stringResource(id = R.string.constellation_label),
                        satellite.constellationType,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        Modifier.fillMaxWidth()
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column() {
                        SatValue(stringResource(id = R.string.pnr), satellite.pnr.toString())
                        SatValue(
                            stringResource(id = R.string.snr),
                            Utils.roundTo1Decimal(satellite.snr)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column() {
                        SatValue(
                            stringResource(id = R.string.azimuth),
                            Utils.roundTo0Decimals(satellite.azimuth)
                        )
                        SatValue(
                            stringResource(id = R.string.elevation),
                            Utils.roundTo0Decimals(satellite.elevation)
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun SatValue(
    label: String,
    value: String,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.wrapContentHeight(),
        horizontalArrangement = horizontalArrangement
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier,
            textAlign = TextAlign.Start
        )

        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSatValue() {
    DeviceInfoTheme {
        SatValue(stringResource(id = R.string.pnr), "7", Arrangement.spacedBy(8.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSatelliteItemView() {

    val satellite = Satellite(88, 234.1f, 74, 123.212f, 35.12331f)
    DeviceInfoTheme {
        SatelliteItemView(satellite = satellite)
    }
}