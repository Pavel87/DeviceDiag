package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.math.roundToInt

@Composable
fun AdvertView(modifier: Modifier = Modifier, adIdRes: Int) {
    val isInEditMode = LocalInspectionMode.current
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

        val deviceCurrentWidth = LocalConfiguration.current.screenWidthDp

        var containerWidth by remember { mutableStateOf<Int?>(null) }
        modifier.onSizeChanged { containerWidth = it.width }
        var adWidth = deviceCurrentWidth ;
        with(LocalDensity.current) {
            containerWidth?.let { containerWidth ->
                adWidth = (containerWidth / density).roundToInt()
            }
        }

        AndroidView(modifier = modifier.fillMaxWidth(), factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth))
                adUnitId = context.getString(adIdRes)
                loadAd(AdRequest.Builder().build())
            }
        })
    }
}

@Preview(showBackground = true)
@Composable
fun AdvertPreview() {
    AdvertView(adIdRes = 1)
}