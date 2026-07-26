package it.ric.chess.navigation

import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class Nav3Navigator(
    internal val backStack: SnapshotStateList<Route>,
    private val log: Logger,
) : Navigator {
    private val results = ConcurrentHashMap<Pair<KClass<out RouteWithResult<*>>, Int>, CompletableDeferred<Any?>>()

    override val currentBackStack: List<Route>
        get() = backStack

    override fun backStackFlow() =
        snapshotFlow {
            backStack.toList()
        }

    override fun push(route: Route) {
        synchronized(backStack) {
            if (backStack.lastOrNull() != route) {
                backStack.add(route)
                log.debug(tag, message = "Pushed route: ${route.tag}")
            } else {
                log.debug(tag, message = "Route not pushed since already last on stack: ${route.tag}")
            }
        }
    }

    override suspend fun <R> pushAndAwaitResult(route: RouteWithResult<R>): R {
        val key = route::class to route.requestId
        val deferred = results.getOrPut(key) { CompletableDeferred() }
        push(route)
        return try {
            @Suppress("UNCHECKED_CAST")
            deferred.await() as R
        } finally {
            results.remove(key)
        }
    }

    override fun <R> emitResult(
        route: RouteWithResult<R>,
        value: R,
    ) {
        val key = route::class to route.requestId
        val deferred = results.getOrPut(key) { CompletableDeferred() }
        deferred.complete(value)
    }

    override fun pop(route: Route): Route? {
        synchronized(backStack) {
            return if (backStack.size > 1) {
                if (backStack.remove(route)) {
                    log.debug(tag, message = "Popped route: ${route.tag}")
                    route
                } else {
                    log.debug(tag, message = "Route not popped since not on stack: ${route.tag}")
                    null
                }
            } else {
                log.debug(tag, message = "Stack is empty, cannot pop")
                null
            }
        }
    }

    override fun pop(): Route? {
        synchronized(backStack) {
            return if (backStack.size > 1) {
                backStack
                    .removeLastOrNull()
                    ?.also { log.debug(tag, message = "Popped route: ${it.tag}") }
            } else {
                log.debug(tag, message = "Stack is empty, cannot pop")
                null
            }
        }
    }

    /**
     * Pops routes from a stack until the specified 'route' is found and popped.
     *
     * @param route The destination route to pop until.
     * @return A list of all routes that were popped, including the destination 'route'.
     */
    override fun popUntil(route: Route): List<Route> {
        synchronized(backStack) {
            if (backStack.contains(route).not()) return emptyList()

            val poppedList = mutableListOf<Route>()
            while (true) {
                val popped = popInternal() ?: break
                poppedList.add(popped)
                if (popped == route) {
                    break
                }
            }
            return poppedList
        }
    }

    private fun popInternal(): Route? =
        if (backStack.size > 1) {
            backStack
                .removeLastOrNull()
                ?.also { log.debug(tag, message = "Popped route: ${it.tag}") }
        } else {
            log.debug(tag, message = "Stack is empty, cannot pop")
            null
        }

    override fun clearAndPush(route: Route) {
        synchronized(backStack) {
            log.debug(tag, message = "Clearing stack")
            backStack.clear()
            push(route)
        }
    }
}
