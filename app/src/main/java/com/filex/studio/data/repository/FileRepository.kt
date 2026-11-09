package com.filex.studio.data.repository

import com.filex.studio.core.FxError
import com.filex.studio.core.FxResult
import com.filex.studio.data.storage.BackendAvailability
import com.filex.studio.data.storage.FileEntry
import com.filex.studio.data.storage.FileOp
import com.filex.studio.data.storage.FxLocation
import com.filex.studio.data.storage.SortSpec
import com.filex.studio.data.storage.StorageBackend
import com.filex.studio.data.storage.StorageManager

interface FileRepository {
    suspend fun list(dir: FxLocation, showHidden: Boolean, sort: SortSpec = SortSpec()): FxResult<List<FileEntry>>
    suspend fun createDirectory(parent: FxLocation, name: String): FxResult<FxLocation>
    suspend fun rename(target: FxLocation, newName: String): FxResult<FxLocation>
    suspend fun delete(target: FxLocation): FxResult<Unit>
}

class DefaultFileRepository(private val storage: StorageManager) : FileRepository {

    override suspend fun list(dir: FxLocation, showHidden: Boolean, sort: SortSpec): FxResult<List<FileEntry>> =
        when (val r = withBackend(dir, FileOp.LIST) { it.list(dir, showHidden) }) {
            is FxResult.Success -> FxResult.Success(r.value.sortedWith(sort.comparator()))
            is FxResult.Failure -> r
        }

    override suspend fun createDirectory(parent: FxLocation, name: String): FxResult<FxLocation> =
        withBackend(parent, FileOp.CREATE_DIRECTORY) { it.createDirectory(parent, name) }

    override suspend fun rename(target: FxLocation, newName: String): FxResult<FxLocation> =
        withBackend(target, FileOp.RENAME) { it.rename(target, newName) }

    override suspend fun delete(target: FxLocation): FxResult<Unit> =
        withBackend(target, FileOp.DELETE) { it.delete(target) }

    /** Refuses early (with a clear reason) when the backend cannot do [op] or is not usable. */
    private inline fun <T> withBackend(
        location: FxLocation,
        op: FileOp,
        block: (StorageBackend) -> FxResult<T>,
    ): FxResult<T> {
        val backend = storage.backendFor(location)
        if (op !in backend.supportedOps) {
            return FxResult.Failure(FxError.Unsupported("${backend.type} backend does not support $op yet"))
        }
        val availability = backend.availability()
        if (availability is BackendAvailability.Unavailable) {
            return FxResult.Failure(FxError.BackendUnavailable(availability.reason))
        }
        return block(backend)
    }
}
