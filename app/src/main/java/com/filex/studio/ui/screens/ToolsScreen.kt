package com.filex.studio.ui.screens

import androidx.compose.runtime.Composable
import com.filex.studio.AppContainer
import com.filex.studio.ui.components.FxCard
import com.filex.studio.ui.components.FxMutedText
import com.filex.studio.ui.components.FxScreen
import com.filex.studio.ui.components.FxSectionTitle
import com.filex.studio.ui.components.FxStatusRow

private val plannedTools = listOf(
    "Dual-pane manager" to "B13",
    "Archive manager" to "B14-B15",
    "App manager and APK inspector" to "B16-B17",
    "Text editor" to "B18",
    "Hex viewer/editor" to "B19",
    "Native drag and drop" to "B20",
    "Quick Drag floating bubble" to "B21-B23",
    "Hidden files and authentication" to "B24-B26",
    "Storage analyzer" to "B31",
)

/** Phase A: an honest map of what exists. Nothing here launches a feature yet. */
@Composable
fun ToolsScreen(container: AppContainer) {
    FxScreen("Tools") {
        FxCard {
            FxSectionTitle("Planned tools (not implemented yet)")
            plannedTools.forEach { (name, tasks) -> FxStatusRow(name, "Task $tasks") }
        }
        FxCard {
            FxSectionTitle("Cloud providers")
            container.cloudProviders.forEach { provider ->
                FxStatusRow(provider.displayName, "Not implemented", ok = false)
                FxMutedText(provider.statusNote)
            }
        }
    }
}
