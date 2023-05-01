package com.pacmac.devinfo.export.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.components.WalletUpsell
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.utils.Utils

@Composable
fun PromoScreen(
    windowSizeClass: WindowWidthSizeClass,
    onClose: (playStoreVisited: Boolean) -> Unit
) {

    val context = LocalContext.current
    BackHandler(true) {
        onClose(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(), true)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = stringResource(id = R.string.close),
                    modifier = Modifier.clickable { onClose(false) },
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                val contentModifier =
                    if (windowSizeClass == WindowWidthSizeClass.Compact) Modifier.fillMaxWidth() else Modifier.width(
                        450.dp
                    )

                Image(
                    painter = painterResource(id = R.drawable.wallet_preview),
                    contentDescription = stringResource(id = R.string.close),
                    contentScale = ContentScale.FillWidth,
                    modifier = contentModifier
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = contentModifier.align(Alignment.CenterHorizontally)
                ) {
                    WalletUpsell(
                        Modifier.padding(8.dp),
                        windowSizeClass,
                        onClick = {
                            Utils.openWalletAppPlayStore(context)
                            onClose(true)
                        })
                }
            }
        }
    }
}

@Preview(
    widthDp = 1200,
    heightDp = 700,
    showBackground = true,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun PreviewPromoScreen() {
    DeviceInfoTheme {
        PromoScreen(WindowWidthSizeClass.Compact, {})
    }
}