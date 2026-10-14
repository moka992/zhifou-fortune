package com.zhifou.fortune

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class FortunePalette(
    val ink: Color, val panel: Color, val panelAlt: Color, val line: Color,
    val gold: Color, val mint: Color, val rose: Color, val danger: Color,
    val textMain: Color, val textSub: Color,
    val chatBubbleAssistant: Color, val chatBubbleUser: Color, val chatText: Color,
    val isLight: Boolean,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF216451), onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEDE5), onPrimaryContainer = Color(0xFF164B3D),
    inversePrimary = Color(0xFFAFD9C3),
    secondary = Color(0xFF4B647A), onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4EDF5), onSecondaryContainer = Color(0xFF284359),
    tertiary = Color(0xFFA8534C), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF8E5DF), onTertiaryContainer = Color(0xFF733D37),
    background = Color(0xFFF7F8F7), onBackground = Color(0xFF222826),
    surface = Color(0xFFF7F8F7), onSurface = Color(0xFF222826),
    surfaceVariant = Color(0xFFEDF0ED), onSurfaceVariant = Color(0xFF666F6B),
    outline = Color(0xFF89928D), outlineVariant = Color(0xFFDFE5E0),
    surfaceTint = Color.Transparent,
    inverseSurface = Color(0xFF28322D), inverseOnSurface = Color(0xFFF3F5F2),
    surfaceBright = Color.White, surfaceDim = Color(0xFFE8ECE8),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFF2F5F1),
    surfaceContainer = Color(0xFFEDF1EC), surfaceContainerHigh = Color(0xFFE7ECE6),
    surfaceContainerHighest = Color(0xFFDDE5DC),
    error = Color(0xFFB3433E), errorContainer = Color(0xFFFFE4DF),
    onError = Color.White, onErrorContainer = Color(0xFF632621),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFACD5BE), onPrimary = Color(0xFF1B3D2D),
    primaryContainer = Color(0xFF2B4437), onPrimaryContainer = Color(0xFFD8ECDD),
    inversePrimary = Color(0xFF216451),
    secondary = Color(0xFFB7CCD9), onSecondary = Color(0xFF243D4C),
    secondaryContainer = Color(0xFF2C3B45), onSecondaryContainer = Color(0xFFDBE7EF),
    tertiary = Color(0xFFE5B0A4), onTertiary = Color(0xFF502F2A),
    tertiaryContainer = Color(0xFF493431), onTertiaryContainer = Color(0xFFFFDFD6),
    background = Color(0xFF151918), onBackground = Color(0xFFE8EEE8),
    surface = Color(0xFF151918), onSurface = Color(0xFFE8EEE8),
    surfaceVariant = Color(0xFF303733), onSurfaceVariant = Color(0xFFAAB6AD),
    outline = Color(0xFF7E8A82), outlineVariant = Color(0xFF343D37),
    surfaceTint = Color.Transparent,
    inverseSurface = Color(0xFFE8EEE8), inverseOnSurface = Color(0xFF28322D),
    surfaceBright = Color(0xFF353D38), surfaceDim = Color(0xFF121614),
    surfaceContainerLowest = Color(0xFF111512), surfaceContainerLow = Color(0xFF1C221E),
    surfaceContainer = Color(0xFF222A24), surfaceContainerHigh = Color(0xFF2A332D),
    surfaceContainerHighest = Color(0xFF343F37),
    error = Color(0xFFF0ADA5), errorContainer = Color(0xFF542B28),
    onError = Color(0xFF542B28), onErrorContainer = Color(0xFFFFDAD5),
)

val LightPalette = FortunePalette(
    ink = LightColorScheme.background, panel = Color.White,
    panelAlt = LightColorScheme.surfaceContainer, line = LightColorScheme.outlineVariant,
    gold = LightColorScheme.primary, mint = LightColorScheme.secondary,
    rose = LightColorScheme.tertiary, danger = LightColorScheme.error,
    textMain = LightColorScheme.onSurface, textSub = LightColorScheme.onSurfaceVariant,
    chatBubbleAssistant = Color(0xFFE9F0F7), chatBubbleUser = Color(0xFFE2EEE4),
    chatText = LightColorScheme.onSurface, isLight = true,
)

val DarkPalette = FortunePalette(
    ink = DarkColorScheme.background, panel = DarkColorScheme.surfaceContainerLow,
    panelAlt = DarkColorScheme.surfaceContainerHigh, line = DarkColorScheme.outlineVariant,
    gold = DarkColorScheme.primary, mint = DarkColorScheme.secondary,
    rose = DarkColorScheme.tertiary, danger = DarkColorScheme.error,
    textMain = DarkColorScheme.onSurface, textSub = DarkColorScheme.onSurfaceVariant,
    chatBubbleAssistant = Color(0xFF27343E), chatBubbleUser = Color(0xFF2B3C30),
    chatText = DarkColorScheme.onSurface, isLight = false,
)

val LocalFortunePalette = staticCompositionLocalOf { DarkPalette }
internal enum class GeminiGlassTone { Neutral, Gold, Teal, Rose }

/** Wallpaper remains user-controlled; optical composition is reserved for floating navigation. */
@Composable
internal fun GeminiAmbientBackdrop(
    modifier: Modifier = Modifier,
    backgroundStyle: AppBackgroundStyle = AppBackgroundStyle.Solid,
    customImageUri: String = "",
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val backdrop = rememberGlassBackdropState()
    val scene = rememberGlassSceneState()
    Box(modifier.background(colors.background)) {
        Box(Modifier.matchParentSize().captureGlassBackdrop(backdrop).background(colors.background)) {
            AppBackgroundImage(backgroundStyle, customImageUri, Modifier.matchParentSize())
            if (backgroundStyle != AppBackgroundStyle.Solid) {
                Box(Modifier.matchParentSize().background(colors.background.copy(alpha = 0.88f)))
            }
        }
        RealtimeGlassScene(scene, backdrop, Modifier.matchParentSize())
        CompositionLocalProvider(LocalGlassBackdrop provides backdrop, LocalGlassScene provides scene) {
            content()
        }
    }
}

/** Shared content surface. Opaque interiors keep body copy readable on every wallpaper. */
@Composable
internal fun GeminiGlassPanel(
    modifier: Modifier = Modifier,
    tone: GeminiGlassTone = GeminiGlassTone.Neutral,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val fill = when (tone) {
        GeminiGlassTone.Neutral -> LocalFortunePalette.current.panel
        GeminiGlassTone.Gold -> colors.primaryContainer
        GeminiGlassTone.Teal -> colors.secondaryContainer
        GeminiGlassTone.Rose -> colors.tertiaryContainer
    }
    Box(modifier.clip(shape).background(fill), content = content)
}

internal val FortunePalette.accentFill: Color
    get() = if (isLight) Color(0xFF216451) else Color(0xFFACD5BE)
internal val FortunePalette.onAccentFill: Color
    get() = if (isLight) Color.White else Color(0xFF1B3D2D)
internal val FortunePalette.selectionFill: Color
    get() = if (isLight) Color(0xFFDCEDE5) else Color(0xFF2B4437)

private fun type(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = FontFamily.SansSerif, fontWeight = weight,
    fontSize = size.sp, lineHeight = height.sp, letterSpacing = 0.sp,
)

private val ZhiFouTypography = Typography(
    displayLarge = type(52, 60, FontWeight.Medium), displayMedium = type(42, 50, FontWeight.Medium),
    displaySmall = type(34, 42, FontWeight.Medium), headlineLarge = type(30, 38, FontWeight.SemiBold),
    headlineMedium = type(27, 36, FontWeight.SemiBold), headlineSmall = type(23, 32, FontWeight.SemiBold),
    titleLarge = type(20, 28, FontWeight.SemiBold), titleMedium = type(16, 24, FontWeight.SemiBold),
    titleSmall = type(14, 22, FontWeight.Medium), bodyLarge = type(16, 26),
    bodyMedium = type(14, 23), bodySmall = type(12, 19), labelLarge = type(14, 20, FontWeight.Medium),
    labelMedium = type(12, 18, FontWeight.Medium), labelSmall = type(11, 16, FontWeight.Medium),
)

private val ZhiFouShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp), small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp), large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun ZhiFouTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFortunePalette provides if (darkTheme) DarkPalette else LightPalette) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = ZhiFouTypography, shapes = ZhiFouShapes, content = content,
        )
    }
}
