package it.ric.chess.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model for a chess match.
 */
@Serializable
data class Match(
    val id: String,
    val name: String,
    val whitePlayerId: String?,
    val blackPlayerId: String?,
    val whitePgsId: String? = null,
    val blackPgsId: String? = null,
    val fen: String,
    val status: MatchStatus,
    val lastUpdate: Long,
)
