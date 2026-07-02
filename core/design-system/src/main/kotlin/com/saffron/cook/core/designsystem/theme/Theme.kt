package com.saffron.cook.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SaffronColors(
    val accent: Color,
    val onCream: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val surfaceCream: Color,
    val borderPrimary: Color,
    val borderTertiary: Color,
)

private val SaffronColorsLight = SaffronColors(
    accent = Color(0xFFC8860A),
    onCream = Color(0xFF5C3C04),
    textPrimary = Color(0xFF1A1208),
    textSecondary = Color(0xFF5C4A2A),
    textTertiary = Color(0xFF8A7A5C),
    surfaceCream = Color(0xFFFEF3DC),
    borderPrimary = Color(0xFFD3CFC8),
    borderTertiary = Color(0xFFE4DFD5),
)

private val SaffronColorsDark = SaffronColors(
    accent = Color(0xFFE09A1A),
    onCream = Color(0xFFF5C76A),
    textPrimary = Color(0xFFF5E8C8),
    textSecondary = Color(0xFFA09070),
    textTertiary = Color(0xFF80724E),
    surfaceCream = Color(0xFF2A2012),
    borderPrimary = Color(0xFF3A3020),
    borderTertiary = Color(0xFF2A2212),
)

val LocalSaffronColors = staticCompositionLocalOf { SaffronColorsLight }

val MaterialTheme.saffronColors: SaffronColors
    @Composable get() = LocalSaffronColors.current

@Composable
fun SaffronTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalSaffronColors provides if (darkTheme) SaffronColorsDark else SaffronColorsLight,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content,
        )
    }
}
