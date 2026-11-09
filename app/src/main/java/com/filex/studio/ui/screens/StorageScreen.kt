package com.filex.studio.ui.screens

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filex.studio.AppContainer
import com.filex.studio.data.shizuku.ShizukuPermission
import com.filex.studio.data.shizuku.ShizukuState
import com.filex.studio.data.storage.BackendAvailability
import com.filex.studio.ui.components.FxCard
import com.filex.studio.ui.components.FxMutedText
import com.filex.studio.ui.components.FxScreen
import com.filex.studio.ui.components.FxSectionTitle
import com.filex.studio.ui.components.FxStatusRow
import com.filex.studio.ui.components.isAuthorized
import com.filex.studio.ui.components.summary

@Composable
fun StorageScreen(container: AppContainer) {
    val context = LocalContext.current
    val pm = container.permissionManager
    val perms by pm.state.collectAsStateWithLifecycle()
    val shizuku by container.shizukuController.state.collectAsStateWithLifecycle()
    var safGrants by remember { mutableStateOf(container.safBackend.persistedTrees()) }
    var grantError by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { pm.refresh() }

    val treeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                container.safBackend.persistGrant(uri)
                grantError = null
            } catch (e: SecurityException) {
                grantError = e.message ?: "The provider did not allow a persistent grant"
            }
            safGrants = container.safBackend.persistedTrees()
        }
    }

    val backends = remember(perms, shizuku, safGrants) {
        container.storageManager.allBackends().map { it to it.availability() }
    }

    FxScreen("Storage") {
        FxCard {
            FxSectionTitle("Direct file access")
            FxStatusRow(
                "All-files access",
                if (perms.allFilesAccess) "Granted" else "Not granted",
                perms.allFilesAccess,
            )
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context.startActivity(pm.allFilesAccessIntent())
                    } else {
                        permissionLauncher.launch(pm.legacyStoragePermissions())
                    }
                },
                enabled = !perms.allFilesAccess,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Grant all-files access") }
        }

        FxCard {
            FxSectionTitle("Media access (MediaStore)")
            FxStatusRow(
                "Media permissions",
                if (perms.mediaAccess) "Granted" else "Not granted",
                perms.mediaAccess,
            )
            OutlinedButton(
                onClick = { permissionLauncher.launch(pm.mediaPermissions()) },
                enabled = !perms.mediaAccess,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Grant media access") }
        }

        FxCard {
            FxSectionTitle("Shizuku")
            FxStatusRow("Status", shizuku.summary(), shizuku.isAuthorized())
            val ready = shizuku as? ShizukuState.Ready
            if (ready?.permission == ShizukuPermission.DENIED_PERMANENTLY) {
                FxMutedText("Permission was denied permanently. Allow FileX Studio inside the Shizuku app.")
            }
            OutlinedButton(
                onClick = { container.shizukuController.requestPermission() },
                enabled = ready?.permission == ShizukuPermission.DENIED,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Request Shizuku permission") }
        }

        FxCard {
            FxSectionTitle("Granted folders (SAF)")
            if (safGrants.isEmpty()) {
                FxMutedText("No folder granted yet.")
            } else {
                safGrants.forEach { FxMutedText(Uri.parse(it.value).lastPathSegment ?: it.value) }
            }
            grantError?.let { FxMutedText("Grant failed: $it") }
            OutlinedButton(
                onClick = { treeLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Grant a folder") }
        }

        FxCard {
            FxSectionTitle("Backends")
            backends.forEach { (backend, availability) ->
                val ops = if (backend.supportedOps.isEmpty()) {
                    "no operations implemented"
                } else {
                    backend.supportedOps.joinToString { it.name.lowercase() }
                }
                FxStatusRow(
                    backend.type.name,
                    if (availability is BackendAvailability.Available) "Available" else "Unavailable",
                    availability is BackendAvailability.Available,
                )
                FxMutedText(ops)
                if (availability is BackendAvailability.Unavailable) FxMutedText(availability.reason)
            }
        }

        FxCard {
            FxSectionTitle("Backend routing")
            listOf(
                "/storage/emulated/0/Download",
                "/storage/emulated/0/Android/data",
            ).forEach { path ->
                val choice = container.storageManager.chooseBackendForPath(path, perms.allFilesAccess)
                FxStatusRow(path.removePrefix("/storage/emulated/0"), choice.type.name)
                FxMutedText(choice.reason)
            }
        }
    }
}
