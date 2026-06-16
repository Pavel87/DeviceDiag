package com.pacmac.devinfo.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.pacmac.devinfo.DeviceInfoApplication
import com.pacmac.devinfo.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvertView(modifier: Modifier = Modifier, adIdRes: Int) {
    val isInEditMode = LocalInspectionMode.current

    // Check banner-free status
    val context = LocalContext.current
    val app = context.applicationContext
    val bannerFreeUntil = if (!isInEditMode && app is DeviceInfoApplication) {
        val manager = app.rewardedBannerDismissManager
        val timestamp by manager.bannerFreeUntilFlow.collectAsState()
        timestamp
    } else {
        0L
    }

    if (!isInEditMode && bannerFreeUntil > System.currentTimeMillis()) {
        return
    }

    if (isInEditMode) {
        Text(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.surface,
            text = "Advert Here",
        )
    } else {
        var showDismissSheet by remember { mutableStateOf(false) }

        Box(modifier = modifier) {
            val deviceCurrentWidth = LocalConfiguration.current.screenWidthDp

            var containerWidth by remember { mutableStateOf<Int?>(null) }
            val sizeModifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { containerWidth = it.width }
            var adWidth = deviceCurrentWidth
            with(LocalDensity.current) {
                containerWidth?.let { cw ->
                    adWidth = (cw / density).roundToInt()
                }
            }

            AndroidView(modifier = sizeModifier, factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth)
                    )
                    adUnitId = ctx.getString(adIdRes)
                    loadAd(AdRequest.Builder().build())
                }
            })

            // Dismiss "×" icon
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.cancel),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .clickable { showDismissSheet = true }
                    .padding(2.dp)
            )
        }

        if (showDismissSheet) {
            val sheetState = rememberModalBottomSheetState()
            val activity = context as? Activity

            ModalBottomSheet(
                onDismissRequest = { showDismissSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = stringResource(id = R.string.dismiss_ads_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Text(
                    text = stringResource(id = R.string.dismiss_ads_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Button(
                    onClick = {
                        showDismissSheet = false
                        if (activity != null && app is DeviceInfoApplication) {
                            app.rewardedBannerDismissManager.showRewardedAd(
                                activity = activity,
                                onRewarded = { /* banners will hide via flow */ },
                                onFailed = { /* silently fail */ }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = stringResource(id = R.string.watch_video))
                }
                OutlinedButton(
                    onClick = { showDismissSheet = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                ) {
                    Text(text = stringResource(id = R.string.no_thanks))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdvertPreview() {
    AdvertView(adIdRes = 1)
}
