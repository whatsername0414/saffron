package com.saffron.cook.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ---- Brand primitives ----
val Saffron  = Color(0xFFC8860A)
val Truffle  = Color(0xFF1A1208)
val Cream    = Color(0xFFFEF3DC)
val Linen    = Color(0xFFF5F0E8)
val Cinnamon = Color(0xFF5C4A2A)

// ---- Saffron ramp (fills, borders, text-on-cream) ----
val Saffron10  = Color(0xFFFEF3DC)
val Saffron20  = Color(0xFFFDE8B4)
val Saffron40  = Color(0xFFF5C76A)
val Saffron60  = Color(0xFFE0A020)
val Saffron100 = Color(0xFFC8860A)
val Saffron120 = Color(0xFFA06808)
val Saffron160 = Color(0xFF5C3C04)

// ---- Semantic (system feedback only — never decorative) ----
val SemanticSuccess = Color(0xFF2D7A4F)
val SemanticWarning = Color(0xFFB85C00)
val SemanticError   = Color(0xFFC0392B)
val SemanticInfo    = Color(0xFF185FA5)

// ---- Light color scheme ----
internal val LightColorScheme = lightColorScheme(
    primary              = Saffron,
    onPrimary            = Color.White,
    primaryContainer     = Saffron20,
    onPrimaryContainer   = Saffron160,
    inversePrimary       = Saffron40,
    secondary            = Cinnamon,
    onSecondary          = Color.White,
    secondaryContainer   = Cream,
    onSecondaryContainer = Truffle,
    tertiary             = Color(0xFF7A6040),
    onTertiary           = Color.White,
    tertiaryContainer    = Saffron10,
    onTertiaryContainer  = Truffle,
    background           = Linen,
    onBackground         = Truffle,
    surface              = Color.White,
    onSurface            = Truffle,
    surfaceVariant       = Linen,
    onSurfaceVariant     = Cinnamon,
    surfaceTint          = Saffron,
    inverseSurface       = Truffle,
    inverseOnSurface     = Cream,
    outline              = Color(0xFFD3CFC8),
    outlineVariant       = Color(0xFFE4DFD5),
    error                = SemanticError,
    onError              = Color.White,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),
    scrim                = Color(0xFF000000),
)

// ---- Dark color scheme ----
internal val DarkColorScheme = darkColorScheme(
    primary              = Color(0xFFE09A1A),
    onPrimary            = Color(0xFF110D06),
    primaryContainer     = Saffron160,
    onPrimaryContainer   = Saffron20,
    inversePrimary       = Saffron100,
    secondary            = Color(0xFFBCA882),
    onSecondary          = Color(0xFF1A1208),
    secondaryContainer   = Color(0xFF3D2E1A),
    onSecondaryContainer = Color(0xFFE0C8A0),
    tertiary             = Color(0xFFD4AC6A),
    onTertiary           = Color(0xFF1A1208),
    tertiaryContainer    = Color(0xFF3A2C14),
    onTertiaryContainer  = Color(0xFFF5DFB0),
    background           = Color(0xFF110D06),
    onBackground         = Color(0xFFF5E8C8),
    surface              = Color(0xFF1E1608),
    onSurface            = Color(0xFFF5E8C8),
    surfaceVariant       = Color(0xFF2A2010),
    onSurfaceVariant     = Color(0xFFA09070),
    surfaceTint          = Color(0xFFE09A1A),
    inverseSurface       = Color(0xFFF5E8C8),
    inverseOnSurface     = Color(0xFF1A1208),
    outline              = Color(0xFF3A3020),
    outlineVariant       = Color(0xFF2A2212),
    error                = Color(0xFFFF897D),
    onError              = Color(0xFF690002),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),
    scrim                = Color(0xFF000000),
)
