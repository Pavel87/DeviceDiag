package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@Composable
fun ImageItemView(label: String, value: ThreeState) {

    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.CenterVertically)

            )
            Spacer(modifier = Modifier.width(4.dp))

            val drawableRes = when (value) {
                ThreeState.YES -> R.drawable.tick
                ThreeState.NO -> R.drawable.cancel
                else -> R.drawable.maybe
            }

            Image(
                painterResource(id = drawableRes),
                contentDescription = value.name,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun PreviewImageItemView() {
    DeviceInfoTheme {
        ImageItemView("LABEL", ThreeState.NO)
    }
}