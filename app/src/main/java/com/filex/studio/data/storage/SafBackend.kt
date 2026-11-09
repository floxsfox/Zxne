package com.filex.studio.data.storage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.filex.studio.core.FxError
import com.filex.studio.core.FxResult
import com.filex.studio.core.fxTry
import com.filex.studio.core.validateFileName
import java.io.FileNotFoundException
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Storage Access Framework backend. Works only inside folders the user granted via the system picker. */
class SafBackend(
    private val context: Context,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) : StorageBackend {

    override val type = BackendType.SAF
    override val supportedOps = setOf(
        FileOp.LIST, FileOp.CREATE_DIRECTORY, FileOp.RENAME, FileOp.DELETE,
    )

    fun persistedTrees(): List<FxLocation> =
        context.contentResolver.persistedUriPermissions
            .filter { it.isReadPermission && DocumentsContract.isTreeUri(it.uri) }
            .map { FxLocation(BackendType.SAF, it.uri.toString()) }

    /** Call with the Uri returned by ACTION_OPEN_DOCUMENT_TREE. */
    fun persistGrant(treeUri: Uri) {
        context.contentResolver.takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
        )
    }

    override fun availability(): BackendAvailability =
        if (persistedTrees().isEmpty()) {
            BackendAvailability.Unavailable("No folder granted through the system picker yet")
        } else {
            BackendAvailability.Available
        }

    private fun documentFor(location: FxLocation): DocumentFile =
        DocumentFile.fromTreeUri(context, Uri.parse(location.value))
            ?: throw FileNotFoundException("Cannot open ${location.value}")

    override suspend fun list(dir: FxLocation, showHidden: Boolean): FxResult<List<FileEntry>> =
        withContext(io) {
            fxTry {
                val doc = documentFor(dir)
                if (!doc.isDirectory) throw IOException("Not a folder")
                val entries = doc.listFiles()
                    .filter { showHidden || it.name?.startsWith(".") != true }
                    .map { it.toEntry() }
                FxResult.Success(entries)
            }
        }

    override suspend fun createDirectory(parent: FxLocation, name: String): FxResult<FxLocation> =
        withContext(io) {
            validateFileName(name)?.let { return@withContext FxResult.Failure(it) }
            fxTry {
                val parentDoc = documentFor(parent)
                if (parentDoc.findFile(name) != null) {
                    FxResult.Failure(FxError.AlreadyExists("\"$name\" already exists"))
                } else {
                    val created = parentDoc.createDirectory(name)
                    if (created == null) {
                        FxResult.Failure(FxError.Io("The provider refused to create \"$name\""))
                    } else {
                        FxResult.Success(FxLocation(BackendType.SAF, created.uri.toString()))
                    }
                }
            }
        }

    override suspend fun rename(target: FxLocation, newName: String): FxResult<FxLocation> =
        withContext(io) {
            validateFileName(newName)?.let { return@withContext FxResult.Failure(it) }
            fxTry {
                val doc = documentFor(target)
                if (doc.renameTo(newName)) {
                    FxResult.Success(FxLocation(BackendType.SAF, doc.uri.toString()))
                } else {
                    FxResult.Failure(FxError.Io("The provider refused to rename to \"$newName\""))
                }
            }
        }

    override suspend fun delete(target: FxLocation): FxResult<Unit> = withContext(io) {
        fxTry {
            val doc = documentFor(target)
            if (doc.delete()) {
                FxResult.Success(Unit)
            } else {
                FxResult.Failure(FxError.Io("The provider refused to delete this item"))
            }
        }
    }

    private fun DocumentFile.toEntry() = FileEntry(
        location = FxLocation(BackendType.SAF, uri.toString()),
        name = name ?: "?",
        isDirectory = isDirectory,
        sizeBytes = if (isDirectory) 0L else length(),
        lastModifiedMillis = lastModified(),
        mimeType = type,
        isHidden = name?.startsWith(".") == true,
    )
}
