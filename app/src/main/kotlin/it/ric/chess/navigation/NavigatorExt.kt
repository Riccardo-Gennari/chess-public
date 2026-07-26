package it.ric.chess.navigation

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach

suspend fun Navigator.repeatOnActive(
    route: Route,
    skipInitial: Boolean = true,
    action: suspend (backStack: List<Route>) -> Unit,
) {
    backStackFlow()
        .filter { backStack -> backStack.lastOrNull() == route }
        .let { flow ->
            if (skipInitial) {
                flow.drop(1)
            } else {
                flow
            }
        }.onEach(action)
        .collect()
}

val Navigator.lastRoute get() =
    currentBackStack.lastOrNull()
