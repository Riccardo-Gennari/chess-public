package it.ric.chess.data.datasource.playgames

import it.ric.chess.domain.model.PlayerInfo

interface PlayGamesDataSource {
    suspend fun getAuthCode(webClientId: String): String?

    suspend fun getPlayerInfo(): PlayerInfo?

    suspend fun getPlayerInfo(playerId: String): PlayerInfo?

    suspend fun signOut()
}
