package com.example.collegetimetable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Built-in wallpapers list */
val builtinWallpapers = listOf(
    R.drawable.wallpaper_1 to "Cosmic Aurora",
    R.drawable.wallpaper_2 to "Cyber Sunset",
    R.drawable.wallpaper_3 to "Emerald Forest",
    R.drawable.wallpaper_4 to "Tokyo Midnight",
    R.drawable.wallpaper_5 to "Dreamy Blossom"
)

/** Dynamic wallpaper color scheme */
data class DynamicWallpaperColors(
    val accent: Color = Color(0xFFE0B0FF),
    val dominant: Color = Color(0xFF1D1B20),
    val onAccent: Color = Color.White
)

/** Animated glowing light orbs background behind the glass for ultra-aesthetic 3D depth */
@Composable
fun AmbientAuraBackground(accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_transition")

    val orb1Offset by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1_offset"
    )

    val orb2Offset by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2_offset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Left Glowing Orb
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-60).dp + orb1Offset.dp, y = (-40).dp + (orb1Offset / 2).dp)
                .blur(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bottom Right Glowing Orb
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(380.dp)
                .offset(x = 80.dp + orb2Offset.dp, y = 60.dp + orb2Offset.dp)
                .blur(90.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/** 3D Glassmorphism Panel with drop shadow, lighting border edge, and backdrop blur */
@Composable
fun GlassPanel(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    shape: Shape = GlassCardShape,
    shadowElevation: Dp = 12.dp,
    glassColor: Color = Color.White,
    glassAlphaTop: Float = 0.18f,
    glassAlphaBottom: Float = 0.08f,
    borderAlphaTop: Float = 0.5f,
    borderAlphaBottom: Float = 0.08f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "glass_scale"
    )

    val currentShadow by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) (shadowElevation.value * 0.4f) else shadowElevation.value,
        label = "glass_shadow"
    )

    val isApi31Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val fallbackTint = if (isApi31Plus) Color.Black.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.45f)

    var panelModifier = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .shadow(
            elevation = currentShadow.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.6f),
            spotColor = Color.Black.copy(alpha = 0.8f)
        )
        .clip(shape)

    if (onClick != null) {
        panelModifier = panelModifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    }

    Box(
        modifier = panelModifier
            .hazeChild(
                state = hazeState,
                shape = shape,
                style = HazeStyle(
                    tint = fallbackTint,
                    blurRadius = 24.dp
                )
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassColor.copy(alpha = glassAlphaTop),
                        glassColor.copy(alpha = glassAlphaBottom)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = borderAlphaTop),
                        Color.White.copy(alpha = borderAlphaBottom)
                    )
                ),
                shape = shape
            ),
        content = content
    )
}

/** Extracts dynamic accent colors using AndroidX Palette */
suspend fun extractWallpaperColors(context: Context, wallpaperUri: String?): DynamicWallpaperColors = withContext(Dispatchers.IO) {
    val bitmap: Bitmap? = try {
        if (wallpaperUri.isNullOrEmpty() || wallpaperUri.startsWith("builtin:")) {
            val index = wallpaperUri?.removePrefix("builtin:")?.toIntOrNull() ?: 0
            val resId = builtinWallpapers.getOrNull(index)?.first ?: builtinWallpapers[0].first
            val drawable = ContextCompat.getDrawable(context, resId)
            if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else if (drawable != null) {
                val bmp = Bitmap.createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(100),
                    drawable.intrinsicHeight.coerceAtLeast(100),
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            } else null
        } else {
            val uri = Uri.parse(wallpaperUri)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    } catch (_: Exception) {
        null
    }

    if (bitmap == null) {
        return@withContext DynamicWallpaperColors()
    }

    val palette = Palette.from(bitmap).resizeBitmapArea(100 * 100).generate()
    val vibrant = palette.getVibrantColor(0xFFE0B0FF.toInt())
    val dominant = palette.getDominantColor(0xFF1D1B20.toInt())
    val lightVibrant = palette.getLightVibrantColor(vibrant)

    val accentColor = Color(if (vibrant != 0xFFE0B0FF.toInt()) vibrant else lightVibrant)
    val dominantColor = Color(dominant)

    DynamicWallpaperColors(
        accent = accentColor,
        dominant = dominantColor,
        onAccent = Color.White
    )
}
