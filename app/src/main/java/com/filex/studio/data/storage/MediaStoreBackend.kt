package com.filex.studio.data.storage

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.filex.studio.core.FxError
import com.filex.studio.core.FxResult
import com.filex.studio.core.fxTry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class MediaCategory(val id: String, val label: String, val collection: Uri) {
    IMAGES("images", "Pictures", MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
    VIDEO("video", "Videos", MediaStore.Video.Media.EXTERNAL_CONTENT_URI),
    AUDIO("audio", "Music", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

    fun location() = FxLocation(BackendType.MEDIA_STORE, id)

    companion object {
        fun fromId(id: String): MediaCategory? = MediaCategory.entries.firstOrNull { it.id == id }
    }
}

/** Read-only listing of media collections. Documents/Downloads categories are planned (B-phase). */
class MediaStoreBackend(
    private val context: Context,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) : StorageBackend {

    override val type = BackendType.MEDIA_STORE
    override val supportedOps = setOf(FileOp.LIST)

    override fun availability(): BackendAvailability = BackendAvailability.Available

    override suspend fun list(dir: FxLocation, showHidden: Boolean): FxResult<List<FileEntry>> =
        withContext(io) {
            val category = MediaCategory.fromId(dir.value)
                ?: return@withContext FxResult.Failure(
                    FxError.NotFound("Unknown media category: ${dir.value}"),
                )
            fxTry { FxResult.Success(query(category)) }
        }

    private fun query(category: MediaCategory): List<FileEntry> {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.MIME_TYPE,
        )
        val sort = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"
        val out = ArrayList<FileEntry>()
        context.contentResolver.query(category.collection, projection, null, null, sort)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameCol = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val sizeCol = c.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
            val mimeCol = c.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            while (c.moveToNext() && out.size < MAX_ROWS) {
                val uri = ContentUris.withAppendedId(category.collection, c.getLong(idCol))
                val name = c.getString(nameCol) ?: "?"
                out += FileEntry(
                    location = FxLocation(BackendType.MEDIA_STORE, uri.toString()),
                    name = name,
                    isDirectory = false,
                    sizeBytes = c.getLong(sizeCol),
                    lastModifiedMillis = c.getLong(dateCol) * 1000L,
                    mimeType = c.getString(mimeCol),
                    isHidden = name.startsWith("."),
                )
            }
        }
        return out
    }

    private companion object {
        const val MAX_ROWS = 5000
    }
}
