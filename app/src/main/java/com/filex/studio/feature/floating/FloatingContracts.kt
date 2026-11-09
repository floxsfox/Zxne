package com.filex.studio.feature.floating

import kotlinx.coroutines.flow.StateFlow

/** Contracts only (task A15). The overlay service and window arrive in tasks B21 and B22. */
sealed interface FloatingUiState {
    data object Hidden : FloatingUiState
    data object Bubble : FloatingUiState
    data object Expanded : FloatingUiState
}

data class FloatingWindowConfig(
    val bubbleSizeDp: Int = 56,
    val windowWidthDp: Int = 320,
    val windowHeightDp: Int = 420,
)

interface FloatingWindowController {
    val state: StateFlow<FloatingUiState>

    /** True when the overlay permission is granted. */
    fun canShow(): Boolean

    fun showBubble()
    fun expand()
    fun minimize()
    fun close()
}
