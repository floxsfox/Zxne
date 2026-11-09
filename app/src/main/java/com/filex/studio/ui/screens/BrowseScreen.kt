package com.filex.studio.ui.screens

import android.text.format.Formatter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filex.studio.AppContainer
import com.filex.studio.data.storage.FileEntry
import com.filex.studio.theme.LocalFxSpacing
import java.text.DateFormat
import java.util.Date

@Composable
fun BrowseScreen(
    container: AppContainer,
    vm: BrowseViewModel = viewModel(factory = BrowseViewModel.Factory),
) {
    val sp = LocalFxSpacing.current
    val perms by container.permissionManager.state.collectAsStateWithLifecycle()
    val ui by vm.state.collectAsStateWithLifecycle()
    val hasAccess = perms.allFilesAccess

    LaunchedEffect(hasAccess) {
        if (hasAccess && vm.state.value.currentDir == null) vm.openRoot()
    }
    BackHandler(enabled = ui.canGoUp) { vm.up() }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = sp.md, vertical = sp.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = ui.currentDir?.value ?: "Browse",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = vm::up, enabled = ui.canGoUp) {
                Icon(Icons.Rounded.ArrowUpward, contentDescription = "Up")
            }
            IconButton(onClick = vm::toggleHidden) {
                Icon(
                    if (ui.showHidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                    contentDescription = "Toggle hidden files",
                )
            }
            IconButton(onClick = vm::refresh, enabled = ui.currentDir != null) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
            }
        }
        if (ui.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        when {
            !hasAccess -> Message("All-files access is not granted. Open the Storage tab to grant it.")
            ui.error != null -> Message(ui.error ?: "")
            ui.currentDir != null && ui.entries.isEmpty() && !ui.loading -> Message("This folder is empty.")
            else -> LazyColumn(Modifier.fillMaxSize()) {
                items(ui.entries, key = { it.location.value }) { entry ->
                    EntryRow(entry, onOpen = { vm.open(entry.location) })
                }
            }
        }
    }
}

@Composable
private fun Message(text: String) {
    val sp = LocalFxSpacing.current
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(sp.md),
    )
}

@Composable
private fun EntryRow(entry: FileEntry, onOpen: () -> Unit) {
    val context = LocalContext.current
    val dateFormat = remember { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) }
    val date = dateFormat.format(Date(entry.lastModifiedMillis))
    val subtitle = if (entry.isDirectory) {
        "Folder · $date"
    } else {
        "${Formatter.formatShortFileSize(context, entry.sizeBytes)} · $date"
    }
    ListItem(
        headlineContent = { Text(entry.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = if (entry.isDirectory) Icons.Rounded.Folder else Icons.Rounded.Description,
                contentDescription = null,
                tint = if (entry.isDirectory) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(enabled = entry.isDirectory, onClick = onOpen),
    )
}
