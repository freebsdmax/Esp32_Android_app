package com.example.androidplayer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Тёмная цветовая схема
private val DarkColorScheme = darkColorScheme(
    primary = Esp32Blue,
    onPrimary = Esp32BlueOnContainer,
    primaryContainer = Esp32Blue,
    onPrimaryContainer = Esp32BlueOnContainer,
    secondary = Esp32Green,
    onSecondary = Esp32GreenOnContainer,
    secondaryContainer = Esp32GreenContainer,
    onSecondaryContainer = Esp32GreenOnContainer,
    tertiary = Esp32Orange,
    onTertiary = Esp32OrangeOnContainer,
    tertiaryContainer = Esp32OrangeContainer,
    onTertiaryContainer = Esp32OrangeOnContainer,
    background = Esp32DarkBackground,
    onBackground = Esp32BlueOnContainer,
    surface = Esp32DarkSurface,
    onSurface = Esp32BlueOnContainer,
    surfaceVariant = Esp32DarkSurfaceVariant,
    onSurfaceVariant = Esp32Grey,
    outline = Esp32DarkOutline,
    error = Esp32Red,
    onError = Esp32RedOnContainer,
    errorContainer = Esp32RedContainer,
    onErrorContainer = Esp32RedOnContainer
)

// Светлая цветовая схема
private val LightColorScheme = lightColorScheme(
    primary = Esp32Blue,
    onPrimary = Esp32BlueContainer,
    primaryContainer = Esp32BlueContainer,
    onPrimaryContainer = Esp32BlueOnContainer,
    secondary = Esp32Green,
    onSecondary = Esp32GreenContainer,
    secondaryContainer = Esp32GreenContainer,
    onSecondaryContainer = Esp32GreenOnContainer,
    tertiary = Esp32Orange,
    onTertiary = Esp32OrangeContainer,
    tertiaryContainer = Esp32OrangeContainer,
    onTertiaryContainer = Esp32OrangeOnContainer,
    background = Esp32SurfaceSubtle,
    onBackground = Esp32BlueOnContainer,
    surface = Esp32SurfaceElevated,
    onSurface = Esp32BlueOnContainer,
    surfaceVariant = Esp32SurfaceSubtle,
    onSurfaceVariant = Esp32Grey,
    outline = Esp32Grey,
    error = Esp32Red,
    onError = Esp32RedContainer,
    errorContainer = Esp32RedContainer,
    onErrorContainer = Esp32RedOnContainer
)

/**
 * Тема приложения Android для управления ESP32 Music Player
 */
@Composable
fun AndroidPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Используем Material You на Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}