package it.ric.chess.navigation

import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * Base class for routes that expect a result of type [T].
 * Provides a unique [requestId] used by the [Navigator] to match results.
 */
@Serializable
abstract class RouteWithResult<T> : Route {
    /**
     * Unique identifier for this specific request.
     * Generated randomly to avoid collisions between identical routes.
     */
    val requestId: Int = Random.nextInt()
}
