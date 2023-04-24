package com.pacmac.devinfo.export.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.components.ActionButton
import com.pacmac.devinfo.ui.components.SecondaryButton
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.ui.theme.color_export_earned_star

@Composable
fun ExportScreen(
    isCompactScreen: Boolean,
    exportCount: Int,
    loadAdEnabled: Boolean = true,
    isAdLoading: Boolean = false,
    onAdClick: () -> Unit,
    onExportClick: () -> Unit,
    onBack: () -> Unit
) {
    val contentModifier =
        if (isCompactScreen) Modifier.fillMaxWidth() else Modifier.width(450.dp)


    Box {
        Scaffold(topBar = {
            TopBar(
                title = stringResource(id = R.string.export_data_title),
                exportVisible = false,
                hasNavigationIcon = true,
                onBack = { onBack() }
            )
        }) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(it)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.export_data_description),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    ExportCard(exportCount, loadAdEnabled, contentModifier, onAdClick)

                    Spacer(modifier = Modifier.height(32.dp))

                    ActionButton(
                        text = stringResource(id = R.string.export_btn),
                        modifier = Modifier
                            .wrapContentWidth()
                            .widthIn(min = 140.dp)
                            .align(Alignment.CenterHorizontally),
                        isEnabled = exportCount > 0,
                        onClick = onExportClick
                    )
                }
            }
        }

        if(isAdLoading) {
            LoadingScreen()
        }

    }
}

@Composable
fun ColumnScope.ExportCard(
    exportCount: Int,
    loadAdEnabled: Boolean,
    modifier: Modifier,
    onAdClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier
            .align(Alignment.CenterHorizontally)
    ) {

        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = exportCount.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(id = R.string.export_available_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                for (i in 0 until 5) {

                    val colorTint =
                        if (i < exportCount) color_export_earned_star else MaterialTheme.colorScheme.onPrimaryContainer

                    Image(
                        painter = painterResource(id = R.drawable.baseline_stars_black_36dp),
                        contentDescription = stringResource(
                            id = R.string.earning_export_slots
                        ),
                        contentScale = ContentScale.FillWidth,
                        colorFilter = ColorFilter.tint(color = colorTint),
                        modifier = Modifier.width(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.export_watch_ad_description),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SecondaryButton(
                    text = stringResource(id = R.string.earn_slot_btn_text),
                    modifier = Modifier
                        .wrapContentWidth(),
                    enabled = loadAdEnabled,
                    onClick = onAdClick
                )
            }
        }
    }
}

@Preview(heightDp = 600, widthDp = 400, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewExportScreen() {
    DeviceInfoTheme() {
        ExportScreen(true, 1, true, true, {}, {}, {})
    }
}

@Preview(heightDp = 600, widthDp = 400, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewExportScreenDark() {
    DeviceInfoTheme() {
        ExportScreen(true, 1, true, false, {}, {}, {})
    }
}