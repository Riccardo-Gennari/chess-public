package it.ric.chess.domain.repository

import it.ric.chess.domain.model.PlayerInfo

interface PlayerRepository {
    /**
     * Returns information about the currently authenticated Play Games player.
     * Returns null if not authenticated or if the operation fails.
     */
    suspend fun getCurrentPlayerInfo(): PlayerInfo?

    /**
     * Returns information about a specific Play Games player by their ID.
     */
    suspend fun getPlayerInfo(playerId: String): PlayerInfo?
}
