package com.filex.studio.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.filex.studio.FileXApp
import com.filex.studio.core.FxResult
import com.filex.studio.data.repository.FileRepository
import com.filex.studio.data.storage.FileEntry
import com.filex.studio.data.storage.FxLocation
import com.filex.studio.data.storage.StorageManager
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrowseUiState(
    val root: FxLocation? = null,
    val currentDir: FxLocation? = null,
    val entries: List<FileEntry> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val showHidden: Boolean = false,
) {
    val canGoUp: Boolean
        get() = currentDir != null && root != null && currentDir != root
}

/** Minimal vertical slice proving the repository -> backend pipeline (full explorer is task B02). */
class BrowseViewModel(
    private val files: FileRepository,
    private val storage: StorageManager,
) : ViewModel() {

    private val _state = MutableStateFlow(BrowseUiState())
    val state: StateFlow<BrowseUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun openRoot() {
        val root = storage.primaryRoot().location
        _state.update { it.copy(root = root) }
        open(root)
    }

    fun open(dir: FxLocation) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            when (val result = files.list(dir, _state.value.showHidden)) {
                is FxResult.Success ->
                    _state.update { it.copy(currentDir = dir, entries = result.value, loading = false) }
                is FxResult.Failure ->
                    _state.update { it.copy(loading = false, error = result.error.message) }
            }
        }
    }

    /** Only meaningful for LOCAL locations (path based), which is all Phase A browses. */
    fun up() {
        val current = _state.value.currentDir ?: return
        if (!_state.value.canGoUp) return
        val parent = File(current.value).parentFile ?: return
        open(current.copy(value = parent.absolutePath))
    }

    fun refresh() {
        _state.value.currentDir?.let { open(it) }
    }

    fun toggleHidden() {
        _state.update { it.copy(showHidden = !it.showHidden) }
        refresh()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as FileXApp
                BrowseViewModel(app.container.fileRepository, app.container.storageManager)
            }
        }
    }
}
