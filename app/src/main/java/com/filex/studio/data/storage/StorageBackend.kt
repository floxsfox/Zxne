package com.filex.studio.data.storage

import com.filex.studio.core.FxError
import com.filex.studio.core.FxResult

/**
 * One way of reaching files. Operations a backend does not implement keep the
 * default below and report Unsupported, so callers never see a fake success.
 */
interface StorageBackend {
    val type: BackendType
    val supportedOps: Set<FileOp>
    fun availability(): BackendAvailability

    suspend fun list(dir: FxLocation, showHidden: Boolean): FxResult<List<FileEntry>>

    suspend fun createDirectory(parent: FxLocation, name: String): FxResult<FxLocation> =
        unsupportedOp(type, FileOp.CREATE_DIRECTORY)

    suspend fun rename(target: FxLocation, newName: String): FxResult<FxLocation> =
        unsupportedOp(type, FileOp.RENAME)

    suspend fun delete(target: FxLocation): FxResult<Unit> =
        unsupportedOp(type, FileOp.DELETE)

    suspend fun copy(source: FxLocation, destDir: FxLocation): FxResult<FxLocation> =
        unsupportedOp(type, FileOp.COPY)

    suspend fun move(source: FxLocation, destDir: FxLocation): FxResult<FxLocation> =
        unsupportedOp(type, FileOp.MOVE)
}

fun <T> unsupportedOp(type: BackendType, op: FileOp): FxResult<T> =
    FxResult.Failure(FxError.Unsupported("$op is not implemented for the $type backend yet"))
