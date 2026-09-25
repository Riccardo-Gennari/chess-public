package it.ric.chess.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

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

/**
 * Main navigation interface for the application.
 * It manages the backstack of [Route]s and provides methods for pushing, popping,
 * and awaiting results from screens.
 */
interface Navigator : ResultEmitter {
    /** The current backstack of routes. */
    val currentBackStack: List<Route>

    /** Returns a flow of the backstack. */
    fun backStackFlow(): Flow<List<Route>>

    /** Clears the entire backstack and pushes the given [route]. */
    fun clearAndPush(route: Route)

    /** Pops a specific [route] from the backstack if it exists. */
    fun pop(route: Route): Route?

    /** Pops the top route from the backstack. */
    fun pop(): Route?

    /** Pops all routes until the given [route] is reached. */
    fun popUntil(route: Route): List<Route>

    /** Pushes a new [route] onto the backstack. */
    fun push(route: Route)

    /**
     * Pushes a [route] that is expected to return a result of type [R]
     * and suspends until the result is emitted via [emitResult].
     */
    suspend fun <R> pushAndAwaitResult(route: RouteWithResult<R>): R

    companion object {
        /** Returns a no-op implementation of [Navigator]. */
        fun noOp(): Navigator =
            object : Navigator {
                override val currentBackStack: List<Route> = emptyList()

                override fun backStackFlow(): Flow<List<Route>> = emptyFlow()

                override fun clearAndPush(route: Route) {}

                override fun pop(route: Route): Route? = null

                override fun pop(): Route? = null

                override fun popUntil(route: Route): List<Route> = emptyList()

                override fun push(route: Route) {}

                override suspend fun <R> pushAndAwaitResult(route: RouteWithResult<R>): R =
                    throw UnsupportedOperationException()

                override fun <R> emitResult(
                    route: RouteWithResult<R>,
                    value: R,
                ) {}
            }
    }
}
