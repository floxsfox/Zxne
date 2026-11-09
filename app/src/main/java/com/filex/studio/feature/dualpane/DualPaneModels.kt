package com.filex.studio.feature.dualpane

import com.filex.studio.data.storage.FxLocation
import com.filex.studio.data.storage.SortSpec

/** Architecture only (task A13). The screen and operations arrive in task B13. */
enum class PaneId { LEFT, RIGHT }

data class PaneState(
    val id: PaneId,
    val dir: FxLocation? = null,
    val selection: Set<FxLocation> = emptySet(),
    val sort: SortSpec = SortSpec(),
    val showHidden: Boolean = false,
)

data class DualPaneState(
    val left: PaneState = PaneState(PaneId.LEFT),
    val right: PaneState = PaneState(PaneId.RIGHT),
    val active: PaneId = PaneId.LEFT,
) {
    fun pane(id: PaneId): PaneState = if (id == PaneId.LEFT) left else right
    fun other(id: PaneId): PaneId = if (id == PaneId.LEFT) PaneId.RIGHT else PaneId.LEFT
}
