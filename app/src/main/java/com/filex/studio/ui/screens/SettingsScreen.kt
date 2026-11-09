package com.filex.studio.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filex.studio.AppContainer
import com.filex.studio.theme.ThemeMode
import com.filex.studio.theme.UiDensity
import com.filex.studio.ui.components.FxCard
import com.filex.studio.ui.components.FxMutedText
import com.filex.studio.ui.components.FxScreen
import com.filex.studio.ui.components.FxSectionTitle
import kotlin.math.roundToInt

private val accentPresets = listOf(
    "Violet" to 0xFF8B5CF6,
    "Blue" to 0xFF3B82F6,
    "Teal" to 0xFF14B8A6,
    "Amber" to 0xFFF59E0B,
)

private fun label(name: String): String = name.lowercase().replaceFirstChar { it.uppercase() }

@Composable
fun SettingsScreen(container: AppContainer) {
    val repo = container.themeRepository
    val config by repo.config.collectAsStateWithLifecycle()
    val chipSpacing = Arrangement.spacedBy(8.dp)

    FxScreen("Settings") {
        FxCard {
            FxSectionTitle("Theme mode")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = chipSpacing) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = config.mode == mode,
                        onClick = { repo.update { it.copy(mode = mode) } },
                        label = { Text(label(mode.name)) },
                    )
                }
            }
        }
        FxCard {
            FxSectionTitle("Accent color")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = chipSpacing) {
                accentPresets.forEach { (name, argb) ->
                    FilterChip(
                        selected = config.accentArgb == argb,
                        onClick = { repo.update { it.copy(accentArgb = argb) } },
                        label = { Text(name) },
                    )
                }
            }
        }
        FxCard {
            FxSectionTitle("Density")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = chipSpacing) {
                UiDensity.entries.forEach { density ->
                    FilterChip(
                        selected = config.density == density,
                        onClick = { repo.update { it.copy(density = density) } },
                        label = { Text(label(density.name)) },
                    )
                }
            }
        }
        FxCard {
            FxSectionTitle("Corner radius: ${config.cornerRadiusDp} dp")
            Slider(
                value = config.cornerRadiusDp.toFloat(),
                onValueChange = { v -> repo.update { it.copy(cornerRadiusDp = v.roundToInt()) } },
                valueRange = 4f..28f,
            )
        }
        FxCard {
            FxMutedText(
                "Phase A: settings apply immediately but are not saved between launches. " +
                    "Saved themes, custom colors and import/export arrive in task B27.",
            )
        }
    }
}
