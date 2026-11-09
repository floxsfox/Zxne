package com.filex.studio.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ThemeMode { SYSTEM, DARK, LIGHT }

enum class UiDensity { COMPACT, COMFORTABLE }

/** All user-adjustable appearance settings. Null colors mean "use the preset palette". */
data class FxThemeConfig(
    val mode: ThemeMode = ThemeMode.DARK,
    val accentArgb: Long = 0xFF8B5CF6,
    val backgroundArgb: Long? = null,
    val surfaceArgb: Long? = null,
    val textArgb: Long? = null,
    val cornerRadiusDp: Int = 14,
    val density: UiDensity = UiDensity.COMFORTABLE,
)

/** Persistence, saved themes and import/export arrive in task B27; this one keeps state in memory. */
interface ThemeRepository {
    val config: StateFlow<FxThemeConfig>
    fun update(transform: (FxThemeConfig) -> FxThemeConfig)
}

class InMemoryThemeRepository : ThemeRepository {
    private val _config = MutableStateFlow(FxThemeConfig())
    override val config: StateFlow<FxThemeConfig> = _config.asStateFlow()

    override fun update(transform: (FxThemeConfig) -> FxThemeConfig) {
        _config.update(transform)
    }
}
