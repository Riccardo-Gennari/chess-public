package it.ric.chess.core.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

class LoadingState(
    initialLoadingJobCount: Int = 0,
) {
    private val loadingJobCount = AtomicInteger(initialLoadingJobCount)
    private val _isLoading = MutableStateFlow(initialLoadingJobCount > 0)

    /** [Flow] emitting true when at least one background operation is in progress. */
    val isLoading = _isLoading.asStateFlow()

    /** Current loading state. */
    val isCurrentlyLoading get() = isLoading.value

    /** Increments the loading counter and updates [isLoading] if necessary. */
    fun startLoading() {
        if (loadingJobCount.getAndIncrement() == 0) {
            _isLoading.value = true
        }
    }

    /** Decrements the loading counter and updates [isLoading] if necessary. */
    fun stopLoading() {
        if (loadingJobCount.decrementAndGet() == 0) {
            _isLoading.value = false
        }
    }

    /**
     * Wraps a **synchronous, blocking** block of code with a loading indicator.
     */
    inline fun <T> whileLoading(block: () -> T): T {
        startLoading()
        try {
            return block()
        } finally {
            // finally guarantees that stopLoading is called, even if 'block' throws an exception.
            stopLoading()
        }
    }

    companion object {
        val Loading = LoadingState(initialLoadingJobCount = 1)
        val Idle = LoadingState(initialLoadingJobCount = 0)
    }
}
