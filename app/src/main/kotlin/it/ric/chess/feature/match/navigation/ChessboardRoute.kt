package it.ric.chess.feature.match.navigation

import it.ric.chess.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class ChessboardRoute(
    val matchId: String? = null,
) : Route
