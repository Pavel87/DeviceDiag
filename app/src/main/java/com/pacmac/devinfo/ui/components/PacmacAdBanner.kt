package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun PacmacAdBanner(adText: String, modifier: Modifier, onBadgeClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Card(
                modifier = modifier,
                shape = MaterialTheme.shapes.extraSmall,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = adText,
                        style = MaterialTheme.typography.labelMedium,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(2f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.google_badge),
                        contentDescription = "navigate to Goole Play Store",
                        modifier = Modifier
                            .size(width = 150.dp, height = 60.dp)
                            .clickable { onBadgeClick.invoke() }
                    )
                }
            }
        }
    }
}


@Preview(widthDp = 450, showBackground = true)
@Composable
fun PreviewPacmacAdBanner() {
    DeviceInfoTheme() {
        PacmacAdBanner(
            adText = stringResource(id = R.string.icmp_ping_ad), modifier = Modifier.padding(16.dp)
        ) {}
    }
}