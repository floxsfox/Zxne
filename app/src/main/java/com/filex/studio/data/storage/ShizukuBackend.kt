package com.filex.studio.data.storage

import com.filex.studio.core.FxError
import com.filex.studio.core.FxResult
import com.filex.studio.data.shizuku.ShizukuController
import com.filex.studio.data.shizuku.ShizukuPermission
import com.filex.studio.data.shizuku.ShizukuState

/**
 * Placeholder for privileged access (Android/data, Android/obb). Connection and permission
 * are real (see ShizukuController); the file operations need a Shizuku UserService and are
 * not implemented yet, so [supportedOps] is empty and nothing pretends to work.
 */
class ShizukuBackend(private val controller: ShizukuController) : StorageBackend {

    override val type = BackendType.SHIZUKU
    override val supportedOps: Set<FileOp> = emptySet()

    override fun availability(): BackendAvailability = when (val s = controller.state.value) {
        is ShizukuState.NotRunning -> BackendAvailability.Unavailable(
            if (s.managerInstalled) {
                "Shizuku is installed but not running. Start it from the Shizuku app."
            } else {
                "Shizuku app is not installed."
            },
        )
        is ShizukuState.Ready ->
            if (s.permission == ShizukuPermission.GRANTED) {
                BackendAvailability.Unavailable(
                    "Connected and authorized. File operations through a Shizuku UserService are not implemented yet.",
                )
            } else {
                BackendAvailability.Unavailable("Shizuku is running but permission is not granted.")
            }
    }

    override suspend fun list(dir: FxLocation, showHidden: Boolean): FxResult<List<FileEntry>> =
        FxResult.Failure(FxError.Unsupported("Shizuku file access is not implemented yet"))
}
