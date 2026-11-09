package com.filex.studio.core

import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.AccessDeniedException as NioAccessDenied
import java.nio.file.FileAlreadyExistsException as NioFileExists
import java.nio.file.NoSuchFileException as NioNoSuchFile
import kotlinx.coroutines.CancellationException

/** Every failure the storage layer can report. Messages are user-presentable. */
sealed interface FxError {
    val message: String

    data class Io(override val message: String, val cause: Throwable? = null) : FxError
    data class PermissionDenied(override val message: String) : FxError
    data class NotFound(override val message: String) : FxError
    data class AlreadyExists(override val message: String) : FxError
    data class InvalidName(override val message: String) : FxError
    data class Unsupported(override val message: String) : FxError
    data class BackendUnavailable(override val message: String) : FxError
}

/** Result type used by all storage/cloud operations. The OS decides success, never the UI. */
sealed interface FxResult<out T> {
    data class Success<out T>(val value: T) : FxResult<T>
    data class Failure(val error: FxError) : FxResult<Nothing>
}

fun Throwable.toFxError(): FxError = when (this) {
    is SecurityException -> FxError.PermissionDenied(message ?: "Permission denied")
    is NioAccessDenied -> FxError.PermissionDenied("Access denied: ${message ?: ""}".trim())
    is NioNoSuchFile -> FxError.NotFound("Not found: ${message ?: ""}".trim())
    is NioFileExists -> FxError.AlreadyExists("Already exists: ${message ?: ""}".trim())
    is FileNotFoundException -> FxError.NotFound(message ?: "File not found")
    is IOException -> FxError.Io(message ?: "I/O error", this)
    else -> FxError.Io("Unexpected error (${javaClass.simpleName}): ${message ?: ""}".trim(), this)
}

/** Runs [block], converting exceptions into [FxResult.Failure]. Coroutine cancellation is rethrown. */
inline fun <T> fxTry(block: () -> FxResult<T>): FxResult<T> = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    FxResult.Failure(e.toFxError())
}

inline fun <T> fxRunCatching(block: () -> T): FxResult<T> = fxTry { FxResult.Success(block()) }

/** Returns an error if [name] cannot be used as a single file or folder name. */
fun validateFileName(name: String): FxError.InvalidName? = when {
    name.isBlank() -> FxError.InvalidName("Name must not be empty")
    name == "." || name == ".." -> FxError.InvalidName("\"$name\" is not a valid name")
    name.contains('/') || name.contains('\u0000') -> FxError.InvalidName("Name must not contain '/'")
    name.length > 255 -> FxError.InvalidName("Name is longer than 255 characters")
    else -> null
}
