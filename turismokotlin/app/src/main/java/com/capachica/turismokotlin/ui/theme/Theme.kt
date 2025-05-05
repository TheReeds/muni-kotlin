package com.capachica.turismokotlin.ui.theme

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
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),      // Verde claro
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E7D32),  // Verde oscuro
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF4DB6AC),    // Teal claro
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF00796B),  // Teal oscuro
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFFB74D),     // Ámbar claro
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFF9800),  // Ámbar
    onTertiaryContainer = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),      // Verde
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),  // Verde claro
    onPrimaryContainer = Color(0xFF0A3D0A),  // Verde muy oscuro
    secondary = Color(0xFF009688),    // Teal
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),  // Teal claro
    onSecondaryContainer = Color(0xFF004D40),  // Teal muy oscuro
    tertiary = Color(0xFFFF9800),     // Ámbar
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFE0B2),  // Ámbar claro
    onTertiaryContainer = Color(0xFF663D00),  // Ámbar muy oscuro
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF5F5F5),
    onSurface = Color.Black
)

@Composable
fun TurismoKotlinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}