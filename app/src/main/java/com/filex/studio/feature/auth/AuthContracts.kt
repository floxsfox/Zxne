package com.filex.studio.feature.auth

import com.filex.studio.core.FxResult
import kotlinx.coroutines.flow.StateFlow

/**
 * Contracts only (task A16); implementations arrive in B24-B26. Secrets must never be stored
 * in plaintext: the vault keeps a salted hash, with key material protected by Android Keystore.
 */
enum class LockMethod { NONE, PIN, PASSWORD, PATTERN }

sealed interface AuthState {
    data object Locked : AuthState
    data object Unlocked : AuthState
}

interface CredentialVault {
    suspend fun hasCredential(method: LockMethod): Boolean
    suspend fun setCredential(method: LockMethod, secret: CharArray): FxResult<Unit>
    suspend fun verify(method: LockMethod, secret: CharArray): FxResult<Boolean>
    suspend fun clear(): FxResult<Unit>
}

interface AuthManager {
    val state: StateFlow<AuthState>
    fun lock()
    suspend fun unlock(method: LockMethod, secret: CharArray): FxResult<Unit>
}
