package it.ric.chess.core.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches a coroutine that updates the [loadingState] for its duration.
 */
fun CoroutineScope.launchWhileLoading(
    loadingState: LoadingState,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    loadingState.startLoading()
    return launch(context, block = block).also {
        it.invokeOnCompletion { loadingState.stopLoading() }
    }
}

/**
 * Launches a coroutine with loading state only if not already loading.
 */
fun CoroutineScope.launchWhileLoadingIfIdle(
    loadingState: LoadingState,
    context: CoroutineContext = EmptyCoroutineContext,
    onLaunchSkipped: () -> Unit = {},
    block: suspend CoroutineScope.() -> Unit,
): Job? {
    if (loadingState.isCurrentlyLoading) {
        onLaunchSkipped()
        return null
    }
    return launchWhileLoading(loadingState, context, block)
}

/**
 * Launches a coroutine only if not already loading.
 */
fun CoroutineScope.launchIfIdle(
    loadingState: LoadingState,
    context: CoroutineContext = EmptyCoroutineContext,
    onLaunchSkipped: () -> Unit = {},
    block: suspend CoroutineScope.() -> Unit,
): Job? {
    if (loadingState.isCurrentlyLoading) {
        onLaunchSkipped()
        return null
    }
    return launch(context, block = block)
}
