package com.filex.studio.data.storage

import android.os.Build
import android.os.Environment
import com.filex.studio.data.shizuku.ShizukuController
import com.filex.studio.data.shizuku.ShizukuPermission
import com.filex.studio.data.shizuku.ShizukuState

data class StorageRoot(val label: String, val location: FxLocation)

data class BackendChoice(val type: BackendType, val reason: String)

/** Owns the backends and decides which one should serve a given path. */
class StorageManager(
    private val backends: Map<BackendType, StorageBackend>,
    private val shizuku: ShizukuController,
) {
    fun backend(type: BackendType): StorageBackend = backends.getValue(type)

    fun backendFor(location: FxLocation): StorageBackend = backend(location.backend)

    fun allBackends(): List<StorageBackend> = backends.values.toList()

    fun primaryRoot(): StorageRoot = StorageRoot(
        label = "Internal storage",
        location = FxLocation(BackendType.LOCAL, Environment.getExternalStorageDirectory().absolutePath),
    )

    /**
     * Routing rule. Android/data and Android/obb are not reliably reachable by direct file
     * access on API 30+, so they prefer Shizuku and fall back to SAF. Everything else uses
     * direct access when all-files access is granted, otherwise SAF.
     */
    fun chooseBackendForPath(absolutePath: String, hasAllFilesAccess: Boolean): BackendChoice {
        val restricted = Regex("/Android/(data|obb)(/|$)").containsMatchIn(absolutePath)
        if (restricted) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return BackendChoice(BackendType.LOCAL, "Direct access is allowed before Android 11")
            }
            val ready = (shizuku.state.value as? ShizukuState.Ready)?.permission == ShizukuPermission.GRANTED
            return if (ready) {
                BackendChoice(BackendType.SHIZUKU, "Shizuku is authorized")
            } else {
                BackendChoice(BackendType.SAF, "Needs Shizuku; SAF access may be limited by Android")
            }
        }
        return if (hasAllFilesAccess) {
            BackendChoice(BackendType.LOCAL, "All-files access is granted")
        } else {
            BackendChoice(BackendType.SAF, "All-files access is not granted")
        }
    }
}
