package com.filex.studio.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FxThemeConfig.resolveDark(): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
}

private val FxTypography: Typography = Typography().let { base ->
    base.copy(
        headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.Medium),
    )
}

@Composable
fun FileXTheme(config: FxThemeConfig, content: @Composable () -> Unit) {
    val dark = config.resolveDark()
    val palette = if (dark) FxPalette.dark else FxPalette.light
    val accent = Color(config.accentArgb)
    val background = config.backgroundArgb?.let { Color(it) } ?: palette.background
    val surface = config.surfaceArgb?.let { Color(it) } ?: palette.surface
    val onSurface = config.textArgb?.let { Color(it) } ?: palette.onSurface

    val base = if (dark) darkColorScheme() else lightColorScheme()
    val colors = base.copy(
        primary = accent,
        onPrimary = Color.White,
        primaryContainer = accent.copy(alpha = 0.24f),
        onPrimaryContainer = onSurface,
        secondary = accent,
        background = background,
        onBackground = onSurface,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = palette.surfaceVariant,
        onSurfaceVariant = palette.onSurfaceMuted,
        surfaceContainer = surface,
        surfaceContainerHigh = palette.surfaceVariant,
        outline = palette.outline,
        outlineVariant = palette.outline,
        error = FxPalette.error,
    )

    val radius = config.cornerRadiusDp.dp
    val shapes = Shapes(
        extraSmall = RoundedCornerShape(radius * 0.4f),
        small = RoundedCornerShape(radius * 0.6f),
        medium = RoundedCornerShape(radius),
        large = RoundedCornerShape(radius * 1.3f),
        extraLarge = RoundedCornerShape(radius * 1.8f),
    )
    val spacing = if (config.density == UiDensity.COMPACT) CompactSpacing else ComfortableSpacing

    CompositionLocalProvider(LocalFxSpacing provides spacing) {
        MaterialTheme(colorScheme = colors, shapes = shapes, typography = FxTypography, content = content)
    }
}
