package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pacmac.devinfo.R

@Composable
fun WalletUpsell(
    modifier: Modifier,
    windowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val contentModifier = if (windowSizeClass == WindowWidthSizeClass.Compact) {
            Modifier.fillMaxWidth()
        } else {
            Modifier.width(500.dp)
        }
        Column(
            modifier = contentModifier
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiary
            ) {
                Text(
                    text = stringResource(id = R.string.new_app).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.wallet_income_expense_manager),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(painterResource(id = R.drawable.google_badge),
                contentDescription = stringResource(id = R.string.download_wallet_app),
                modifier = Modifier
                    .heightIn(max = 80.dp)
                    .clickable {
                        onClick.invoke()
                    }
                    .align(Alignment.CenterHorizontally))
        }
    }
}
