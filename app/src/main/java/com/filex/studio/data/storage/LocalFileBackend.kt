package com.filex.studio.data.storage

import com.filex.studio.core.FxError
import com.filex.studio.core.FxResult
import com.filex.studio.core.fxRunCatching
import com.filex.studio.core.validateFileName
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Direct java.io access. Needs all-files access (API 30+) or legacy storage permissions. */
class LocalFileBackend(
    private val io: CoroutineDispatcher = Dispatchers.IO,
) : StorageBackend {

    override val type = BackendType.LOCAL
    override val supportedOps = setOf(
        FileOp.LIST, FileOp.CREATE_DIRECTORY, FileOp.RENAME, FileOp.DELETE,
    )

    override fun availability(): BackendAvailability = BackendAvailability.Available

    override suspend fun list(dir: FxLocation, showHidden: Boolean): FxResult<List<FileEntry>> =
        withContext(io) {
            val folder = File(dir.value)
            when {
                !folder.exists() ->
                    FxResult.Failure(FxError.NotFound("Folder does not exist: ${dir.value}"))
                !folder.isDirectory ->
                    FxResult.Failure(FxError.Io("Not a folder: ${dir.value}"))
                else -> {
                    val children = folder.listFiles()
                    if (children == null) {
                        FxResult.Failure(FxError.PermissionDenied("Cannot read folder: ${dir.value}"))
                    } else {
                        FxResult.Success(
                            children
                                .filter { showHidden || !it.name.startsWith(".") }
                                .map { it.toEntry() },
                        )
                    }
                }
            }
        }

    override suspend fun createDirectory(parent: FxLocation, name: String): FxResult<FxLocation> =
        withContext(io) {
            validateFileName(name)?.let { return@withContext FxResult.Failure(it) }
            val dir = File(parent.value, name)
            when {
                dir.exists() -> FxResult.Failure(FxError.AlreadyExists("\"$name\" already exists"))
                dir.mkdir() -> FxResult.Success(FxLocation(BackendType.LOCAL, dir.absolutePath))
                else -> FxResult.Failure(
                    FxError.Io("Could not create folder \"$name\" (permission or storage problem)"),
                )
            }
        }

    override suspend fun rename(target: FxLocation, newName: String): FxResult<FxLocation> =
        withContext(io) {
            validateFileName(newName)?.let { return@withContext FxResult.Failure(it) }
            val src = File(target.value)
            val parentDir = src.parentFile
            if (!src.exists()) {
                return@withContext FxResult.Failure(FxError.NotFound("Not found: ${target.value}"))
            }
            if (parentDir == null) {
                return@withContext FxResult.Failure(FxError.Unsupported("Cannot rename a storage root"))
            }
            val dst = File(parentDir, newName)
            if (dst.absolutePath == src.absolutePath) {
                return@withContext FxResult.Success(target)
            }
            // The emulated storage is case-insensitive: allow case-only renames of the same file.
            val sameFile = dst.exists() && dst.canonicalPath == src.canonicalPath
            when {
                dst.exists() && !sameFile ->
                    FxResult.Failure(FxError.AlreadyExists("\"$newName\" already exists"))
                src.renameTo(dst) ->
                    FxResult.Success(FxLocation(BackendType.LOCAL, dst.absolutePath))
                else -> FxResult.Failure(FxError.Io("Rename was rejected by the system"))
            }
        }

    override suspend fun delete(target: FxLocation): FxResult<Unit> = withContext(io) {
        val path = File(target.value).toPath()
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            return@withContext FxResult.Failure(FxError.NotFound("Not found: ${target.value}"))
        }
        if (File(target.value).parentFile == null) {
            return@withContext FxResult.Failure(FxError.Unsupported("Cannot delete a storage root"))
        }
        fxRunCatching { deleteTree(path) }
    }

    /** Walks without following symlinks, so a link is removed but its target is never touched. */
    private fun deleteTree(root: Path) {
        Files.walkFileTree(
            root,
            object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    Files.delete(file)
                    return FileVisitResult.CONTINUE
                }

                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                    if (exc != null) throw exc
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }
            },
        )
    }

    private fun File.toEntry() = FileEntry(
        location = FxLocation(BackendType.LOCAL, absolutePath),
        name = name,
        isDirectory = isDirectory,
        sizeBytes = if (isDirectory) 0L else length(),
        lastModifiedMillis = lastModified(),
        mimeType = if (isDirectory) null else mimeTypeForName(name),
        isHidden = name.startsWith("."),
    )
}
