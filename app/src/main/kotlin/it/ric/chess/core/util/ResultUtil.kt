package it.ric.chess.core.util

import kotlin.coroutines.cancellation.CancellationException

/**
 * Similar to [runCatching], but rethrows [CancellationException] to ensure coroutine cancellation is preserved.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T> runCatchingCancelable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
