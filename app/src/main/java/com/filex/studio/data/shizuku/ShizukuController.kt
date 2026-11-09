package com.filex.studio.data.shizuku

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku

enum class ShizukuPermission { GRANTED, DENIED, DENIED_PERMANENTLY }

sealed interface ShizukuState {
    /** Binder not available: Shizuku is not installed, not started, or just died. */
    data class NotRunning(val managerInstalled: Boolean) : ShizukuState

    /** Shizuku server is reachable. [uid] is 0 (root) or 2000 (adb shell). */
    data class Ready(val version: Int, val uid: Int, val permission: ShizukuPermission) : ShizukuState
}

/**
 * Tracks the Shizuku connection and permission using the official Shizuku API listeners.
 * It only reports what Shizuku reports; file operations are a separate (later) task.
 */
class ShizukuController(private val context: Context) {

    private val _state = MutableStateFlow<ShizukuState>(ShizukuState.NotRunning(managerInstalled = false))
    val state: StateFlow<ShizukuState> = _state.asStateFlow()

    private var started = false

    private val onBinderReceived = Shizuku.OnBinderReceivedListener { refresh() }
    private val onBinderDead = Shizuku.OnBinderDeadListener { refresh() }
    private val onPermissionResult = Shizuku.OnRequestPermissionResultListener { _, _ -> refresh() }

    fun start() {
        if (started) return
        started = true
        Shizuku.addBinderReceivedListenerSticky(onBinderReceived)
        Shizuku.addBinderDeadListener(onBinderDead)
        Shizuku.addRequestPermissionResultListener(onPermissionResult)
        refresh()
    }

    fun stop() {
        if (!started) return
        started = false
        Shizuku.removeBinderReceivedListener(onBinderReceived)
        Shizuku.removeBinderDeadListener(onBinderDead)
        Shizuku.removeRequestPermissionResultListener(onPermissionResult)
    }

    fun refresh() {
        _state.value = readState()
    }

    /** Shows Shizuku's own permission dialog. The outcome arrives through [state]. */
    fun requestPermission() {
        val current = _state.value
        if (current !is ShizukuState.Ready || current.permission == ShizukuPermission.GRANTED) return
        try {
            Shizuku.requestPermission(REQUEST_CODE)
        } catch (e: Exception) {
            Log.w(TAG, "requestPermission failed", e)
        }
    }

    private fun readState(): ShizukuState {
        val installed = isManagerInstalled()
        if (!Shizuku.pingBinder()) return ShizukuState.NotRunning(installed)
        return try {
            val permission = when {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED ->
                    ShizukuPermission.GRANTED
                Shizuku.shouldShowRequestPermissionRationale() ->
                    ShizukuPermission.DENIED_PERMANENTLY
                else -> ShizukuPermission.DENIED
            }
            ShizukuState.Ready(Shizuku.getVersion(), Shizuku.getUid(), permission)
        } catch (e: Exception) {
            Log.w(TAG, "Could not read Shizuku state", e)
            ShizukuState.NotRunning(installed)
        }
    }

    @Suppress("DEPRECATION")
    private fun isManagerInstalled(): Boolean = try {
        context.packageManager.getPackageInfo(MANAGER_PACKAGE, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    private companion object {
        const val TAG = "ShizukuController"
        const val MANAGER_PACKAGE = "moe.shizuku.privileged.api"
        const val REQUEST_CODE = 1001
    }
}
