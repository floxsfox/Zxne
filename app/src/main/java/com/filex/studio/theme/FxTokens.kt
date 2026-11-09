package com.filex.studio.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Central design tokens. Screens read colors from MaterialTheme and spacing from LocalFxSpacing. */
data class FxSurfaceColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val outline: Color,
)

object FxPalette {
    val dark = FxSurfaceColors(
        background = Color(0xFF101114),
        surface = Color(0xFF191B20),
        surfaceVariant = Color(0xFF22252B),
        onSurface = Color(0xFFECEDF1),
        onSurfaceMuted = Color(0xFF9AA0AD),
        outline = Color(0xFF2D3139),
    )
    val light = FxSurfaceColors(
        background = Color(0xFFF6F6F8),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFEEEFF3),
        onSurface = Color(0xFF15161A),
        onSurfaceMuted = Color(0xFF5F6472),
        outline = Color(0xFFD8DAE0),
    )
    val accent = Color(0xFF8B5CF6)
    val error = Color(0xFFFF6B6B)
}

data class FxSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
)

val ComfortableSpacing = FxSpacing()
val CompactSpacing = FxSpacing(xs = 3.dp, sm = 6.dp, md = 12.dp, lg = 18.dp)

val LocalFxSpacing = staticCompositionLocalOf { ComfortableSpacing }
