package com.filex.studio.feature.dragdrop

import android.content.Intent
import android.net.Uri
import com.filex.studio.core.FxResult
import com.filex.studio.data.storage.FileEntry
import com.filex.studio.data.storage.FxLocation

/** Contracts only (task A14). Implementations arrive in tasks B20 and B23. */
enum class DropAction { COPY, MOVE }

enum class DragOrigin { IN_APP_PANE, FLOATING_WINDOW }

data class DragPayload(val sources: List<FxLocation>, val origin: DragOrigin)

/** In-app drag and drop between folders and panes. */
interface DragDropCoordinator {
    fun begin(payload: DragPayload)
    fun cancel()
    suspend fun drop(target: FxLocation, action: DropAction): FxResult<Unit>
}

/**
 * Cross-app sharing. Must hand out content Uris with temporary read grants and the right
 * MIME type; if a receiving app rejects the drag, the Sharesheet intent is the fallback.
 */
interface ExternalShareBridge {
    fun contentUriFor(entry: FileEntry): Uri?
    fun shareIntent(entries: List<FileEntry>): Intent?
}
