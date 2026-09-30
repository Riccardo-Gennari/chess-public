package it.ric.chess.navigation

/**
 * Narrow interface for components that only need to return data
 * back to a caller after a screen is popped.
 */
interface ResultEmitter {
    /**
     * Emits a result [value] for a given [route].
     */
    fun <R> emitResult(
        route: RouteWithResult<R>,
        value: R,
    )
}
