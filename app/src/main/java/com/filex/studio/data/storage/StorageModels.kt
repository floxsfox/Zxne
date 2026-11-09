package com.filex.studio.data.storage

import android.webkit.MimeTypeMap

enum class BackendType { LOCAL, SAF, MEDIA_STORE, SHIZUKU }

enum class FileOp { LIST, CREATE_DIRECTORY, RENAME, DELETE, COPY, MOVE }

/**
 * Backend-neutral address. [value] is a filesystem path for LOCAL/SHIZUKU,
 * a document Uri string for SAF, and a category id or content Uri for MEDIA_STORE.
 */
data class FxLocation(val backend: BackendType, val value: String)

data class FileEntry(
    val location: FxLocation,
    val name: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val lastModifiedMillis: Long,
    val mimeType: String?,
    val isHidden: Boolean,
)

sealed interface BackendAvailability {
    data object Available : BackendAvailability
    data class Unavailable(val reason: String) : BackendAvailability
}

enum class SortField { NAME, SIZE, DATE, TYPE }

data class SortSpec(
    val field: SortField = SortField.NAME,
    val ascending: Boolean = true,
    val foldersFirst: Boolean = true,
) {
    fun comparator(): Comparator<FileEntry> {
        val byField: Comparator<FileEntry> = when (field) {
            SortField.NAME -> compareBy<FileEntry, String>(String.CASE_INSENSITIVE_ORDER) { it.name }
            SortField.SIZE -> compareBy<FileEntry> { it.sizeBytes }
            SortField.DATE -> compareBy<FileEntry> { it.lastModifiedMillis }
            SortField.TYPE -> compareBy<FileEntry, String>(String.CASE_INSENSITIVE_ORDER) {
                it.name.substringAfterLast('.', "")
            }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        }
        val directed = if (ascending) byField else byField.reversed()
        return if (foldersFirst) {
            compareBy<FileEntry> { !it.isDirectory }.then(directed)
        } else {
            directed
        }
    }
}

internal fun mimeTypeForName(name: String): String? {
    val ext = name.substringAfterLast('.', "").lowercase()
    if (ext.isEmpty()) return null
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
}
