package com.pacmac.devinfo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.main.model.FeatureModel
import com.pacmac.devinfo.main.ui.DeviceInfoDialog
import com.pacmac.devinfo.ui.components.ActionButton
import com.pacmac.devinfo.ui.components.WalletUpsell
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.utils.Utils

@Composable
fun NewFeaturesScreen(onClose: () -> Unit, onAppReview: () -> Unit) {

    val newFeautures = listOf(
        FeatureModel(
            stringResource(id = R.string.new_features1),
            "Switch to dark mode and enjoy the new look."
        ),
        FeatureModel(stringResource(id = R.string.new_features2), "Multiple UI changes."),
        FeatureModel(
            stringResource(id = R.string.new_features3),
            "If permissions denied 2 times user is informed about option to enable via Device Settings."
        )
    )

    var displayRateDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    BackHandler(true) {
        onClose()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Surface() {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(), true)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = stringResource(id = R.string.close),
                    modifier = Modifier.clickable { onClose() },
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.what_is_new).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                newFeautures.forEach { item ->
                    FeatureDescription(item)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                ActionButton(
                    text = stringResource(id = R.string.rate_app_new_feature),
                    modifier = Modifier
                        .widthIn(300.dp, 450.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = { onAppReview() },
                    isEnabled = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WalletUpsell(
                        Modifier.padding(8.dp),
                        onClick = { Utils.openWalletAppPlayStore(context) })
                }
            }
        }

        if (displayRateDialog) {
            DeviceInfoDialog(
                title = stringResource(id = R.string.rate_app),
                msg = stringResource(id = R.string.rta_dialog_message),
                onDismiss = {
                    displayRateDialog = false
                },
                onPositiveAction = {
                    Utils.launchPlayStore(context)
                    displayRateDialog = false
                },
                positiveButtonText = stringResource(id = R.string.rateit),
                dismissButtonText = stringResource(id = R.string.no_thanks),
            )
        }
    }
}

@Composable
fun FeatureDescription(feature: FeatureModel) {
    Row() {
        Text(
            text = "•",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(4.dp))


        Column() {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            feature.subtitle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(widthDp = 350, heightDp = 400, showBackground = true)
@Composable
fun PreviewFeatureDescription() {
    DeviceInfoTheme() {
        FeatureDescription(
            FeatureModel(
                stringResource(id = R.string.new_features3),
                "If permissions denied 2 times user is informed about option to enable via Device Settings."
            )
        )
    }
}

@Preview(widthDp = 350, heightDp = 700, showBackground = true)
@Composable
fun PreviewNewFeaturesScreen() {
    DeviceInfoTheme {
        NewFeaturesScreen({}, {})
    }
}