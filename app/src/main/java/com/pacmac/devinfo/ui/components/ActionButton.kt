package com.pacmac.devinfo.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme


@Composable
fun ActionButton(
    text: String,
    modifier: Modifier,
    isEnabled: Boolean = false,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = { onClick.invoke() },
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    ) {
        Text(
            text = text.uppercase(),
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        border = ButtonDefaults.outlinedButtonBorder,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.secondary
        ),
        enabled = enabled,
        onClick = { onClick.invoke() },
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Text(
            text = text.uppercase(),
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewActionButton() {
    DeviceInfoTheme() {
        Column {
            ActionButton(stringResource(id = R.string.rate_app), Modifier.fillMaxWidth())
            SecondaryButton("Outlined Button", Modifier.fillMaxWidth(), true)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDarkActionButton() {
    DeviceInfoTheme() {
        Column {
            ActionButton(stringResource(id = R.string.rate_app), Modifier.fillMaxWidth())
            Surface {

                SecondaryButton("Outlined Button", Modifier.fillMaxWidth())
            }
        }
    }
}