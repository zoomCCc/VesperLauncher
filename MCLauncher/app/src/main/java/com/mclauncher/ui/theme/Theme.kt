package com.mclauncher.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ThemeManager {
    var isDarkTheme by mutableStateOf(true)
}

private val DarkColorScheme = darkColorScheme(
    primary = Green60,
    onPrimary = Color.White,
    primaryContainer = Green40,
    secondary = Brown40,
    onSecondary = Color.White,
    secondaryContainer = Brown20,
    tertiary = Orange40,
    tertiaryContainer = Orange20,
    surface = DarkSurface,
    onSurface = Color(0xFFE8E0D0),
    surfaceVariant = DarkSurfaceVariant,
    background = DarkBackground,
    onBackground = Color(0xFFE8E0D0),
    error = Red40,
    outline = Color(0xFF555570)
)

private val LightColorScheme = lightColorScheme(
    primary = Green60,
    onPrimary = Color.White,
    primaryContainer = Green80,
    secondary = Brown40,
    onSecondary = Color.White,
    secondaryContainer = Brown80,
    tertiary = Orange40,
    tertiaryContainer = Orange80,
    surface = LightSurface,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = LightSurfaceVariant,
    background = LightBackground,
    onBackground = Color(0xFF1A1A2E),
    error = Red40,
    outline = Color(0xFFC0C0B0)
)

@Composable
fun MCLauncherTheme(
    darkTheme: Boolean = ThemeManager.isDarkTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
