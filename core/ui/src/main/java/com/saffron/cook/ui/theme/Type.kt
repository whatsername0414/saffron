package com.saffron.cook.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.saffron.cook.core.ui.R

val FontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val PlayfairDisplayFamily = FontFamily(
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = FontProvider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = FontProvider, weight = FontWeight.Medium),
)

val InterFamily = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = FontProvider, weight = FontWeight.Light),
    Font(googleFont = GoogleFont("Inter"), fontProvider = FontProvider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Inter"), fontProvider = FontProvider, weight = FontWeight.Medium),
)

val Typography = Typography(
    // Playfair Display — names & moments (never below 18sp, never in paragraphs)
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 55.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 43.sp,
        letterSpacing = (-0.3).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 35.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 35.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 29.sp,
        letterSpacing = 0.sp,
    ),
    // Inter — everything functional
    headlineSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 27.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Light,
        fontSize = 15.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),
    // overline — uppercase with tracking (apply TextDecoration.uppercase at call site)
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 1.1.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
    ),
)
