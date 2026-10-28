package com.example.androidplayer.ui.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4FF),
    onPrimary = Color(0xFF002B66),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF5EEAD4),
    onSecondary = Color(0xFF00332D),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = Esp32DarkBackground,
    onBackground = Color(0xFFE2E8F0),
    surface = Esp32DarkSurface,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Esp32DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Esp32DarkOutline,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

private val LightColorScheme = lightColorScheme(
    primary = Esp32Blue,
    onPrimary = Color.White,
    primaryContainer = Esp32BlueContainer,
    onPrimaryContainer = Esp32BlueOnContainer,
    secondary = Esp32Green,
    onSecondary = Color.White,
    secondaryContainer = Esp32GreenContainer,
    onSecondaryContainer = Esp32GreenOnContainer,
    tertiary = Esp32Orange,
    onTertiary = Color.White,
    tertiaryContainer = Esp32OrangeContainer,
    onTertiaryContainer = Esp32OrangeOnContainer,
    background = Esp32SurfaceSubtle,
    onBackground = Color(0xFF111827),
    surface = Esp32SurfaceElevated,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFEDF1F7),
    onSurfaceVariant = Esp32Grey,
    outline = Color(0xFFCBD5E1),
    error = Esp32Red,
    onError = Color.White,
    errorContainer = Esp32RedContainer,
    onErrorContainer = Esp32RedOnContainer
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AndroidPlayerTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("esp32_player_prefs", Context.MODE_PRIVATE)
    }
    val systemDark = isSystemInDarkTheme()
    var darkTheme by remember {
        mutableStateOf(prefs.getBoolean("dark_theme", systemDark))
    }
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "dark_theme") {
                darkTheme = prefs.getBoolean("dark_theme", darkTheme)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

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
        shapes = AppShapes,
        content = content
    )
}
