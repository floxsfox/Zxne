package com.filex.studio.data.cloud

import android.net.Uri
import com.filex.studio.core.FxError
import com.filex.studio.core.FxResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CloudProviderId { DROPBOX, MEDIAFIRE, TERABOX }

sealed interface CloudConnection {
    data object Disconnected : CloudConnection
    data class Connected(val accountLabel: String) : CloudConnection
}

data class CloudEntry(
    val path: String,
    val name: String,
    val isFolder: Boolean,
    val sizeBytes: Long,
    val modifiedMillis: Long?,
)

data class CloudAccountInfo(val displayName: String, val usedBytes: Long?, val totalBytes: Long?)

data class TransferProgress(val bytesDone: Long, val bytesTotal: Long?)

/**
 * Contract for a cloud service. Implementations must use the provider's official API and
 * official authentication only. Cancel a transfer by cancelling the calling coroutine.
 */
interface CloudProvider {
    val id: CloudProviderId
    val displayName: String

    /** Honest description of what is and is not done for this provider. */
    val statusNote: String

    val connection: StateFlow<CloudConnection>

    suspend fun connect(): FxResult<Unit>
    suspend fun disconnect(): FxResult<Unit>
    suspend fun accountInfo(): FxResult<CloudAccountInfo>
    suspend fun listFolder(path: String): FxResult<List<CloudEntry>>
    suspend fun createFolder(parentPath: String, name: String): FxResult<CloudEntry>
    suspend fun rename(path: String, newName: String): FxResult<CloudEntry>
    suspend fun delete(path: String): FxResult<Unit>
    suspend fun upload(source: Uri, remoteDir: String, onProgress: (TransferProgress) -> Unit): FxResult<CloudEntry>
    suspend fun download(remotePath: String, destination: Uri, onProgress: (TransferProgress) -> Unit): FxResult<Unit>
}

/** Base for providers whose integration has not been written yet: every call reports Unsupported. */
abstract class UnimplementedCloudProvider : CloudProvider {
    private val _connection = MutableStateFlow<CloudConnection>(CloudConnection.Disconnected)
    override val connection: StateFlow<CloudConnection> = _connection.asStateFlow()

    private fun <T> notImplemented(): FxResult<T> =
        FxResult.Failure(FxError.Unsupported("$displayName integration is not implemented yet"))

    override suspend fun connect(): FxResult<Unit> = notImplemented()
    override suspend fun disconnect(): FxResult<Unit> = notImplemented()
    override suspend fun accountInfo(): FxResult<CloudAccountInfo> = notImplemented()
    override suspend fun listFolder(path: String): FxResult<List<CloudEntry>> = notImplemented()
    override suspend fun createFolder(parentPath: String, name: String): FxResult<CloudEntry> = notImplemented()
    override suspend fun rename(path: String, newName: String): FxResult<CloudEntry> = notImplemented()
    override suspend fun delete(path: String): FxResult<Unit> = notImplemented()
    override suspend fun upload(
        source: Uri,
        remoteDir: String,
        onProgress: (TransferProgress) -> Unit,
    ): FxResult<CloudEntry> = notImplemented()

    override suspend fun download(
        remotePath: String,
        destination: Uri,
        onProgress: (TransferProgress) -> Unit,
    ): FxResult<Unit> = notImplemented()
}
