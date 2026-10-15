package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF81C784),
    onSecondary = Color(0xFF1B5E20),
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF3E2723),
    background = Color(0xFF101928),
    surface = Color(0xFF1A2639),
    surfaceVariant = Color(0xFF24344D),
    onBackground = Color(0xFFECEFF1),
    onSurface = Color(0xFFECEFF1),
)

private val LightColorScheme = lightColorScheme(
    primary = EduBlue,
    onPrimary = Color.White,
    primaryContainer = EduBlueLight,
    onPrimaryContainer = EduBlueDark,
    secondary = EduGreen,
    onSecondary = Color.White,
    secondaryContainer = EduGreenLight,
    onSecondaryContainer = EduGreenDark,
    tertiary = EduGold,
    onTertiary = Color.Black,
    tertiaryContainer = EduGoldLight,
    onTertiaryContainer = Color(0xFF5D4037),
    background = Color(0xFFF4F6F9),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F4F8),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
            }
        }
    }

    // Wrap with RTL layout direction for Arabic
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
