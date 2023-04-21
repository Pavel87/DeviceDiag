package com.pacmac.devinfo.main.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.ui.theme.md_theme_color_scrim

@Composable
fun DeviceInfoDialog(
    title: String,
    msg: String,
    cancellable: Boolean = true,
    disableDismissButton: Boolean = false,
    dismissButtonText: String = "",
    positiveButtonText: String,
    onPositiveAction: () -> Unit,
    onDismiss: () -> Unit = {},
) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(md_theme_color_scrim)
        .clickable { if (cancellable) onDismiss() }) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { }
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = dimensionResource(id = R.dimen.dialog_width))
                    .padding(24.dp)


            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = msg, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (!disableDismissButton) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = dismissButtonText.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    TextButton(
                        onClick = onPositiveAction,
                    ) {
                        Text(
                            text = positiveButtonText.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 820)
@Composable
fun PreviewPermissionExplanation() {
    DeviceInfoTheme {
        DeviceInfoDialog(
            stringResource(id = R.string.missing_permission),
            stringResource(id = R.string.location_permission_msg),
            positiveButtonText = stringResource(id = R.string.request_perm),
            dismissButtonText = stringResource(id = R.string.cancel),
            onPositiveAction = {},
            onDismiss = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 820,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewPermissionExplanationDark() {
    DeviceInfoTheme {
        DeviceInfoDialog(
            stringResource(id = R.string.missing_permission),
            stringResource(id = R.string.location_permission_msg),
            positiveButtonText = stringResource(id = R.string.request_perm),
            dismissButtonText = stringResource(id = R.string.cancel),
            onPositiveAction = {},
            onDismiss = {}
        )
    }
}