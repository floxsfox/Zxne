package com.filex.studio.ui.screens

import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filex.studio.AppContainer
import com.filex.studio.ui.components.FxCard
import com.filex.studio.ui.components.FxMutedText
import com.filex.studio.ui.components.FxScreen
import com.filex.studio.ui.components.FxSectionTitle
import com.filex.studio.ui.components.FxStatusRow
import com.filex.studio.ui.components.isAuthorized
import com.filex.studio.ui.components.summary

private fun internalStorageSpace(): Pair<Long, Long> = try {
    val stat = StatFs(Environment.getExternalStorageDirectory().path)
    stat.totalBytes to stat.availableBytes
} catch (e: Exception) {
    0L to 0L
}

@Composable
fun HomeScreen(container: AppContainer) {
    val context = LocalContext.current
    val perms by container.permissionManager.state.collectAsStateWithLifecycle()
    val shizuku by container.shizukuController.state.collectAsStateWithLifecycle()
    val space = remember(perms) { internalStorageSpace() }
    val total = space.first
    val free = space.second
    val used = (total - free).coerceAtLeast(0L)

    FxScreen("FileX Studio") {
        FxCard {
            FxSectionTitle("Internal storage")
            LinearProgressIndicator(
                progress = { if (total > 0) used.toFloat() / total else 0f },
                modifier = Modifier.fillMaxWidth(),
            )
            FxMutedText(
                "${Formatter.formatFileSize(context, used)} used of " +
                    "${Formatter.formatFileSize(context, total)} · " +
                    "${Formatter.formatFileSize(context, free)} free",
            )
        }
        FxCard {
            FxSectionTitle("Access status")
            FxStatusRow(
                "All-files access",
                if (perms.allFilesAccess) "Granted" else "Not granted",
                perms.allFilesAccess,
            )
            FxStatusRow(
                "Media access",
                if (perms.mediaAccess) "Granted" else "Not granted",
                perms.mediaAccess,
            )
            FxStatusRow("Shizuku", shizuku.summary(), shizuku.isAuthorized())
        }
        FxCard {
            FxMutedText(
                "Phase A skeleton: this page shows live status only. " +
                    "Recent files and favorites arrive in tasks B11 and B12.",
            )
        }
    }
}
