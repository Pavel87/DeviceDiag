package com.pacmac.devinfo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.pacmac.devinfo.ui.components.setStatusBarColor

private val DarkColorPalette = darkColorScheme(
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    surfaceVariant = md_theme_dark_surface_variant,
    onSurfaceVariant = md_theme_dark_onSurface_variant,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_on_tertiary
)

private val LightColorPalette = lightColorScheme(
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    surfaceVariant = md_theme_light_surface_variant,
    onSurfaceVariant = md_theme_light_onSurface_variant,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_on_tertiary
)

@Composable
fun DeviceInfoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content =  {
            setStatusBarColor()
            content()
        }
    )
}

@Composable
fun ColorScheme.tileStart() = if (isSystemInDarkTheme()) md_theme_dark_tile_start  else md_theme_light_tile_start

@Composable
fun ColorScheme.tileEnd() = if (isSystemInDarkTheme()) md_theme_dark_tile_end  else md_theme_light_tile_end