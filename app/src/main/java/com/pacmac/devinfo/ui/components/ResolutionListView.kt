package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.pacmac.devinfo.camera.model.Resolution
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun ResolutionItemView(title: String, resolutions: List<Resolution>) {

    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {

                Text(
                    text = "${resolutions.size}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth()
                        .padding(start = 24.dp)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                    )

                    Text(
                        text = "[${stringResource(id = R.string.width).lowercase()} x ${stringResource(id = R.string.height).lowercase()}]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ResolutionsCardView(resolutions)
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResolutionsCardView(resolutions: List<Resolution>) {
    Card(
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            resolutions.forEach { resolution ->
                ResolutionView(resolution = resolution)
            }
        }

    }
}


@Composable
private fun ResolutionView(resolution: Resolution) {
    Row(modifier = Modifier.wrapContentWidth()) {

        Text(
            text = "${resolution.width}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "x",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "${resolution.height}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun PreviewResolutionListView() {


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
    DeviceInfoTheme {
        ResolutionItemView("Supported Image Size", res)
    }
}