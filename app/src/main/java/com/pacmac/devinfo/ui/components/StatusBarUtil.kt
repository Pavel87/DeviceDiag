package com.pacmac.devinfo.ui.components

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun setStatusBarColor() {
    val view = LocalView.current
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (Build.VERSION.SDK_INT >= 35) {
                window.statusBarColor = Color.TRANSPARENT
            } else {
                window.statusBarColor = primaryColor
            }
            WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = false
        }
    }
}
