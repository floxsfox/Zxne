package com.filex.studio

import android.app.Application
import com.filex.studio.data.cloud.CloudProvider
import com.filex.studio.data.cloud.DropboxProvider
import com.filex.studio.data.cloud.MediaFireProvider
import com.filex.studio.data.cloud.TeraBoxProvider
import com.filex.studio.data.permission.PermissionManager
import com.filex.studio.data.repository.DefaultFileRepository
import com.filex.studio.data.repository.FileRepository
import com.filex.studio.data.shizuku.ShizukuController
import com.filex.studio.data.storage.LocalFileBackend
import com.filex.studio.data.storage.MediaStoreBackend
import com.filex.studio.data.storage.SafBackend
import com.filex.studio.data.storage.ShizukuBackend
import com.filex.studio.data.storage.StorageBackend
import com.filex.studio.data.storage.StorageManager
import com.filex.studio.theme.InMemoryThemeRepository
import com.filex.studio.theme.ThemeRepository

/** Manual dependency container (no DI framework). Created once in [FileXApp]. */
class AppContainer(application: Application) {
    val permissionManager = PermissionManager(application)
    val shizukuController = ShizukuController(application)
    val safBackend = SafBackend(application)

    val storageManager = StorageManager(
        backends = listOf<StorageBackend>(
            LocalFileBackend(),
            safBackend,
            MediaStoreBackend(application),
            ShizukuBackend(shizukuController),
        ).associateBy { it.type },
        shizuku = shizukuController,
    )

    val fileRepository: FileRepository = DefaultFileRepository(storageManager)
    val themeRepository: ThemeRepository = InMemoryThemeRepository()
    val cloudProviders: List<CloudProvider> = listOf(DropboxProvider(), MediaFireProvider(), TeraBoxProvider())
}
