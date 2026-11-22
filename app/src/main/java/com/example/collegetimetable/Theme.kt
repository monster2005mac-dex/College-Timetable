package com.example.collegetimetable

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Theme Semantic Color Tokens ---
val GlassSuccessColor = Color(0xFF4CAF50)
val OnGlassSuccessColor = Color(0xFFC8E6C9)

val GlassErrorColor = Color(0xFFE53935)
val OnGlassErrorColor = Color(0xFFFFCDD2)

val GlassWarningColor = Color(0xFFFFB300)
val OnGlassWarningColor = Color(0xFFFFF8E1)

val GlassInfoColor = Color(0xFF2196F3)
val OnGlassInfoColor = Color(0xFFBBDEFB)

// --- Theme Shape Tokens ---
val GlassCardShape = RoundedCornerShape(24.dp)
val GlassDialogShape = RoundedCornerShape(28.dp)
val GlassInputShape = RoundedCornerShape(16.dp)
val GlassPillShape = CircleShape

// --- Google Fonts Typography ---
val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val poppinsFont = GoogleFont("Poppins")

val AppFontFamily = FontFamily(
    Font(googleFont = poppinsFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = poppinsFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = poppinsFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = poppinsFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

val GlassTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = Color.White
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color.White.copy(alpha = 0.95f)
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.90f)
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.75f)
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        color = Color.White
    )
)

@Composable
fun TimetableTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme(
        primary = Color(0xFFE0B0FF),
        onPrimary = Color.Black,
        secondary = GlassInfoColor,
        onSecondary = Color.White,
        error = GlassErrorColor,
        onError = OnGlassErrorColor,
        surface = Color.Transparent,
        onSurface = Color.White,
        onSurfaceVariant = Color.White.copy(alpha = 0.75f),
        background = Color.Transparent,
        onBackground = Color.White
    )
    MaterialTheme(
        colorScheme = darkColors,
        typography = GlassTypography,
        content = content
    )
}
