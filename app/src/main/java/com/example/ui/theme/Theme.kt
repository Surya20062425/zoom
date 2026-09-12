package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ZoomBlue,
    onPrimary = Color.White,
    primaryContainer = ZoomSurfaceDark,
    onPrimaryContainer = ZoomBlueLight,
    secondary = ZoomOrange,
    onSecondary = Color.White,
    tertiary = ZoomGreen,
    background = ZoomBgDark,
    surface = ZoomSurfaceDark,
    surfaceVariant = ZoomCardDark,
    onBackground = ZoomTextDark,
    onSurface = ZoomTextDark,
    onSurfaceVariant = ZoomTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = ZoomBlue,
    onPrimary = Color.White,
    primaryContainer = ZoomBlueLight,
    onPrimaryContainer = ZoomBlueDark,
    secondary = ZoomOrange,
    onSecondary = Color.White,
    tertiary = ZoomGreen,
    background = ZoomBgLight,
    surface = ZoomSurfaceLight,
    surfaceVariant = ZoomBorderLight,
    onBackground = ZoomTextLight,
    onSurface = ZoomTextLight,
    onSurfaceVariant = ZoomTextSecondaryLight
)

@Composable
fun ZoomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
