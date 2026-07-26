package it.ric.chess.core.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = appColorScheme(),
        content = content,
    )
}

@Composable
private fun appColorScheme(): ColorScheme {
    val isDark = isSystemInDarkTheme()
    return remember(isDark) { if (isDark) appDarkColorScheme() else appLightColorScheme() }
}

private fun appDarkColorScheme(): ColorScheme =
    darkColorScheme(
        primary = Color(0xFFD7CCC8),
        onPrimary = Color(0xFF3E2723),
        primaryContainer = Color(0xFF5D4037),
        onPrimaryContainer = Color(0xFFD7CCC8),
        secondary = Color(0xFFBCAAA4),
        onSecondary = Color(0xFF3E2723),
        secondaryContainer = Color(0xFF4E342E),
        onSecondaryContainer = Color(0xFFBCAAA4),
        tertiary = Color(0xFF90A4AE),
        onTertiary = Color(0xFF263238),
        tertiaryContainer = Color(0xFF3E2723),
        onTertiaryContainer = Color(0xFF90A4AE),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF1A1A1A),
        onBackground = Color(0xFFE6E1E5),
        surface = Color(0xFF1A1A1A),
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF4E444B),
        onSurfaceVariant = Color(0xFFD1C4CC),
        outline = Color(0xFF9A8D96),
        surfaceDim = Color(0xFF131313),
        surfaceBright = Color(0xFF393939),
        surfaceContainerLowest = Color(0xFF0E0E0E),
        surfaceContainerLow = Color(0xFF1C1C1C),
        surfaceContainer = Color(0xFF202020),
        surfaceContainerHigh = Color(0xFF2B2B2B),
        surfaceContainerHighest = Color(0xFF363636),
    )

private fun appLightColorScheme(): ColorScheme =
    lightColorScheme(
        primary = Color(0xFF795548),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFEAD8C0),
        onPrimaryContainer = Color(0xFF3E2723),
        secondary = Color(0xFF5D4037),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFF59D),
        onSecondaryContainer = Color(0xFF3E2723),
        tertiary = Color(0xFF455A64),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFC8E6C9),
        onTertiaryContainer = Color(0xFF002008),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFFAF9F6),
        onBackground = Color(0xFF1C1B1F),
        surface = Color(0xFFFAF9F6),
        onSurface = Color(0xFF1C1B1F),
        surfaceVariant = Color(0xFFE7E0D3),
        onSurfaceVariant = Color(0xFF49454E),
        outline = Color(0xFF7A757F),
        surfaceDim = Color(0xFFDED9D0),
        surfaceBright = Color(0xFFFFF9F0),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF7F3EA),
        surfaceContainer = Color(0xFFF1EDE4),
        surfaceContainerHigh = Color(0xFFEBE7DE),
        surfaceContainerHighest = Color(0xFFE6E2D9),
    )
