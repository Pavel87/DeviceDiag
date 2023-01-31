package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme


@Composable
fun ActionButton(text: String, modifier: Modifier, onClick: () -> Unit = {}) {
    Button(
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
        ActionButton(stringResource(id = R.string.rate_app), Modifier.fillMaxWidth())
    }
}