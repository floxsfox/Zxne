package com.filex.studio.ui.components

import com.filex.studio.data.shizuku.ShizukuPermission
import com.filex.studio.data.shizuku.ShizukuState

fun ShizukuState.summary(): String = when (this) {
    is ShizukuState.NotRunning ->
        if (managerInstalled) "Installed, not running" else "Not installed"
    is ShizukuState.Ready -> {
        val who = if (uid == 0) "root" else "adb"
        val perm = when (permission) {
            ShizukuPermission.GRANTED -> "authorized"
            ShizukuPermission.DENIED -> "permission needed"
            ShizukuPermission.DENIED_PERMANENTLY -> "permission denied"
        }
        "Running v$version ($who), $perm"
    }
}

fun ShizukuState.isAuthorized(): Boolean =
    this is ShizukuState.Ready && permission == ShizukuPermission.GRANTED
