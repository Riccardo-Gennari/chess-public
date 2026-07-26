package it.ric.chess.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel(
    protected val log: Logger,
) : ViewModel() {
    protected val scope = viewModelScope
    protected val viewModelLog = log.withFixedTag(tag)
    protected val viewModelLoadingState = LoadingState()

    /**
     * Launches a coroutine that updates the [viewModelLoadingState] for its duration.
     */
    protected fun CoroutineScope.launchWhileLoading(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = launchWhileLoading(viewModelLoadingState, context, block)

    /**
     * Launches a coroutine with loading state only if not already loading.
     */
    protected fun CoroutineScope.launchWhileLoadingIfIdle(
        context: CoroutineContext = EmptyCoroutineContext,
        onLaunchSkipped: () -> Unit = ::logCoroutineSkipped,
        block: suspend CoroutineScope.() -> Unit,
    ): Job? = launchWhileLoadingIfIdle(viewModelLoadingState, context, onLaunchSkipped, block)

    /**
     * Launches a coroutine only if not already loading.
     */
    protected fun CoroutineScope.launchIfIdle(
        context: CoroutineContext = EmptyCoroutineContext,
        onLaunchSkipped: () -> Unit = ::logCoroutineSkipped,
        block: suspend CoroutineScope.() -> Unit,
    ): Job? = launchIfIdle(viewModelLoadingState, context, onLaunchSkipped, block)

    protected fun logCoroutineSkipped() = viewModelLog.debug("coroutine launch skipped because loading")
}
