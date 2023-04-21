package com.pacmac.devinfo.export.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun LoadingScreen() {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Box(modifier = Modifier.fillMaxSize()) {
            Loader(Modifier.align(Alignment.Center))
        }
    }
}


@Composable
fun Loader(modifier: Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loader))
    LottieAnimation(
        composition,
        iterations = LottieConstants.IterateForever,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .wrapContentWidth()
    )
}

@Preview
@Composable
fun PreviewLoader() {
    DeviceInfoTheme {
        LoadingScreen()
    }
}