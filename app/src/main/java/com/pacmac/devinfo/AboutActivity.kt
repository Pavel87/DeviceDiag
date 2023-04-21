package com.pacmac.devinfo

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.main.ui.DeviceInfoDialog
import com.pacmac.devinfo.ui.components.ActionButton
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.components.WalletUpsell
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.utils.Utils
import java.util.*

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceInfoTheme {
                var displayRateDialog by remember { mutableStateOf(false) }

                val context = LocalContext.current

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = {
                            TopBar(stringResource(id = R.string.title_activity_about))
                        },
                        content = {
                            AboutContent(
                                Modifier.padding(it),
                                getAppVersionName(),
                                onRateAppClick = { displayRateDialog = true })
                        })

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
        }
    }

    @Composable
    fun AboutCard(version: String, modifier: Modifier) {
        Card(
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = version, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.app_description),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.app_author),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }

    @Composable
    fun AboutContent(modifier: Modifier, version: String = "9.9.9", onRateAppClick: () -> Unit) {
        val context = LocalContext.current
        Surface(color = MaterialTheme.colorScheme.surface, modifier = modifier) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxHeight()
            ) {
                WalletUpsell(Modifier, onClick = { Utils.openWalletAppPlayStore(context) })
                Spacer(modifier = Modifier.height(24.dp))
                AboutCard(version, Modifier)
                Spacer(modifier = Modifier.height(32.dp))
                ActionButton(
                    text = stringResource(id = R.string.rate_app),
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    onClick = onRateAppClick,
                    isEnabled = true
                )
            }
        }
    }

    @Preview(showBackground = true, widthDp = 500)
    @Composable
    fun PreviewAboutCard() {
        DeviceInfoTheme {
            AboutCard("9.9.9", Modifier.padding(16.dp))
        }
    }

    //    @Preview(showBackground = true, widthDp = 500)
//    @Composable
//    fun PreviewWalletUpsell() {
//        DeviceInfoTheme {
//            WalletUpsell(Modifier.padding(16.dp)) {}
//        }
//    }
//
    @Preview(showBackground = true, widthDp = 500, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun PreviewAboutScreen() {
        DeviceInfoTheme {
            AboutContent(Modifier, onRateAppClick = {})
        }
    }

    @Suppress("DEPRECATION")
    private fun getAppVersionName(): String {
        var s = "Unknown"
        try {
            s = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName, PackageManager.PackageInfoFlags.of(0)
                ).versionName
            } else {
                packageManager.getPackageInfo(packageName, 0).versionName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return String.format(
            Locale.ENGLISH, "%s %s", resources.getString(R.string.version_text), s
        )
    }
}